package no.ntnu.item.its.osgi.sensors.scheduler;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import no.ntnu.item.its.osgi.common.interfaces.SensorSchedulerService;

public class SchedulerServiceImpl implements BundleActivator, SensorSchedulerService {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private ScheduledExecutorService scheduler;
	private ServiceReference<LogService> logRef;
	private HashMap<Integer, ScheduledFuture> tasks;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		SchedulerServiceImpl.context = bundleContext;
		this.scheduler = Executors.newScheduledThreadPool(0); 
		tasks = new HashMap<>();
		logRef = bundleContext.getServiceReference(LogService.class);
		bundleContext.registerService(SensorSchedulerService.class, this, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		scheduler.shutdownNow();
		SchedulerServiceImpl.context = null;
	}

	@Override
	public void add(Runnable r, long initialDelay, long period) {
		ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(r, initialDelay, period, TimeUnit.MILLISECONDS);
		if (logRef != null) {
			context.getService(logRef).log(LogService.LOG_INFO, 
					String.format("Added runnable with hashCode %d, executing periodically each %d ms", r.hashCode(), period));
		}
		tasks.put(r.hashCode(), task);
	}

	@Override
	public boolean remove(Runnable r, boolean interrupt) {
		ScheduledFuture<?> task = tasks.remove(r.hashCode());
		if(task == null) return false;
		boolean res = task.cancel(interrupt);
		if (logRef != null) {
			context.getService(logRef).log(LogService.LOG_INFO, 
					String.format("Removed runnable with hashCode %d", r.hashCode()));
		}
		return res;
	}
}
