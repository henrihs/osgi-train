package no.ntnu.item.its.osgi.publishers.speed;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.apache.commons.math.analysis.integration.*;

import no.ntnu.item.its.osgi.sensors.common.enums.PublisherType;
import no.ntnu.item.its.osgi.sensors.common.enums.Status;
import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.PublisherService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.VelocityControllerService;
import no.ntnu.item.its.osgi.sensors.common.servicetrackers.SchedulerTrackerCustomizer;

public class VelocityPublisher implements EventHandler, PublisherService {

	public static final long SCHEDULE_PERIOD = 50;

	private TrapezoidIntegrator integrator = new TrapezoidIntegrator();
	private volatile ConcurrentSkipListSet<VelocityData<?>> accelDataCollection;
	private static final PublisherType TYPE = PublisherType.VELOCITY;
	private Function<Void, Void> speedAddingFunc;
	private double velocity;

	public VelocityPublisher() {
		accelDataCollection = new ConcurrentSkipListSet<VelocityData<?>>();
		String[] topics = new String[] { AccelerationControllerService.EVENT_TOPIC };
		Hashtable<String, Object> handlerServiceProps = new Hashtable<String, Object>();
		handlerServiceProps.put(EventConstants.EVENT_TOPIC, topics);
		VelocityPubActivator.getContext().registerService(EventHandler.class.getName(), this, handlerServiceProps);

		Dictionary<String, Object> publiserServiceProps = new Hashtable<String, Object>();
		publiserServiceProps.put(PublisherType.class.getSimpleName(), TYPE);
		VelocityPubActivator.getContext().registerService(PublisherService.class, this, publiserServiceProps);
		velocity = 0;

		speedAddingFunc = getSpeedAddingFunc();
		Runnable runnableSpeedAdding = new Runnable() {
			@Override
			public void run() {
				speedAddingFunc.apply(null);
			}
		};

		ServiceTracker<SensorSchedulerService, Object> schedulerTracker = 
				new ServiceTracker<SensorSchedulerService, Object>(
						VelocityPubActivator.getContext(), 
						SensorSchedulerService.class, 
						new SchedulerTrackerCustomizer(
								VelocityPubActivator.getContext(), 
								runnableSpeedAdding, 
								SCHEDULE_PERIOD));
		schedulerTracker.open();
	}

	@Override
	public void handleEvent(final Event arg0) {

		Runnable r = new Runnable() {
			public void run() {
				VelocityData<?> previous = null;
				if (!accelDataCollection.isEmpty()) {
					try {
						previous = accelDataCollection.last();
					} catch (NoSuchElementException e) {
						previous = null;
					}
				}
				
				double a_x = (double) arg0.getProperty(AccelerationControllerService.X_DATA_KEY);
				long timestamp = (long) arg0.getProperty(AccelerationControllerService.TIMESTAMP_KEY);
				VelocityData<TrapezoidIntegrator> preSpeedEvent = new VelocityData<TrapezoidIntegrator>(a_x, timestamp,
						integrator);
				if (previous != null) {
					if (previous.getTimestamp() > preSpeedEvent.getTimestamp()) {
						return; //Don't calculate speed if a newer event is already received
					}

					preSpeedEvent.calculateVelocityDelta(previous);
				}
				accelDataCollection.add(preSpeedEvent);
			}
		};
		
		new Thread(r).start();
	}

	private Function<Void, Void> getSpeedAddingFunc() {
		return new Function<Void, Void>() {

			@Override
			public Void apply(Void t) {
				if (accelDataCollection.size() < 5) {
					return null;
				}
				
				SortedSet<VelocityData<?>> data = null;
				synchronized (accelDataCollection) {
					data = (ConcurrentSkipListSet<VelocityData<?>>) accelDataCollection.clone();
					accelDataCollection.clear();					
					accelDataCollection.add(data.last());
				}

				Double total_delta = 0.0;
				try {
					total_delta = data.stream().mapToDouble(v -> v.v_delta).sum();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}

				velocity += total_delta;
				publish(velocity);
				return null;
			}
		};

	}


	private void publish(double v_x) {
		if (!VelocityPubActivator.eventAdminTracker.isEmpty()) {
			Event speedEvent = createEvent(v_x);			
			((EventAdmin) VelocityPubActivator.eventAdminTracker.getService()).postEvent(speedEvent);
		}

		else if (!VelocityPubActivator.logServiceTracker.isEmpty()) {
			((LogService) VelocityPubActivator.logServiceTracker.getService()).log(
					LogService.LOG_DEBUG, 
					"Failed to publish event, no EventAdmin service available!");
		}
	}

	private Event createEvent(double v_x) {
		Map<String, Object> properties = new Hashtable<>();
		properties.put(
				VelocityControllerService.TIMESTAMP_KEY, 
				System.nanoTime());
		properties.put(VelocityControllerService.VX_KEY, v_x);
		Event speedEvent = new Event(VelocityControllerService.EVENT_TOPIC, properties);
		return speedEvent;
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PublisherType getType() {
		return TYPE ;
	}

	public void stop() {
		speedAddingFunc = null;
	}
}
