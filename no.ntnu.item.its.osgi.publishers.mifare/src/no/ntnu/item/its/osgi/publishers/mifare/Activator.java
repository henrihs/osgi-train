package no.ntnu.item.its.osgi.publishers.mifare;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareController;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.mifare.*;
import no.ntnu.item.its.osgi.sensors.common.*;
import no.ntnu.item.its.osgi.sensors.common.enums.*;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;

public class Activator implements BundleActivator {
	
	public static final long SCHEDULE_PERIOD = 500;

	private static BundleContext context;
	private ServiceReference<SensorSchedulerService> schedulerRef;
	private ServiceReference<EventAdmin> eventAdminRef;

	private Runnable runnableSensorReading;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		MifareController mc = MifareControllerFactory.getInstance();
		MifareKeyRing keyRing = new MifareKeyRing(MifareKeyType.A);
		runnableSensorReading = new Runnable() {
			
			@Override
			public void run() {
				String content;
				try {
					content = mc.read(42,keyRing);
				} catch (SensorCommunicationException e) {
					return;
				}
				
				if (!content.isEmpty()) {
					publish(content);
				}
			}
		};
		
		schedulerRef = bundleContext.getServiceReference(SensorSchedulerService.class);
		eventAdminRef = bundleContext.getServiceReference(EventAdmin.class);
		
		SensorSchedulerService scheduler = bundleContext.getService(schedulerRef);
		if (scheduler != null) {
			scheduler.add(runnableSensorReading, SCHEDULE_PERIOD);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		SensorSchedulerService scheduler = bundleContext.getService(schedulerRef);
		if (scheduler != null) {
			scheduler.remove(runnableSensorReading);
		}
		
		Activator.context = null;
	}
	
	private void publish(String content){
		EventAdmin eventAdmin = getContext().getService(eventAdminRef);
		if (eventAdmin != null) {
			Dictionary<String, String> properties = new Hashtable<>();
			properties.put(MifareController.LOC_ID_KEY, content);
			Event mifareEvent = new Event(MifareController.EVENT_TOPIC, properties);	
			eventAdmin.postEvent(mifareEvent);
		}
	}

}
