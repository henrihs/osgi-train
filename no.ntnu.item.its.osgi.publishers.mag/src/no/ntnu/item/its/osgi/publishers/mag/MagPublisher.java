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

import no.ntnu.item.its.osgi.sensors.common.enums.PublisherType;
import no.ntnu.item.its.osgi.sensors.common.enums.Status;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.MagControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.PublisherService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.common.servicetrackers.SchedulerTrackerCustomizer;

public class MagPublisher implements PublisherService {

	public static final PublisherType TYPE = PublisherType.MAG;
	
	public static final long SCHEDULE_PERIOD = 50;

	private Function<Void, Void> sensorReading;
	private double[] previous;


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
					
					double[] magData = mcs.getRawData();
					double heading = calculateHeading(magData);
					successiveExceptions = 0;
					publish(magData, heading);
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
	
	private double calculateHeading(double[] magDataXyz) {
		return 180*Math.atan2(magDataXyz[1],magDataXyz[0])/Math.PI;
	}
	
	private void publish(double[] magData, double heading) {
		if (!MagPubActivator.eventAdminTracker.isEmpty()) {
			Event magEvent = createEvent(magData, heading);			
			((EventAdmin) MagPubActivator.eventAdminTracker.getService()).postEvent(magEvent);
			previous = magData;
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
