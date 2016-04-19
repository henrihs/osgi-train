package no.ntnu.item.its.osgi.publishers.color;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.sensors.common.interfaces.ColorControllerService;

public class ColorPubActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	protected static ServiceTracker<LogService, Object> logServiceTracker;
	protected static ServiceTracker<EventAdmin, Object> eventAdminTracker;
	protected static ServiceTracker<ColorControllerService, Object> colorControllerTracker;

	private ColorPublisher publisher;


	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		ColorPubActivator.context = bundleContext;

		logServiceTracker = new ServiceTracker<>(bundleContext, LogService.class, null);
		logServiceTracker.open();
		eventAdminTracker = new ServiceTracker<>(bundleContext, EventAdmin.class, null);
		eventAdminTracker.open();
		colorControllerTracker = new ServiceTracker<>(
				bundleContext, 
				ColorControllerService.class, 
				null);
		colorControllerTracker.open();
		
		publisher = new ColorPublisher();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		publisher.stop();
		ColorPubActivator.context = null;
	}
}
