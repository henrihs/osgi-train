package no.ntnu.item.its.osgi.publishers.mifare;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import no.ntnu.item.its.osgi.sensors.common.MifareKeyRing;
import no.ntnu.item.its.osgi.sensors.common.enums.MifareKeyType;
import no.ntnu.item.its.osgi.sensors.common.enums.PublisherType;
import no.ntnu.item.its.osgi.sensors.common.enums.Status;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareControllerService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.PublisherService;
import no.ntnu.item.its.osgi.sensors.common.interfaces.SensorSchedulerService;
import no.ntnu.item.its.osgi.sensors.common.servicetrackers.SchedulerTrackerCustomizer;

public class MifarePublisher implements PublisherService {

	public static final long SCHEDULE_PERIOD = 1000;
	private static final PublisherType TYPE = PublisherType.BEACON;
	
	private Function<Void, Void> sensorReading;
	
	public MifarePublisher() {
		sensorReading = getSensorReadingFunc();
		Runnable runnableSensorReading = new Runnable() {
			@Override
			public void run() {
				sensorReading.apply(null);
			}
		};

		ServiceTracker<SensorSchedulerService, Object> schedulerTracker = 
				new ServiceTracker<SensorSchedulerService, Object>(
						MifarePubActivator.getContext(), 
						SensorSchedulerService.class, 
						new SchedulerTrackerCustomizer(
								MifarePubActivator.getContext(), runnableSensorReading, SCHEDULE_PERIOD));
		schedulerTracker.open();
		
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
				String content; 
				try {
					MifareControllerService s = 
							(MifareControllerService)MifarePubActivator.
								mifareControllerTracker.getService();
					content = s.read(42, new MifareKeyRing(MifareKeyType.A));
					if (!content.isEmpty()) {
						publish(content);
					}
				} catch (SensorCommunicationException e) {
				} catch (Exception e) {
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

}
