package no.ntnu.item.its.osgi.publishers.accel;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.common.servicetrackers.SchedulerTrackerCustomizer;

public class AccelPublisher implements BundleActivator {
	
	public static final long SCHEDULE_PERIOD = 1000;

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}
	
	private ServiceTracker<LogService, Object> logServiceTracker;
	private ServiceTracker<EventAdmin, Object> eventAdminTracker;
	private ServiceTracker<AccelerationControllerService, Object> accelControllerTracker;

	private Function<Void, Void> sensorReading;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		AccelPublisher.context = bundleContext;
		
		logServiceTracker = new ServiceTracker<>(bundleContext, LogService.class, null);
		logServiceTracker.open();
		eventAdminTracker = new ServiceTracker<>(bundleContext, EventAdmin.class, null);
		eventAdminTracker.open();
		accelControllerTracker = new ServiceTracker<>(
				bundleContext, 
				AccelerationControllerService.class, 
				null);
		accelControllerTracker.open();
		
		sensorReading = getSensorReadingFunc();
		Runnable runnableSensorReading = new Runnable() {

			@Override
			public void run() {
				sensorReading.apply(null);
			}
		};

		ServiceTracker<SensorSchedulerService, Object> schedulerTracker = 
				new ServiceTracker<SensorSchedulerService, Object>(
						bundleContext, 
						SensorSchedulerService.class, 
						new SchedulerTrackerCustomizer(
								bundleContext, 
								runnableSensorReading, 
								SCHEDULE_PERIOD));
		schedulerTracker.open();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		sensorReading = null;
		AccelPublisher.context = null;
	}
	
	private Function<Void, Void> getSensorReadingFunc() {
		return new Function<Void, Void>() {
			
			@Override
			public Void apply(Void t) {
				try {
					AccelerationControllerService acs = (AccelerationControllerService) 
							accelControllerTracker.getService();
					int[] accelData = acs.getCalibratedData();
					publish(accelData);
					
				} catch (Exception e) {
					((LogService)logServiceTracker.getService()).log(
							LogService.LOG_ERROR, 
							"Faulted while reading from sensor service", e);
					try {
						context.getBundle().stop();
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
		if (!eventAdminTracker.isEmpty()) {
			Map<String, Object> properties = new Hashtable<>();
			properties.put(AccelerationControllerService.X_DATA_KEY, accelData[0]/1024.0*1000);
			properties.put(AccelerationControllerService.Y_DATA_KEY, accelData[1]/1024.0*1000);
			properties.put(AccelerationControllerService.Z_DATA_KEY, accelData[2]/1024.0*1000);
			Event accelEvent = new Event(AccelerationControllerService.EVENT_TOPIC, properties);			
			((EventAdmin) eventAdminTracker.getService()).postEvent(accelEvent);
		}
		
		else if (!logServiceTracker.isEmpty()) {
		((LogService) logServiceTracker.getService()).log(
				LogService.LOG_DEBUG, 
				"Failed to publish event, no EventAdmin service available!");
		}
	}


}
