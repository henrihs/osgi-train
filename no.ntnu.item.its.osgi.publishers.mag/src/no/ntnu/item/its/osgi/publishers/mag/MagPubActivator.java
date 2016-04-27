package no.ntnu.item.its.osgi.publishers.mag;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.common.interfaces.MagControllerService;

public class MagPubActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}
	
	protected static ServiceTracker<LogService, Object> logServiceTracker;
	protected static ServiceTracker<EventAdmin, Object> eventAdminTracker;
	protected static ServiceTracker<MagControllerService, Object> magControllerTracker;
	private MagPublisher publisher;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		MagPubActivator.context = bundleContext;
		
		logServiceTracker = new ServiceTracker<>(bundleContext, LogService.class, null);
		logServiceTracker.open();
		eventAdminTracker = new ServiceTracker<>(bundleContext, EventAdmin.class, null);
		eventAdminTracker.open();
		magControllerTracker = new ServiceTracker<>(
				MagPubActivator.getContext(), 
				MagControllerService.class, 
				null);
		magControllerTracker.open();
		
		publisher = new MagPublisher();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		publisher.stop();
		MagPubActivator.context = null;
	}

}
