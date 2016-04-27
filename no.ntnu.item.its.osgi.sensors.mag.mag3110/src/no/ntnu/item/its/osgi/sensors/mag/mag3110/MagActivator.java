package no.ntnu.item.its.osgi.sensors.mag.mag3110;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import no.ntnu.item.its.osgi.common.enums.SensorNature;
import no.ntnu.item.its.osgi.common.interfaces.MagControllerService;

public class MagActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private MagController mc;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		MagActivator.context = bundleContext;
		final ServiceReference<LogService> logRef = context.getServiceReference(LogService.class);
		
		Runnable r = new Runnable() {
			public void run() {
				try {
					mc = new MagController();
					Hashtable<String, Object> props = new Hashtable<String, Object>();
					props.put(SensorNature.PROPERTY_KEY, SensorNature.PHYSICAL);
					props.put(org.osgi.framework.Constants.SERVICE_RANKING, SensorNature.PHYSICAL.ordinal());
					bundleContext.registerService(MagControllerService.class, mc, props);
				} catch (Exception e) {
					try {
						logAndStop(bundleContext, logRef, e);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		};
		new Thread(r).start();
		
	}
	
	private void logAndStop(BundleContext bundleContext, ServiceReference<LogService> logRef, Exception e) throws Exception {
		context.getService(logRef).log(LogService.LOG_ERROR, e.getMessage(), e);
		context.getBundle().stop();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		MagActivator.context = null;
	}

}
