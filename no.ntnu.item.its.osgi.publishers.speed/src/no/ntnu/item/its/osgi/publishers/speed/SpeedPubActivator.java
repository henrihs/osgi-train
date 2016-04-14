package no.ntnu.item.its.osgi.publishers.speed;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class SpeedPubActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	protected static ServiceTracker<LogService, Object> logServiceTracker;
	protected static ServiceTracker<EventAdmin, Object> eventAdminTracker;
	private SpeedPublisher publisher;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		SpeedPubActivator.context = bundleContext;
				
		logServiceTracker = new ServiceTracker<>(bundleContext, LogService.class, null);
		logServiceTracker.open();
		eventAdminTracker = new ServiceTracker<>(bundleContext, EventAdmin.class, null);
		eventAdminTracker.open();
		
		publisher = new SpeedPublisher();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		SpeedPubActivator.context = null;
	}

}
