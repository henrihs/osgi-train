package no.ntnu.item.its.osgi.publishers.mifare;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.common.interfaces.MifareControllerService;

public class MifarePubActivator implements BundleActivator {
	private static BundleContext context;
	protected static ServiceTracker<EventAdmin, Object> eventAdminTracker;
	protected static ServiceTracker<LogService, LogService> logServiceTracker;
	protected static ServiceTracker<MifareControllerService, MifareControllerService> mifareControllerTracker;

	private MifarePublisher publisher; 
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		MifarePubActivator.context = bundleContext;

		logServiceTracker = new ServiceTracker<>(bundleContext, LogService.class, null);
		logServiceTracker.open();
		eventAdminTracker = new ServiceTracker<>(bundleContext, EventAdmin.class, null);
		eventAdminTracker.open();
		mifareControllerTracker = new ServiceTracker<>(
				bundleContext, 
				MifareControllerService.class, 
				null);
		mifareControllerTracker.open();

		publisher = new MifarePublisher();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		publisher.stop();
		MifarePubActivator.context = null;
	}
}
