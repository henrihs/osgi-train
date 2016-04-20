package no.ntnu.item.its.osgi.testrunner;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		System.out.println("Starting testrun");
		bundleContext.getBundle(2).start();
		bundleContext.getBundle(10).start();
		bundleContext.getBundle(15).start();
		bundleContext.getBundle(17).start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		bundleContext.getBundle(10).stop();
		bundleContext.getBundle(17).stop();
		bundleContext.getBundle(15).stop();
		bundleContext.getBundle(2).stop();
		System.out.println("Testrun stopped");
		Activator.context = null;
	}

}
