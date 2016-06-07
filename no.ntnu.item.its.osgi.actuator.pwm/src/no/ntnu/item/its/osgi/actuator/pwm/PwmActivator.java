package no.ntnu.item.its.osgi.actuator.pwm;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.common.enums.MotorCommand;
import no.ntnu.item.its.osgi.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.common.interfaces.ActuatorControllerService;

public class PwmActivator implements BundleActivator {

	private static BundleContext context;
	
	protected static ServiceTracker<LogService, Object> logServiceTracker;
	protected static ServiceTracker<EventAdmin, Object> eventAdminTracker;

	static BundleContext getContext() {
		return context;
	}

	private ActuatorControllerImpl controller;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		PwmActivator.context = bundleContext;
		
		logServiceTracker = new ServiceTracker<>(bundleContext, LogService.class, null);
		logServiceTracker.open();
		eventAdminTracker = new ServiceTracker<>(bundleContext, EventAdmin.class, null);
		eventAdminTracker.open();
		
		try {
			controller = new ActuatorControllerImpl();
			bundleContext.registerService(ActuatorControllerService.class, controller, null);
			controller.send(MotorCommand.FORWARD);			
		} catch (SensorInitializationException e) {
			if (!logServiceTracker.isEmpty()) {
				((LogService)logServiceTracker.getService()).log(
						LogService.LOG_ERROR, 
						"Could not initialize actuator", 
						e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		controller.send(MotorCommand.STOP);
		PwmActivator.context = null;
	}

}
