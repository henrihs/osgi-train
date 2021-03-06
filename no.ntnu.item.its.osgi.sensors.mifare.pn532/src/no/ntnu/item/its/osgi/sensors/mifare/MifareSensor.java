package no.ntnu.item.its.osgi.sensors.mifare;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import no.ntnu.item.its.osgi.common.enums.SensorNature;
import no.ntnu.item.its.osgi.common.interfaces.MifareControllerService;
import no.ntnu.item.its.osgi.sensors.mifare.pn532.MifareControllerImpl;

public class MifareSensor implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		MifareSensor.context = bundleContext;
		final ServiceReference<LogService> logRef = context.getServiceReference(LogService.class);
		
		Runnable r = new Runnable() {
			public void run() {
				try {
					MifareControllerService mcs = new MifareControllerImpl();
					Hashtable<String, Object> props = new Hashtable<String, Object>();
					props.put(SensorNature.PROPERTY_KEY, SensorNature.PHYSICAL);
					props.put(Constants.SERVICE_RANKING, SensorNature.PHYSICAL.ordinal());
					bundleContext.registerService(MifareControllerService.class, mcs, props);
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
		stop(bundleContext);
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		MifareSensor.context = null;

	}
}
