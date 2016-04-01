package no.ntnu.item.its.osgi.test;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

import no.ntnu.item.its.osgi.sensors.common.interfaces.ColorController;
import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareController;

public class Activator implements BundleActivator, EventHandler, LogListener {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private String[] topics;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		topics = new String[] { ColorController.EVENT_TOPIC, MifareController.EVENT_TOPIC };
		Hashtable<String, Object> serviceProps = new Hashtable<String, Object>();
		serviceProps.put(EventConstants.EVENT_TOPIC, topics);
		bundleContext.registerService(EventHandler.class.getName(), this, serviceProps);
		
		ServiceReference<LogReaderService> readerRef = (ServiceReference<LogReaderService>) 
				context.getServiceReference(LogReaderService.class.getName());
		if (readerRef != null) {
			LogReaderService reader = (LogReaderService) context.getService(readerRef);
			reader.addLogListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	@Override
	public void handleEvent(Event arg0) {
		if (arg0.getTopic().equals(ColorController.EVENT_TOPIC)) {
			System.out.println(arg0.getProperty(ColorController.COLOR_KEY));
		} 
		else if (arg0.getTopic().equals(MifareController.EVENT_TOPIC)) {
			System.out.println(arg0.getProperty(MifareController.LOC_ID_KEY));
		}
		
	}

	@Override
	public void logged(LogEntry arg0) {
		System.out.println(String.format(
				"[%d]%s: %s",
				arg0.getLevel(), 
				arg0.getBundle(), 
				arg0.getMessage()));				
	}


}
