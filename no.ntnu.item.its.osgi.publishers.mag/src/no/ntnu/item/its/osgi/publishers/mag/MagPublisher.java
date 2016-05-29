package no.ntnu.item.its.osgi.publishers.mag;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.common.enums.PublisherType;
import no.ntnu.item.its.osgi.common.enums.Status;
import no.ntnu.item.its.osgi.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.common.interfaces.MagControllerService;
import no.ntnu.item.its.osgi.common.interfaces.PublisherService;
import no.ntnu.item.its.osgi.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.common.servicetrackers.SchedulerTrackerCustomizer;

public class MagPublisher implements PublisherService {

	public static final PublisherType TYPE = PublisherType.MAG;
	
	public static final long SCHEDULE_PERIOD = 200;
	public static final double CALIBRATION_X_AXIS = 65;
	public static final double CALIBRATION_Y_AXIS = -85;

	private Function<Void, Void> sensorReading;


	public MagPublisher() {		
		sensorReading = getSensorReadingFunc();
		Runnable runnableSensorReading = new Runnable() {

			@Override
			public void run() {
				sensorReading.apply(null);
			}
		};
		
		ServiceTracker<SensorSchedulerService, Object> schedulerTracker = 
				new ServiceTracker<SensorSchedulerService, Object>(
						MagPubActivator.getContext(), 
						SensorSchedulerService.class, 
						new SchedulerTrackerCustomizer(
								MagPubActivator.getContext(), 
								runnableSensorReading, 
								SCHEDULE_PERIOD));
		schedulerTracker.open();
		
		Dictionary<String, Object> publiserServiceProps = new Hashtable<String, Object>();
		publiserServiceProps.put(PublisherType.class.getSimpleName(), TYPE);
		MagPubActivator.getContext().registerService(PublisherService.class, this, publiserServiceProps);
	}
	
	protected void stop() {
		sensorReading = null;
	}
	
	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PublisherType getType() {
		return TYPE;
	}
	
	private Function<Void, Void> getSensorReadingFunc() {
		return new Function<Void, Void>() {
			private int successiveExceptions = 0;
			
			@Override
			public Void apply(Void t) {
				try {
					MagControllerService mcs = (MagControllerService) 
							MagPubActivator.magControllerTracker.getService();
					
					int[] magData = mcs.getRawData();
					double[] siMagData = convertToSIUnits(magData);
					siMagData = applyCalibration(siMagData);
					double heading = calculateHeading(siMagData);
					successiveExceptions = 0;
					publish(siMagData, heading);
				} catch (Exception e) {
					if (e.getClass().equals(SensorCommunicationException.class) && successiveExceptions++ < 4) {
						return t;
					}
					
					((LogService)MagPubActivator.logServiceTracker.getService()).log(
							LogService.LOG_ERROR, 
							"Faulted while reading from sensor service", e);
					try {
						MagPubActivator.getContext().getBundle().stop();
					} catch (BundleException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				return t;
			}

		};
	}
	
	private double[] convertToSIUnits(int[] magData) {
		return new double[] {
			magData[0] / 10.0,
			magData[1] / 10.0,
			magData[2] / 10.0
		};
	}
	
	private double[] applyCalibration(double[] magDataXyz) {
		magDataXyz[0] = magDataXyz[0] + CALIBRATION_X_AXIS;
		magDataXyz[1] = magDataXyz[1] + CALIBRATION_Y_AXIS;
		
		return magDataXyz;
	}
	
	private double calculateHeading(double[] magDataXyz) {
		double x = magDataXyz[0];
		double y = magDataXyz[1];
		double h = 0;
		
		if (y > 0) {
			h = 90 - Math.atan(x/y)*180/Math.PI;
		} else if (y < 0) {
			h = 270 - Math.atan(x/y)*180/Math.PI;
		} else if (x > 0) {
			h = 0;
		} else if (x < 0) {
			h = 180;
		}
		
		return h;
		
//		return Math.atan(x/y);
	}
	
	private void publish(double[] magData, double heading) {
		if (!MagPubActivator.eventAdminTracker.isEmpty()) {
			Event magEvent = createEvent(magData, heading);			
			((EventAdmin) MagPubActivator.eventAdminTracker.getService()).postEvent(magEvent);
		}
		
		else if (!MagPubActivator.logServiceTracker.isEmpty()) {
		((LogService) MagPubActivator.logServiceTracker.getService()).log(
				LogService.LOG_DEBUG, 
				"Failed to publish event, no EventAdmin service available!");
		}
	}

	private Event createEvent(double[] magData, double heading) {
		Map<String, Object> properties = new Hashtable<>();
		properties.put(
				MagControllerService.TIMESTAMP_KEY, 
				System.nanoTime());
		properties.put(
				MagControllerService.X_DATA_KEY, 
				magData[0]);
		properties.put(
				MagControllerService.Y_DATA_KEY, 
				magData[1]);
		properties.put(
				MagControllerService.Z_DATA_KEY, 
				magData[2]);
		properties.put(
				MagControllerService.HEADING_KEY,
				heading);
		Event accelEvent = new Event(MagControllerService.EVENT_TOPIC, properties);
		return accelEvent;
	}

}
