package no.ntnu.item.its.osgi.sensors.common.interfaces;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;

public interface MagControllerService {

	public static final String EVENT_TOPIC = "no/ntnu/item/its/osgi/sensors/mag";
	public static final String X_DATA_KEY = "mag.x";
	public static final String Y_DATA_KEY = "mag.y";
	public static final String Z_DATA_KEY = "mag.z";
	public static final String HEADING_KEY = "mag.heading";
	public static final String TIMESTAMP_KEY = "data.timestamp";

	public double[] getRawData() throws SensorCommunicationException;
}
