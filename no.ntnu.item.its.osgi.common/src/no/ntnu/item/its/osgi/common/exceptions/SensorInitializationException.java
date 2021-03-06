package no.ntnu.item.its.osgi.common.exceptions;

public class SensorInitializationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SensorInitializationException() {
		super();
	}
	
	public SensorInitializationException(String message) {
		super(message);
	}

	public SensorInitializationException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
