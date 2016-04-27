package no.ntnu.item.its.osgi.publishers.accel;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.common.interfaces.AccelerationControllerService;

public class AccelPubActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}
	
	protected static ServiceTracker<LogService, Object> logServiceTracker;
	protected static ServiceTracker<EventAdmin, Object> eventAdminTracker;
	protected static ServiceTracker<AccelerationControllerService, Object> accelControllerTracker;
	private AccelPublisher publisher;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		AccelPubActivator.context = bundleContext;
		
		logServiceTracker = new ServiceTracker<>(bundleContext, LogService.class, null);
		logServiceTracker.open();
		eventAdminTracker = new ServiceTracker<>(bundleContext, EventAdmin.class, null);
		eventAdminTracker.open();
		accelControllerTracker = new ServiceTracker<>(
				AccelPubActivator.getContext(), 
				AccelerationControllerService.class, 
				null);
		accelControllerTracker.open();
		
		publisher = new AccelPublisher();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		publisher.stop();
		AccelPubActivator.context = null;
	}


}
