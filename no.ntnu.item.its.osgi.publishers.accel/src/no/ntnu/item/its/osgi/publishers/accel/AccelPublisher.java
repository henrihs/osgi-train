package no.ntnu.item.its.osgi.publishers.accel;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.sensors.common.enums.Status;
import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.PublisherService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.common.servicetrackers.SchedulerTrackerCustomizer;

public class AccelPublisher implements PublisherService {
	
	public static final long SCHEDULE_PERIOD = 25;

	private Function<Void, Void> sensorReading;

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
	}
	
	protected void stop() {
		sensorReading = null;
	}
	
	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Function<Void, Void> getSensorReadingFunc() {
		return new Function<Void, Void>() {
			
			@Override
			public Void apply(Void t) {
				try {
					AccelerationControllerService acs = (AccelerationControllerService) 
							AccelPubActivator.accelControllerTracker.getService();
					int[] accelData = acs.getCalibratedData();
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
	
	private void publish(int[] accelData) {
		if (!AccelPubActivator.eventAdminTracker.isEmpty()) {
			Event accelEvent = createEvent(accelData);			
			((EventAdmin) AccelPubActivator.eventAdminTracker.getService()).postEvent(accelEvent);
		}
		
		else if (!AccelPubActivator.logServiceTracker.isEmpty()) {
		((LogService) AccelPubActivator.logServiceTracker.getService()).log(
				LogService.LOG_DEBUG, 
				"Failed to publish event, no EventAdmin service available!");
		}
	}

	private Event createEvent(int[] accelData) {
		Map<String, Object> properties = new Hashtable<>();
		properties.put(
				AccelerationControllerService.TIMESTAMP_KEY, 
				System.nanoTime());
		properties.put(
				AccelerationControllerService.X_DATA_KEY, 
				accelData[0]/1024.0*AccelerationControllerService.GRAVITATIONAL_RATIO);
		properties.put(
				AccelerationControllerService.Y_DATA_KEY, 
				accelData[1]/1024.0*AccelerationControllerService.GRAVITATIONAL_RATIO);
		properties.put(
				AccelerationControllerService.Z_DATA_KEY, 
				accelData[2]/1024.0*AccelerationControllerService.GRAVITATIONAL_RATIO);
		Event accelEvent = new Event(AccelerationControllerService.EVENT_TOPIC, properties);
		return accelEvent;
	}
	
	

}
