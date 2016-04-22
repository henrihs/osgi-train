package no.ntnu.item.its.osgi.testrunner;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import no.ntnu.item.its.osgi.eventlogger.EventLoggerActivator;
import no.ntnu.item.its.osgi.publishers.accel.AccelPublisher;
import no.ntnu.item.its.osgi.publishers.speed.VelocityPublisher;
//import no.ntnu.item.osgi.sensors.simulated.SimulationActivator;
import no.ntnu.item.osgi.sensors.simulated.SimulationActivator;

public class TestRunActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		TestRunActivator.context = bundleContext;
		System.out.println("Starting testrun");
		FrameworkUtil.getBundle(EventLoggerActivator.class).start();
//		FrameworkUtil.getBundle(AccelSensor.class).start();
//		Thread.sleep(3000);
		FrameworkUtil.getBundle(SimulationActivator.class).start();
		FrameworkUtil.getBundle(VelocityPublisher.class).start();
		FrameworkUtil.getBundle(AccelPublisher.class).start();
//		Thread.sleep(3000);
//		FrameworkUtil.getBundle(PwmActivator.class).start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
//		FrameworkUtil.getBundle(PwmActivator.class).stop();
//		Thread.sleep(3000);
		FrameworkUtil.getBundle(EventLoggerActivator.class).stop();
		FrameworkUtil.getBundle(AccelPublisher.class).stop();
		FrameworkUtil.getBundle(VelocityPublisher.class).stop();
//		FrameworkUtil.getBundle(AccelSensor.class).stop();
		FrameworkUtil.getBundle(SimulationActivator.class).stop();
		System.out.println("Testrun stopped");
		TestRunActivator.context = null;
	}

}
