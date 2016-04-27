package no.ntnu.item.its.osgi.common.exceptions;

public class SensorCommunicationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SensorCommunicationException() {
		super();
	}
	
	public SensorCommunicationException(String message) {
		super(message);
	}
	
	public SensorCommunicationException(String message, Throwable t) {
		super(message, t);
	}
}
