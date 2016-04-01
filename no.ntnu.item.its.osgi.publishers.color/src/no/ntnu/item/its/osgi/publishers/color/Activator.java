package no.ntnu.item.its.osgi.publishers.color;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import no.ntnu.item.its.osgi.sensors.color.ColorControllerFactory;
import no.ntnu.item.its.osgi.sensors.common.enums.EColor;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.ColorController;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;

public class Activator implements BundleActivator {

	public static final long SCHEDULE_PERIOD = 1000;

	private static BundleContext context;
	private static HashMap<ColorMapping, Double> colors = new HashMap<ColorMapping, Double>();
	static {
		colors.put(new ColorMapping(EColor.RED),null);
		colors.put(new ColorMapping(EColor.BLUE),null);
		colors.put(new ColorMapping(EColor.GREEN),null);
		colors.put(new ColorMapping(EColor.GRAY),null);
		colors.put(new ColorMapping(EColor.YELLOW),null);
	}

	static BundleContext getContext() {
		return context;
	}

	private ColorController cc;
	private ServiceReference<LogService> logRef;
	private ServiceReference<EventAdmin> eventAdminRef;
	private Runnable runnableSensorReading;

	private EColor lastPublishedColor;


	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		cc = ColorControllerFactory.getInstance();

		logRef = bundleContext.getServiceReference(LogService.class);
		eventAdminRef = bundleContext.getServiceReference(EventAdmin.class);

		ServiceTracker<SensorSchedulerService, Runnable> schedulerTracker = 
				new ServiceTracker<SensorSchedulerService, Runnable>(
						bundleContext, 
						SensorSchedulerService.class, 
						new SchedulerTrackerCustomizer());
		schedulerTracker.open();
	}

	private void publish(EColor color){
		if (color == null || color.equals(lastPublishedColor)) {
			return;
		}

		if (eventAdminRef != null) {
			EventAdmin eventAdmin = getContext().getService(eventAdminRef);
			Map<String, EColor> properties = new Hashtable<>();
			properties.put(ColorController.COLOR_KEY, color);
			Event mifareEvent = new Event(ColorController.EVENT_TOPIC, properties);			
			eventAdmin.postEvent(mifareEvent);
			lastPublishedColor = color;
		}
		
		else if (logRef != null) {
			context.getService(logRef).log(LogService.LOG_INFO, "Failed to publish event, no EventAdmin service available!");
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		runnableSensorReading = null;
		Activator.context = null;
	}

	private static EColor colorApproximation(int[] rawColor) {
		Map<ColorMapping, Double> distances = (Map<ColorMapping, Double>) colors.clone();

		for (ColorMapping mapping : colors.keySet()) {
			colors.put(mapping, mapping.compareWith(rawColor));
		}

		Entry<ColorMapping, Double> minEntry = null;

		for (Entry<ColorMapping, Double> entry : colors.entrySet()) {
			if (minEntry == null || entry.getValue() < minEntry.getValue())
				minEntry = entry;
		}

		if (minEntry.getValue() > 196) { // Don't make too approximated approximations!
			return null;
		}

		return minEntry.getKey().getType();
	}

	private class SchedulerTrackerCustomizer implements 
	ServiceTrackerCustomizer<SensorSchedulerService, Runnable> {

		@Override
		public Runnable addingService(ServiceReference<SensorSchedulerService> arg0) {
			runnableSensorReading = new Runnable() {

				@Override
				public void run() {
					int[] rawColor;
					try {
						rawColor = cc.getRawData();
					} catch (SensorCommunicationException e) {
						return;
					}

					if (rawColor != null) {
						EColor color = colorApproximation(rawColor);
						publish(color);
					}
				}
			};

			SensorSchedulerService scheduler = context.getService(arg0);
			scheduler.add(runnableSensorReading, SCHEDULE_PERIOD);
			if (logRef != null) {
				context.getService(logRef).log(
						LogService.LOG_INFO, 
						String.format("Executing periodic sensor readings via %s", 
								arg0.getBundle().getSymbolicName()));
			}

			return null;
		}

		@Override
		public void modifiedService(ServiceReference<SensorSchedulerService> arg0, Runnable arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void removedService(ServiceReference<SensorSchedulerService> arg0, Runnable arg1) {
			if (logRef != null) {
				context.getService(logRef).log(
						LogService.LOG_WARNING, 
						"No longer executing sensor readings, scheduling service is down!");
			}
		}
	}

}
