package no.ntnu.item.its.osgi.publishers.speed;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class VelocityPubActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	protected static ServiceTracker<LogService, Object> logServiceTracker;
	protected static ServiceTracker<EventAdmin, Object> eventAdminTracker;
	private VelocityPublisher publisher;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		VelocityPubActivator.context = bundleContext;
				
		logServiceTracker = new ServiceTracker<>(bundleContext, LogService.class, null);
		logServiceTracker.open();
		eventAdminTracker = new ServiceTracker<>(bundleContext, EventAdmin.class, null);
		eventAdminTracker.open();
		
		publisher = new VelocityPublisher();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		publisher.stop();
		VelocityPubActivator.context = null;
	}

}
