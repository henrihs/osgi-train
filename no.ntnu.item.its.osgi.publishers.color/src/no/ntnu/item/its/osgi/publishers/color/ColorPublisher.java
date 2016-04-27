package no.ntnu.item.its.osgi.publishers.color;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.common.enums.EColor;
import no.ntnu.item.its.osgi.common.enums.PublisherType;
import no.ntnu.item.its.osgi.common.enums.Status;
import no.ntnu.item.its.osgi.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.common.interfaces.ColorControllerService;
import no.ntnu.item.its.osgi.common.interfaces.PublisherService;
import no.ntnu.item.its.osgi.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.common.servicetrackers.SchedulerTrackerCustomizer;

public class ColorPublisher implements PublisherService {
	
	public static final long SCHEDULE_PERIOD = 50;
	private static final PublisherType TYPE = PublisherType.SLEEPER;
	
	private EColor lastPublishedColor;


	private Function<Void, Void> sensorReading;

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PublisherType getType() {
		return TYPE;
	}

	public ColorPublisher() {
		sensorReading = getSensorReadingFunc();
		Runnable runnableSensorReading = new Runnable() {

			@Override
			public void run() {
				sensorReading.apply(null);
			}
		};

		ServiceTracker<SensorSchedulerService, Object> schedulerTracker = 
				new ServiceTracker<SensorSchedulerService, Object>(
						ColorPubActivator.getContext(), 
						SensorSchedulerService.class, 
						new SchedulerTrackerCustomizer(
								ColorPubActivator.getContext(), 
								runnableSensorReading, 
								SCHEDULE_PERIOD));
		schedulerTracker.open();
		
		Dictionary<String, Object> serviceProps = new Hashtable<String, Object>();
		serviceProps.put(PublisherType.class.getSimpleName(), TYPE);
		ColorPubActivator.getContext().registerService(PublisherService.class, this, serviceProps);
	}
	
	private void publish(EColor color){
		if (color == null || color.equals(lastPublishedColor)) {
			return;
		}
		
		if (!ColorPubActivator.eventAdminTracker.isEmpty()) {
			Map<String, EColor> properties = new Hashtable<>();
			properties.put(ColorControllerService.COLOR_KEY, color);
			Event colorEvent = new Event(ColorControllerService.EVENT_TOPIC, properties);			
			((EventAdmin) ColorPubActivator.eventAdminTracker.getService()).postEvent(colorEvent);
			lastPublishedColor = color;
		}
		
		else if (!ColorPubActivator.logServiceTracker.isEmpty()) {
		((LogService) ColorPubActivator.logServiceTracker.getService()).log(
				LogService.LOG_DEBUG, 
				"Failed to publish event, no EventAdmin service available!");
		}
	}

	private Function<Void, Void> getSensorReadingFunc() {
		return new Function<Void, Void>() {
			
			@Override
			public Void apply(Void t) {
				try {
					ColorControllerService ccs = (ColorControllerService) ColorPubActivator.colorControllerTracker.getService();
					int[] rawColor = ccs.getRawData();
					if (rawColor != null) {
						EColor color = ColorClassifier.colorApproximation(rawColor);
						publish(color);
					}
				} catch (SensorCommunicationException e) {
				} catch (Exception e) {
					((LogService)ColorPubActivator.logServiceTracker.getService()).log(
							LogService.LOG_ERROR, 
							"Faulted while reading from sensor service", e);
					try {
						ColorPubActivator.getContext().getBundle().stop();
					} catch (BundleException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				return t; 				
			}
		};
	}

	public void stop() {
		sensorReading = null;
	}

}
