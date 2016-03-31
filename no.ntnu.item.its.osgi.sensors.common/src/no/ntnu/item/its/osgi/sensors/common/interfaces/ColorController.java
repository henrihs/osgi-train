package no.ntnu.item.its.osgi.sensors.common.interfaces;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;

/*
 * This is the public API of an RGB Color sensor unit
 * 
 * The API encapsulates the inner workings of the reader/writer
 */
public interface ColorController {
	
	public static final String EVENT_TOPIC = "/no/ntnu/item/its/osgi/sensors/color";
	public static final String COLOR_KEY = "COLOR";

	/*
	 * Read raw color data from sensor
	 *  
	 * @returns an array of integers, where each integer represents the quantity of each component,
	 * 			in the form of [clear, red, green, blue]
	 */
	public int[] getRawData() throws SensorCommunicationException;
}

