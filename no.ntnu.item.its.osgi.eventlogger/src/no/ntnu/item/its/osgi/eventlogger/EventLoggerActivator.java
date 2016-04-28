package no.ntnu.item.its.osgi.eventlogger;

import java.io.PrintWriter;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
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

import no.ntnu.item.its.osgi.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.common.interfaces.ActuatorControllerService;
import no.ntnu.item.its.osgi.common.interfaces.ColorControllerService;
import no.ntnu.item.its.osgi.common.interfaces.MagControllerService;
import no.ntnu.item.its.osgi.common.interfaces.MifareControllerService;
import no.ntnu.item.its.osgi.common.interfaces.VelocityControllerService;

public class EventLoggerActivator implements BundleActivator, EventHandler {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private String[] topics;
	private PrintWriter accelWriter;
	private PrintWriter velocityWriter;
	private PrintWriter commandWriter;
	private PrintWriter headingWriter;

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
				VelocityControllerService.EVENT_TOPIC,
				ActuatorControllerService.EVENT_TOPIC,
				MagControllerService.EVENT_TOPIC
				};
		Hashtable<String, Object> serviceProps = new Hashtable<String, Object>();
		serviceProps.put(EventConstants.EVENT_TOPIC, topics);
		bundleContext.registerService(EventHandler.class.getName(), this, serviceProps);
		
		long now = System.currentTimeMillis();
		accelWriter = new PrintWriter("acceleration_" + now + ".csv", "UTF-8");
		velocityWriter = new PrintWriter("velocity_" + now + ".csv", "UTF-8");
		commandWriter = new PrintWriter("command_" + now + ".csv", "UTF-8");
		headingWriter = new PrintWriter("mag_" + now + ".csv", "UTF-8");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		accelWriter.close();
		velocityWriter.close();
		commandWriter.close();
		headingWriter.close();
		EventLoggerActivator.context = null;
	}
	
	

	@Override
	public void handleEvent(Event arg0) {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
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
				else if (arg0.getTopic().equals(ActuatorControllerService.EVENT_TOPIC)) {
					commandWriter.println(
							((long)arg0.getProperty(ActuatorControllerService.TIMESTAMP_KEY)-1)*1E-9 + ", " +
							arg0.getProperty(ActuatorControllerService.PREV_STATE_KEY));
					
					commandWriter.println(
							(long)arg0.getProperty(ActuatorControllerService.TIMESTAMP_KEY)*1E-9 + ", " +
							arg0.getProperty(ActuatorControllerService.NEXT_STATE_KEY));
				}
				else if (arg0.getTopic().equals(MagControllerService.EVENT_TOPIC)) {
					headingWriter.println(
							(long)arg0.getProperty(MagControllerService.TIMESTAMP_KEY)*1E-9 + ", " + 
							arg0.getProperty(MagControllerService.HEADING_KEY));
				}
				
			}
		};
		new Thread(r).start();
	}
}

