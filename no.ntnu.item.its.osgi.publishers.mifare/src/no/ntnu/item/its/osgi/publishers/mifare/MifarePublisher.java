package no.ntnu.item.its.osgi.publishers.mifare;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.common.MifareKeyRing;
import no.ntnu.item.its.osgi.common.enums.EColor;
import no.ntnu.item.its.osgi.common.enums.MifareKeyType;
import no.ntnu.item.its.osgi.common.enums.PublisherType;
import no.ntnu.item.its.osgi.common.enums.Status;
import no.ntnu.item.its.osgi.common.exceptions.NoCardFoundException;
import no.ntnu.item.its.osgi.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.common.interfaces.ActuatorControllerService;
import no.ntnu.item.its.osgi.common.interfaces.ColorControllerService;
import no.ntnu.item.its.osgi.common.interfaces.MagControllerService;
import no.ntnu.item.its.osgi.common.interfaces.MifareControllerService;
import no.ntnu.item.its.osgi.common.interfaces.PublisherService;
import no.ntnu.item.its.osgi.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.common.interfaces.VelocityControllerService;
import no.ntnu.item.its.osgi.common.servicetrackers.SchedulerTrackerCustomizer;

public class MifarePublisher implements PublisherService, EventHandler {

//	public static final long SCHEDULE_PERIOD = 30;
//	public static final long SLEEP_AFTER_PUBLISH_TIME = 1500;
	private static final PublisherType TYPE = PublisherType.BEACON;
	
	private Function<Void, Void> sensorReading;
	
	public MifarePublisher() {
		sensorReading = getSensorReadingFunc();
//		Runnable runnableSensorReading = new Runnable() {
//			@Override
//			public void run() {
//				sensorReading.apply(null);
//			}
//		};

//		ServiceTracker<SensorSchedulerService, Object> schedulerTracker = 
//				new ServiceTracker<SensorSchedulerService, Object>(
//						MifarePubActivator.getContext(), 
//						SensorSchedulerService.class, 
//						new SchedulerTrackerCustomizer(
//								MifarePubActivator.getContext(), runnableSensorReading, SCHEDULE_PERIOD));
//		schedulerTracker.open();
		String[] topics = new String[] { 
				ColorControllerService.EVENT_TOPIC
		};
		Hashtable<String, Object> handlerProps = new Hashtable<String, Object>();
		handlerProps.put(EventConstants.EVENT_TOPIC, topics);
		MifarePubActivator.getContext().registerService(EventHandler.class.getName(), this, handlerProps);
		
		Dictionary<String, Object> serviceProps = new Hashtable<String, Object>();
		serviceProps.put(PublisherType.class.getSimpleName(), TYPE);
		MifarePubActivator.getContext().registerService(PublisherService.class, this, serviceProps);
		
	}
	
	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PublisherType getType() {
		return TYPE;
	}

	public void stop() {
		sensorReading = null;
		
	}
	
	private Function<Void, Void> getSensorReadingFunc() {
		return new Function<Void, Void>() {

			@Override
			public Void apply(Void t) {
				String locationInfo; 
				try {
					MifareControllerService s = 
							(MifareControllerService)MifarePubActivator.
								mifareControllerTracker.getService();
					locationInfo = s.read(42, new MifareKeyRing(MifareKeyType.A));
					if (!locationInfo.isEmpty()) {
						publish(locationInfo);
					} else {
						((LogService)MifarePubActivator.logServiceTracker.getService()).log(
								LogService.LOG_INFO, 
								"Passed beacon with empty location information");
					}
					
//					Thread.sleep(SLEEP_AFTER_PUBLISH_TIME);
				} catch (NoCardFoundException e) {
				} catch (SensorCommunicationException e) {
					((LogService)MifarePubActivator.logServiceTracker.getService()).log(
							LogService.LOG_WARNING, 
							"Problems with sensor reading", e);
				} catch (Exception e) {
					e.printStackTrace();
					((LogService)MifarePubActivator.logServiceTracker.getService()).log(
							LogService.LOG_ERROR, 
							"Faulted while reading from sensor service", e);
						try {
							MifarePubActivator.getContext().getBundle().stop();
						} catch (BundleException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
				return t;
			}
		};
	}

	private void publish(String content){
		EventAdmin ea = (EventAdmin) MifarePubActivator.eventAdminTracker.getService();
		if (ea != null) {
			Map<String, String> properties = new Hashtable<String, String>();
			properties.put(MifareControllerService.LOC_ID_KEY, content);
			Event mifareEvent;
			try {
				mifareEvent = new Event(MifareControllerService.EVENT_TOPIC, properties);
				((EventAdmin) MifarePubActivator.eventAdminTracker.getService()).postEvent(mifareEvent);
			} catch (Exception e) {				
				e.printStackTrace();
			}

		}

		else if (!MifarePubActivator.logServiceTracker.isEmpty()) {
			((LogService) MifarePubActivator.logServiceTracker.getService()).log(
					LogService.LOG_INFO, "Failed to publish event, no EventAdmin service available!");
		}
	}

	@Override
	public void handleEvent(Event arg0) {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				if (arg0.getProperty(ColorControllerService.COLOR_KEY) == EColor.BLUE) {
					sensorReading.apply(null);
				}				
			}
		};
		new Thread(r).start();
	}

}
