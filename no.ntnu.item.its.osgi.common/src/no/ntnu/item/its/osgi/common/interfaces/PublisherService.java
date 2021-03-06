package no.ntnu.item.its.osgi.common.interfaces;

import no.ntnu.item.its.osgi.common.enums.PublisherType;
import no.ntnu.item.its.osgi.common.enums.Status;

public interface PublisherService {
	
	public Status getStatus();

	public PublisherType getType();
}
