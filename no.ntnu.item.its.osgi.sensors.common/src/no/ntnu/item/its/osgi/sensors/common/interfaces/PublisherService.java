package no.ntnu.item.its.osgi.sensors.common.interfaces;

import no.ntnu.item.its.osgi.sensors.common.enums.PublisherType;
import no.ntnu.item.its.osgi.sensors.common.enums.Status;

public interface PublisherService {
	
	public Status getStatus();

	public PublisherType getType();
}
