package no.ntnu.item.its.osgi.positioning;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.ActuatorControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.ColorControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.MagControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.VelocityControllerService;

public class SensorEventHandler implements EventHandler {
	
	private static final String[] topics = new String[] { 
			ColorControllerService.EVENT_TOPIC, 
			MifareControllerService.EVENT_TOPIC,
			AccelerationControllerService.EVENT_TOPIC,
			VelocityControllerService.EVENT_TOPIC,
			MagControllerService.EVENT_TOPIC
	};
	
	private final Map<String, Function<Event, Void>> topicMapping = 
			new Hashtable<String, Function<Event, Void>>();
		

	public SensorEventHandler() {
		topicMapping.put(ColorControllerService.EVENT_TOPIC,  this::handleColorEvent);
		topicMapping.put(MifareControllerService.EVENT_TOPIC, this::handleMifareEvent);
		topicMapping.put(AccelerationControllerService.EVENT_TOPIC, this::handleAccelEvent);
		topicMapping.put(VelocityControllerService.EVENT_TOPIC, this::handleVelocityEvent);
		topicMapping.put(MagControllerService.EVENT_TOPIC, this::handleMagEvent);
		
		Hashtable<String, Object> serviceProps = new Hashtable<String, Object>();
		serviceProps.put(EventConstants.EVENT_TOPIC, topics);
		
		PositioningActivator.getContext().
			registerService(EventHandler.class, this, serviceProps);
	}

	@Override
	public void handleEvent(Event arg0) {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				topicMapping.get(arg0.getTopic()).apply(arg0);						
			}
		};
		
		new Thread(r).start();
	}
	
	private Void handleColorEvent(Event colorEvent) {
		return null; 		
	}
	
	private Void handleMifareEvent(Event colorEvent) {
		return null; 		
	}
	
	private Void handleAccelEvent(Event colorEvent) {
		return null; 		
	}
	
	private Void handleVelocityEvent(Event colorEvent) {
		return null; 		
	}
	
	private Void handleMagEvent(Event colorEvent) {
		return null; 		
	}

}
