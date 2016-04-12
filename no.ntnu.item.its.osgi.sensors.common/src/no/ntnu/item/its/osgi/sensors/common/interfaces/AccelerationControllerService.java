package no.ntnu.item.its.osgi.sensors.common.interfaces;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorInitializationException;

public interface AccelerationControllerService {
	public static final String EVENT_TOPIC = "no/ntnu/item/its/osgi/sensors/accel";
	public static final String X_DATA_KEY = "data.x";
	public static final String Y_DATA_KEY = "data.y";
	public static final String Z_DATA_KEY = "data.z";
	public static final String TIMESTAMP_KEY = "data.timestamp";
	public static final double GRAVITATIONAL_RATIO = 9.81;

	/*
	 * Read raw acceleration data from sensor
	 *  
	 * @returns an array of integers, where each integer represents 
	 *  the acceleration in the directions [x, y, z]. 
	 *  The value is a signed 14-bit integer value. 
	 */
	public int[] getRawData() throws SensorCommunicationException, SensorInitializationException;

	/*
	 * Read acceleration data from sensor, 
	 * adjusted to calibration data collected at 
	 * instantiation time. 
	 * <p>
	 * Sensor should be in a stationary position at instantiation time
	 * for this calibrated data to be valid!   
	 *  
	 * @returns an array of integers, where each integer represents 
	 *  the acceleration in the directions [x, y, z]. 
	 *  The value is a signed 14-bit integer value. 
	 */
	public int[] getCalibratedData() throws SensorCommunicationException, SensorInitializationException;
}
