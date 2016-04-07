package no.ntnu.item.its.osgi.publishers.mifare;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.common.servicetrackers.SchedulerTrackerCustomizer;
import no.ntnu.item.its.osgi.sensors.common.*;
import no.ntnu.item.its.osgi.sensors.common.enums.*;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;

public class MifarePublisher implements BundleActivator {

	public static final long SCHEDULE_PERIOD = 1000;

	private static BundleContext context;
	private ServiceTracker<EventAdmin, Object> eventAdminTracker;
	private ServiceTracker<LogService, Object> logServiceTracker;
	private ServiceTracker<MifareControllerService, Object> mifareControllerTracker;

	private Function<Void, Void> sensorReading; 
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		MifarePublisher.context = bundleContext;

		logServiceTracker = new ServiceTracker<>(bundleContext, LogService.class, null);
		logServiceTracker.open();
		eventAdminTracker = new ServiceTracker<>(bundleContext, EventAdmin.class, null);
		eventAdminTracker.open();
		mifareControllerTracker = new ServiceTracker<>(
				bundleContext, 
				MifareControllerService.class, 
				null);
		mifareControllerTracker.open();

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
						new SchedulerTrackerCustomizer(bundleContext, runnableSensorReading, SCHEDULE_PERIOD));
		schedulerTracker.open();


	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		sensorReading = null;
		MifarePublisher.context = null;
	}
	
	private Function<Void, Void> getSensorReadingFunc() {
		return new Function<Void, Void>() {

			@Override
			public Void apply(Void t) {
				String content; 
				try {
					MifareControllerService s = (MifareControllerService)mifareControllerTracker.getService();
					content = s.read(42, new MifareKeyRing(MifareKeyType.A));
					if (!content.isEmpty()) {
						publish(content);
					}
				} catch (SensorCommunicationException e) {
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

	private void publish(String content){
		EventAdmin ea = (EventAdmin) eventAdminTracker.getService();
		if (ea != null) {
			Map<String, String> properties = new Hashtable<String, String>();
			properties.put(MifareControllerService.LOC_ID_KEY, content);
			Event mifareEvent;
			try {
				mifareEvent = new Event(MifareControllerService.EVENT_TOPIC, properties);
				((EventAdmin) eventAdminTracker.getService()).postEvent(mifareEvent);
			} catch (Exception e) {				
				e.printStackTrace();
			}

		}

		else if (!logServiceTracker.isEmpty()) {
			((LogService) logServiceTracker.getService()).log(LogService.LOG_INFO, "Failed to publish event, no EventAdmin service available!");
		}
	}
}
