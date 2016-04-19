package no.ntnu.item.its.osgi.publishers.speed;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;
import org.apache.commons.math.analysis.integration.*;

import no.ntnu.item.its.osgi.sensors.common.enums.PublisherType;
import no.ntnu.item.its.osgi.sensors.common.enums.Status;
import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.PublisherService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.VelocityControllerService;

public class VelocityPublisher implements EventHandler, PublisherService {

	private TrapezoidIntegrator integrator = new TrapezoidIntegrator();
	private ArrayList<VelocityData<?>> accelDataCollection;
	private static final PublisherType TYPE = PublisherType.VELOCITY;

	public VelocityPublisher() {
		accelDataCollection = new ArrayList<>();
		String[] topics = new String[] { AccelerationControllerService.EVENT_TOPIC };
		Hashtable<String, Object> handlerServiceProps = new Hashtable<String, Object>();
		handlerServiceProps.put(EventConstants.EVENT_TOPIC, topics);
		VelocityPubActivator.getContext().registerService(EventHandler.class.getName(), this, handlerServiceProps);
		
		Dictionary<String, Object> publiserServiceProps = new Hashtable<String, Object>();
		publiserServiceProps.put(PublisherType.class.getSimpleName(), TYPE);
		VelocityPubActivator.getContext().registerService(PublisherService.class, this, publiserServiceProps);
	}

	@Override
	public void handleEvent(Event arg0) {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				VelocityData<?> previous = null;
				if (!accelDataCollection.isEmpty()) {
					previous = accelDataCollection.get(accelDataCollection.size()-1);					
				}
				
				double a_x = (double) arg0.getProperty(AccelerationControllerService.X_DATA_KEY);
				long timestamp = (long) arg0.getProperty(AccelerationControllerService.TIMESTAMP_KEY);
				VelocityData<TrapezoidIntegrator> preSpeedEvent = new VelocityData<TrapezoidIntegrator>(a_x, timestamp, integrator);
				if (previous != null) {
					if (previous.getTimestamp() > preSpeedEvent.getTimestamp()) {
						return; //Don't calculate speed if a newer event is already received
					}
					
					preSpeedEvent.calculateVelocityDelta(previous);
				}

				if (accelDataCollection.size() > 30) {
					double delta_v_sum = 0;
					
					try {
						delta_v_sum = accelDataCollection.stream().mapToDouble(v -> v.v_delta).sum();
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					
					publish(delta_v_sum + preSpeedEvent.v_delta);
					accelDataCollection.clear();
				}

				accelDataCollection.add(preSpeedEvent);
			}
		};
		new Thread(r).start();
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
}
