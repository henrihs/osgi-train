package no.ntnu.item.its.osgi.publishers.mifare;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareController;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.mifare.*;
import no.ntnu.item.its.osgi.sensors.common.*;
import no.ntnu.item.its.osgi.sensors.common.enums.*;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;

public class Activator implements BundleActivator {
	
	public static final long SCHEDULE_PERIOD = 1000;

	private static BundleContext context;
	private ServiceReference<EventAdmin> eventAdminRef;

	private Runnable runnableSensorReading;

	private ServiceReference<LogService> logRef;

	private MifareController mc;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		mc = MifareControllerFactory.getInstance();
		
		logRef = bundleContext.getServiceReference(LogService.class);
		eventAdminRef = bundleContext.getServiceReference(EventAdmin.class);
		
		ServiceTracker<SensorSchedulerService, Runnable> schedulerTracker = 
				new ServiceTracker<SensorSchedulerService, Runnable>(
						bundleContext, 
						SensorSchedulerService.class, 
						new SchedulerTrackerCustomizer());
		schedulerTracker.open();
		
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		runnableSensorReading = null;
//		SensorSchedulerService scheduler = bundleContext.getService(schedulerRef);
//		if (scheduler != null) {
//			scheduler.remove(runnableSensorReading);
//		}
		
		Activator.context = null;
	}
	
	private void publish(String content){
		if (eventAdminRef != null) {
			EventAdmin eventAdmin = getContext().getService(eventAdminRef);
			Dictionary<String, String> properties = new Hashtable<>();
			properties.put(MifareController.LOC_ID_KEY, content);
			Event mifareEvent = new Event(MifareController.EVENT_TOPIC, properties);	
			eventAdmin.postEvent(mifareEvent);
			if (logRef != null) {
				context.getService(logRef).log(
						LogService.LOG_DEBUG, "Posted event to EventAdmin");
			}
			
		}
		
		else if (logRef != null) {
			context.getService(logRef).log(LogService.LOG_INFO, "Failed to publish event, no EventAdmin service available!");
		}
	}
	
	private class SchedulerTrackerCustomizer implements 
		ServiceTrackerCustomizer<SensorSchedulerService, Runnable> {
		
		@Override
		public Runnable addingService(ServiceReference<SensorSchedulerService> arg0) {
			runnableSensorReading = new Runnable() {
				
				@Override
				public void run() {
					String content;
					try {
						content = mc.read(42, new MifareKeyRing(MifareKeyType.A));
					} catch (SensorCommunicationException e) {
						return;
					}
					
					if (!content.isEmpty()) {
						publish(content);
					}
				}
			};
			
			SensorSchedulerService scheduler = context.getService(arg0);
			scheduler.add(runnableSensorReading, SCHEDULE_PERIOD);
			if (logRef != null) {
				context.getService(logRef).log(
						LogService.LOG_INFO, 
						String.format("Executing periodic sensor readings via %s", 
								arg0.getBundle().getSymbolicName()));
			}
			
			return null;
		}

		@Override
		public void modifiedService(ServiceReference<SensorSchedulerService> arg0, Runnable arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removedService(ServiceReference<SensorSchedulerService> arg0, Runnable arg1) {
			if (logRef != null) {
				context.getService(logRef).log(
						LogService.LOG_WARNING, 
						"No longer executing sensor readings, scheduling service is down!");
			}
		}
	}

}
