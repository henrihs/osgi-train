package no.ntnu.item.its.osgi.eventlogger;

import java.io.PrintWriter;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.ColorControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.VelocityControllerService;

public class EventLoggerActivator implements BundleActivator, EventHandler, LogListener {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private String[] topics;
	private ServiceTracker<LogReaderService, Object> readerTracker;
	private PrintWriter accelWriter;
	private PrintWriter velocityWriter;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		EventLoggerActivator.context = bundleContext;
		
		topics = new String[] { 
				ColorControllerService.EVENT_TOPIC, 
				MifareControllerService.EVENT_TOPIC,
				AccelerationControllerService.EVENT_TOPIC,
				VelocityControllerService.EVENT_TOPIC
				};
		Hashtable<String, Object> serviceProps = new Hashtable<String, Object>();
		serviceProps.put(EventConstants.EVENT_TOPIC, topics);
		bundleContext.registerService(EventHandler.class.getName(), this, serviceProps);
		
		readerTracker = new ServiceTracker<LogReaderService, Object>(
				bundleContext, 
				LogReaderService.class,
				new LogReaderTrackerCustomizer());
		readerTracker.open();
		
		accelWriter = new PrintWriter("x_axis_acceleration_log.csv", "UTF-8");
		velocityWriter = new PrintWriter("x_axis_velocity_log.csv", "UTF-8");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		readerTracker.close();
		accelWriter.close();
		velocityWriter.close();
		EventLoggerActivator.context = null;
	}
	
	

	@Override
	public void handleEvent(Event arg0) {
		if (arg0.getTopic().equals(ColorControllerService.EVENT_TOPIC)) {
			System.out.println(ColorControllerService.COLOR_KEY + ": " + arg0.getProperty(ColorControllerService.COLOR_KEY));
		} 
		else if (arg0.getTopic().equals(MifareControllerService.EVENT_TOPIC)) {
			System.out.println(MifareControllerService.LOC_ID_KEY + ": " + arg0.getProperty(MifareControllerService.LOC_ID_KEY));
		}
		else if (arg0.getTopic().equals(AccelerationControllerService.EVENT_TOPIC)) {
			accelWriter.println(
					(long)arg0.getProperty(AccelerationControllerService.TIMESTAMP_KEY)*1E-9 + ", " +
					arg0.getProperty(AccelerationControllerService.X_DATA_KEY));
		}
		else if (arg0.getTopic().equals(VelocityControllerService.EVENT_TOPIC)) {
			velocityWriter.println(
					(long)arg0.getProperty(VelocityControllerService.TIMESTAMP_KEY)*1E-9 + ", " +
					arg0.getProperty(VelocityControllerService.VX_KEY));
		}
		
	}
	
	public void listenTo(LogReaderService logReader) {
		logReader.addLogListener(this);
	}

	@Override
	public void logged(LogEntry arg0) {
		String level = null;
		switch (arg0.getLevel()) {
		case LogService.LOG_DEBUG:
			level = "DEBUG";
			break;
		case LogService.LOG_INFO:
			level = "INFO";
			break;
		case LogService.LOG_WARNING:
			level = "WARNING";
			break;
		case LogService.LOG_ERROR:
			level = "ERROR";
			break;
		}
		
		String content = arg0.getMessage();
		if (arg0.getException() != null && arg0.getException().getCause() != null) {
			content = content.concat(": ").concat(arg0.getException().getMessage());
		}
		
		System.out.println(String.format(
				"[%s]%s: %s",
				level, 
				arg0.getBundle(),
				content));				
	}

	private class LogReaderTrackerCustomizer implements ServiceTrackerCustomizer<LogReaderService, Object> {
		@Override
		public Object addingService(ServiceReference<LogReaderService> arg0) {
			listenTo(context.getService(arg0));
			return null;
		}
		@Override
		public void modifiedService(ServiceReference<LogReaderService> arg0, Object arg1) {}
		@Override
		public void removedService(ServiceReference<LogReaderService> arg0, Object arg1) {}
	}

}

