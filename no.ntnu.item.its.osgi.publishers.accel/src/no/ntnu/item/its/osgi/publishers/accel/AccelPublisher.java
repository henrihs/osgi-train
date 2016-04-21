package no.ntnu.item.its.osgi.publishers.accel;

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
import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.PublisherService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.common.servicetrackers.SchedulerTrackerCustomizer;

public class AccelPublisher implements PublisherService {
	
	public static final long SCHEDULE_PERIOD = 50;
	private static final PublisherType TYPE = PublisherType.ACCEL;
	private static final float ALPHA = 0.15f;

	private Function<Void, Void> sensorReading;
	private double[] previous;


	public AccelPublisher() {		
		sensorReading = getSensorReadingFunc();
		Runnable runnableSensorReading = new Runnable() {

			@Override
			public void run() {
				sensorReading.apply(null);
			}
		};
		
		ServiceTracker<SensorSchedulerService, Object> schedulerTracker = 
				new ServiceTracker<SensorSchedulerService, Object>(
						AccelPubActivator.getContext(), 
						SensorSchedulerService.class, 
						new SchedulerTrackerCustomizer(
								AccelPubActivator.getContext(), 
								runnableSensorReading, 
								SCHEDULE_PERIOD));
		schedulerTracker.open();
		
		Dictionary<String, Object> publiserServiceProps = new Hashtable<String, Object>();
		publiserServiceProps.put(PublisherType.class.getSimpleName(), TYPE);
		AccelPubActivator.getContext().registerService(PublisherService.class, this, publiserServiceProps);
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
			
			@Override
			public Void apply(Void t) {
				try {
					AccelerationControllerService acs = (AccelerationControllerService) 
							AccelPubActivator.accelControllerTracker.getService();
					int[] accelDataBits = acs.getCalibratedData();
					double[] accelData = convertToStandardUnits(accelDataBits);
					accelData = lowPass(accelData, previous);
					publish(accelData);
					
				} catch (Exception e) {
					((LogService)AccelPubActivator.logServiceTracker.getService()).log(
							LogService.LOG_ERROR, 
							"Faulted while reading from sensor service", e);
					try {
						AccelPubActivator.getContext().getBundle().stop();
					} catch (BundleException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				return t;
			}
		};
	}
	
	private void publish(double[] accelData) {
		if (!AccelPubActivator.eventAdminTracker.isEmpty()) {
			Event accelEvent = createEvent(accelData);			
			((EventAdmin) AccelPubActivator.eventAdminTracker.getService()).postEvent(accelEvent);
			previous = accelData;
		}
		
		else if (!AccelPubActivator.logServiceTracker.isEmpty()) {
		((LogService) AccelPubActivator.logServiceTracker.getService()).log(
				LogService.LOG_DEBUG, 
				"Failed to publish event, no EventAdmin service available!");
		}
	}

	private Event createEvent(double[] accelData) {
		Map<String, Object> properties = new Hashtable<>();
		properties.put(
				AccelerationControllerService.TIMESTAMP_KEY, 
				System.nanoTime());
		properties.put(
				AccelerationControllerService.X_DATA_KEY, 
				accelData[0]);
		properties.put(
				AccelerationControllerService.Y_DATA_KEY, 
				accelData[1]);
		properties.put(
				AccelerationControllerService.Z_DATA_KEY, 
				accelData[2]);
		Event accelEvent = new Event(AccelerationControllerService.EVENT_TOPIC, properties);
		return accelEvent;
	}
	
	private double[] convertToStandardUnits(int[] accelDataFromBits) {
		double[] accelData = new double[accelDataFromBits.length];
		for (int i = 0; i < accelDataFromBits.length; i++) {
			accelData[i] = accelDataFromBits[i]/1024.0*AccelerationControllerService.GRAVITATIONAL_RATIO;
			
		}
		
		return accelData;
	}
	
	private double[] lowPass( double[] input, double[] output ) {
	    if ( output == null ) return input;
	     
	    for ( int i=0; i<input.length; i++ ) {
	        output[i] = output[i] + ALPHA * (input[i] - output[i]);
	    }
	    return output;
	}

}
