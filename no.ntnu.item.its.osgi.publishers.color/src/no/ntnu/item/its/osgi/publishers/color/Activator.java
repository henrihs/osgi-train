package no.ntnu.item.its.osgi.publishers.color;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.sensors.color.ColorControllerFactory;
import no.ntnu.item.its.osgi.sensors.common.enums.EColor;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.ColorController;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.common.servicetrackers.SchedulerTrackerCustomizer;

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
	private ServiceTracker<LogService, Object> logServiceTracker;
	private ServiceTracker<EventAdmin, Object> eventAdminTracker;
	private Runnable runnableSensorReading;

	private EColor lastPublishedColor;


	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		cc = ColorControllerFactory.getInstance();

		logServiceTracker = new ServiceTracker<>(bundleContext, LogService.class, null);
		logServiceTracker.open();
		eventAdminTracker = new ServiceTracker<>(bundleContext, EventAdmin.class, null);
		eventAdminTracker.open();
		
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

		ServiceTracker schedulerTracker = 
				new ServiceTracker(
						bundleContext, 
						SensorSchedulerService.class, 
						new SchedulerTrackerCustomizer(bundleContext, runnableSensorReading, SCHEDULE_PERIOD));
		schedulerTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		runnableSensorReading = null;
		Activator.context = null;
	}
	
	private void publish(EColor color){
		if (color == null || color.equals(lastPublishedColor)) {
			return;
		}
		
		if (!eventAdminTracker.isEmpty()) {
			Map<String, EColor> properties = new Hashtable<>();
			properties.put(ColorController.COLOR_KEY, color);
			Event mifareEvent = new Event(ColorController.EVENT_TOPIC, properties);			
			((EventAdmin) eventAdminTracker.getService()).postEvent(mifareEvent);
			((LogService) logServiceTracker.getService()).log(LogService.LOG_DEBUG, "Posted event");
			lastPublishedColor = color;
		}
		
		else if (!logServiceTracker.isEmpty()) {
		((LogService) logServiceTracker.getService()).log(
				LogService.LOG_DEBUG, 
				"Failed to publish event, no EventAdmin service available!");
		}
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
}
