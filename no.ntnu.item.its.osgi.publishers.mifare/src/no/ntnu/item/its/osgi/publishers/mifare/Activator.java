package no.ntnu.item.its.osgi.publishers.mifare;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareController;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.common.servicetrackers.SchedulerTrackerCustomizer;
import no.ntnu.item.its.osgi.sensors.mifare.*;
import no.ntnu.item.its.osgi.sensors.common.*;
import no.ntnu.item.its.osgi.sensors.common.enums.*;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;

public class Activator implements BundleActivator {
	
	public static final long SCHEDULE_PERIOD = 1000;

	private static BundleContext context;
	private ServiceTracker<EventAdmin, Object> eventAdminTracker;
	private ServiceTracker<LogService, Object> logServiceTracker;

	private Runnable runnableSensorReading;


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
		
		logServiceTracker = new ServiceTracker<>(bundleContext, LogService.class, null);
		logServiceTracker.open();
		eventAdminTracker = new ServiceTracker<>(bundleContext, EventAdmin.class, null);
		eventAdminTracker.open();
		
		runnableSensorReading = new Runnable() {
			
			@Override
			public void run() {
				String content;
				try {
					content = mc.read(42, new MifareKeyRing(MifareKeyType.A));
					if (!content.isEmpty()) {
						publish(content);
					}
				} catch (SensorCommunicationException e) {
					return;
				}
				
			}
		};
		
		ServiceTracker<SensorSchedulerService, Object> schedulerTracker = 
				new ServiceTracker<SensorSchedulerService, Object>(
						bundleContext, 
						SensorSchedulerService.class, 
						new SchedulerTrackerCustomizer(bundleContext, runnableSensorReading, SCHEDULE_PERIOD));
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
		EventAdmin ea = (EventAdmin) eventAdminTracker.getService();
		if (ea != null) {
			Map<String, String> properties = new Hashtable<String, String>();
			properties.put(MifareController.LOC_ID_KEY, content);
			Event mifareEvent;
			try {
				mifareEvent = new Event(MifareController.EVENT_TOPIC, properties);
				((EventAdmin) eventAdminTracker.getService()).sendEvent(mifareEvent);
			} catch (Exception e) {				
				e.printStackTrace();
			}
			
		}
		
		else if (!logServiceTracker.isEmpty()) {
			((LogService) logServiceTracker.getService()).log(LogService.LOG_INFO, "Failed to publish event, no EventAdmin service available!");
		}
	}

}
