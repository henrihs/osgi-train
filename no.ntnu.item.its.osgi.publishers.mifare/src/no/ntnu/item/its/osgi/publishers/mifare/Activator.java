package no.ntnu.item.its.osgi.publishers.mifare;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareController;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.mifare.*;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private ServiceReference<SensorSchedulerService> schedulerRef;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		MifareController mc = MifareControllerFactory.getInstance();
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		};
		
		schedulerRef = bundleContext.getServiceReference(SensorSchedulerService.class);
		SensorSchedulerService scheduler = bundleContext.getService(schedulerRef);
		if (scheduler != null) {
			scheduler.add(r, period);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
