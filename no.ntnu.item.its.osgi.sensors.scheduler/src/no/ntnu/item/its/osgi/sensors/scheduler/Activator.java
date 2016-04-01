package no.ntnu.item.its.osgi.sensors.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;

public class Activator implements BundleActivator, SensorSchedulerService {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private ScheduledExecutorService scheduler;
	private ServiceReference<LogService> logRef;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.scheduler = Executors.newScheduledThreadPool(0); 
		
		logRef = bundleContext.getServiceReference(LogService.class);
		bundleContext.registerService(SensorSchedulerService.class, this, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		scheduler.shutdownNow();
		Activator.context = null;
	}

	@Override
	public void add(Runnable r, long period) {
		scheduler.scheduleAtFixedRate(r, period, period, TimeUnit.MILLISECONDS);
		if (logRef != null) {
			context.getService(logRef).log(LogService.LOG_INFO, 
					String.format("Added runnable with hashCode %d, executing periodically each %d ms", r.hashCode(), period));
		}
	}

	@Override
	public boolean remove(Runnable r) {
		return false;
//		if (logRef != null) {
//			context.getService(logRef).log(LogService.LOG_INFO, String.format("Removing runnable with hashCode %d", r.hashCode()));
//		}
//		
//		return scheduler.;
	}

}
