package no.ntnu.item.its.osgi.publishers.speed;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;
import org.apache.commons.math.analysis.integration.*;

import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.VelocityControllerService;

public class VelocityPublisher implements EventHandler {

	private VelocityData<TrapezoidIntegrator> latestSpeedEvent;
	private TrapezoidIntegrator integrator = new TrapezoidIntegrator();

	public VelocityPublisher() {
		String[] topics = new String[] { AccelerationControllerService.EVENT_TOPIC };
		Hashtable<String, Object> serviceProps = new Hashtable<String, Object>();
		serviceProps.put(EventConstants.EVENT_TOPIC, topics);
		VelocityPubActivator.getContext().registerService(EventHandler.class.getName(), this, serviceProps);
	}

	@Override
	public void handleEvent(Event arg0) {
		double a_x = (double) arg0.getProperty(AccelerationControllerService.X_DATA_KEY);
		long timestamp = (long) arg0.getProperty(AccelerationControllerService.TIMESTAMP_KEY);
		VelocityData<TrapezoidIntegrator> preSpeedEvent = new VelocityData<TrapezoidIntegrator>(a_x, timestamp, integrator);
		if (latestSpeedEvent != null) {
			preSpeedEvent.calculateVelocityDelta(latestSpeedEvent);
		}

		publish(preSpeedEvent.v_x);

		latestSpeedEvent = preSpeedEvent;
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
}
