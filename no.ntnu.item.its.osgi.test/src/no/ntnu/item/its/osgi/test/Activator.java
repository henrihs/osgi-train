package no.ntnu.item.its.osgi.test;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import no.ntnu.item.its.osgi.sensors.common.enums.SensorNature;
import no.ntnu.item.its.osgi.sensors.common.interfaces.ColorController;
import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareControllerService;
import no.ntnu.item.its.osgi.sensors.mifare.MifareControllerMocker;

public class Activator implements BundleActivator, EventHandler, LogListener {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private String[] topics;
	private ServiceTracker<LogReaderService, Object> readerTracker;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		topics = new String[] { ColorController.EVENT_TOPIC, MifareControllerService.EVENT_TOPIC };
		Hashtable<String, Object> serviceProps = new Hashtable<String, Object>();
		serviceProps.put(EventConstants.EVENT_TOPIC, topics);
		bundleContext.registerService(EventHandler.class.getName(), this, serviceProps);
		
		readerTracker = new ServiceTracker<LogReaderService, Object>(
				bundleContext, 
				LogReaderService.class,
				new LogReaderTrackerCustomizer());
		readerTracker.open();
		
		registerMifareMocker();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		readerTracker.close();
		Activator.context = null;
	}
	
	private static void registerMifareMocker() {
		MifareControllerService mcs = new MifareControllerMocker(); 
		Hashtable<String, Object> props = new Hashtable<String, Object>(); 
		props.put(SensorNature.PROPERTY_KEY, SensorNature.SIMULATED);
		props.put(Constants.SERVICE_RANKING, SensorNature.SIMULATED.ordinal());
		context.registerService(MifareControllerService.class, mcs, props);
	}

	@Override
	public void handleEvent(Event arg0) {
		if (arg0.getTopic().equals(ColorController.EVENT_TOPIC)) {
			System.out.println(arg0.getProperty(ColorController.COLOR_KEY));
		} 
		else if (arg0.getTopic().equals(MifareControllerService.EVENT_TOPIC)) {
			System.out.println(arg0.getProperty(MifareControllerService.LOC_ID_KEY));
		}
		
	}
	
	public void listenTo(LogReaderService logReader) {
		logReader.addLogListener(this);
	}

	@Override
	public void logged(LogEntry arg0) {
		System.out.println(String.format(
				"[%d]%s: %s",
				arg0.getLevel(), 
				arg0.getBundle(), 
				arg0.getMessage()));				
	}

	private class LogReaderTrackerCustomizer implements ServiceTrackerCustomizer<LogReaderService, Object> {
		@Override
		public Object addingService(ServiceReference<LogReaderService> arg0) {
			listenTo(context.getService(arg0));
			return null;
		}
		@Override
		public void modifiedService(ServiceReference<LogReaderService> arg0, Object arg1) {}
		@Override
		public void removedService(ServiceReference<LogReaderService> arg0, Object arg1) {}
	}

}

