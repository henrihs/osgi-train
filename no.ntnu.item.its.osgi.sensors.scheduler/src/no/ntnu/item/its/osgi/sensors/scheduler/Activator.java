package no.ntnu.item.its.osgi.sensors.scheduler;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;

public class Activator implements BundleActivator, SensorSchedulerService {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private ScheduledThreadPoolExecutor scheduler;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		this.scheduler = new ScheduledThreadPoolExecutor(0); 
		
		bundleContext.registerService(SensorSchedulerService.class.getName(), this, null);
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
		scheduler.scheduleAtFixedRate(r, period, period, TimeUnit.MICROSECONDS);
		
	}

	@Override
	public boolean remove(Runnable r) {
		return scheduler.remove(r);
	}

}
