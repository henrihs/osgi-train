package no.ntnu.item.its.osgi.test;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import no.ntnu.item.its.osgi.sensors.common.interfaces.ColorController;
import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareController;

public class Activator implements BundleActivator, EventHandler {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		bundleContext.registerService(EventHandler.class, this, null);
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


}
