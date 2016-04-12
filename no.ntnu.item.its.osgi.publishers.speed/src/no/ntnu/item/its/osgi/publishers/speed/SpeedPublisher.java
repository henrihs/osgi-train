package no.ntnu.item.its.osgi.publishers.speed;

import java.util.Hashtable;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;

public class SpeedPublisher implements EventHandler {
	
	

	public SpeedPublisher() {
		String[] topics = new String[] { AccelerationControllerService.EVENT_TOPIC };
		Hashtable<String, Object> serviceProps = new Hashtable<String, Object>();
		serviceProps.put(EventConstants.EVENT_TOPIC, topics);
		SpeedPubActivator.getContext().registerService(EventHandler.class.getName(), this, serviceProps);
	}

	@Override
	public void handleEvent(Event arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private class PreSpeedEvent {
		private double x_accel;
		private double y_accel;
		private double z_accel;
		private double velocity;
		private long timestamp;
		
		public PreSpeedEvent() {
			// TODO Auto-generated constructor stub
		}
	}
}
