package no.ntnu.item.osgi.sensors.simulated;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import no.ntnu.item.its.osgi.common.enums.SensorNature;
import no.ntnu.item.its.osgi.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.common.interfaces.ColorControllerService;
import no.ntnu.item.its.osgi.common.interfaces.MifareControllerService;
import no.ntnu.item.its.osgi.sensors.accel.AccelerationControllerMocker;
import no.ntnu.item.its.osgi.sensors.color.ColorControllerMocker;
import no.ntnu.item.its.osgi.sensors.mifare.MifareControllerMocker;

public class SimulationActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		SimulationActivator.context = bundleContext;
		
//		registerMifareMocker();
		registerColorMocker();
//		ssregisterAccelMocker();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		SimulationActivator.context = null;
	}
	
	private static void registerMifareMocker() {
		MifareControllerService mockedController = new MifareControllerMocker(); 
		Hashtable<String, Object> props = new Hashtable<String, Object>(); 
		props.put(SensorNature.PROPERTY_KEY, SensorNature.SIMULATED);
		props.put(Constants.SERVICE_RANKING, SensorNature.SIMULATED.ordinal());
		context.registerService(MifareControllerService.class, mockedController, props);
	}
	
	private static void registerColorMocker() {
		ColorControllerService mockedController = new ColorControllerMocker(); 
		Hashtable<String, Object> props = new Hashtable<String, Object>(); 
		props.put(SensorNature.PROPERTY_KEY, SensorNature.SIMULATED);
		props.put(Constants.SERVICE_RANKING, SensorNature.SIMULATED.ordinal());
		context.registerService(ColorControllerService.class, mockedController, props);
	}
	
	private static void registerAccelMocker() {
		AccelerationControllerService mockedController = new AccelerationControllerMocker(); 
		Hashtable<String, Object> props = new Hashtable<String, Object>(); 
		props.put(SensorNature.PROPERTY_KEY, SensorNature.SIMULATED);
		props.put(Constants.SERVICE_RANKING, SensorNature.SIMULATED.ordinal());
		context.registerService(AccelerationControllerService.class, mockedController, props);
	}

}
