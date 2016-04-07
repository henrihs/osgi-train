package no.ntnu.item.its.osgi.sensors.accel;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import no.ntnu.item.its.osgi.sensors.accel.mma8491q.AccelerationControllerImpl;
import no.ntnu.item.its.osgi.sensors.common.enums.SensorNature;
import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;

public class AccelSensor implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		AccelSensor.context = bundleContext;
		ServiceReference<LogService> logRef = context.getServiceReference(LogService.class);
		
		try {
			AccelerationControllerService acs = new AccelerationControllerImpl();
			Hashtable<String, Object> props = new Hashtable<String, Object>(); 
			props.put(SensorNature.PROPERTY_KEY, SensorNature.PHYSICAL);
			props.put(org.osgi.framework.Constants.SERVICE_RANKING, SensorNature.PHYSICAL.ordinal());
			bundleContext.registerService(AccelerationControllerService.class, acs, props);
		} catch (Exception e) {
			logAndStop(bundleContext, logRef, e);
		}
		
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
		AccelSensor.context = null;
	}

}
