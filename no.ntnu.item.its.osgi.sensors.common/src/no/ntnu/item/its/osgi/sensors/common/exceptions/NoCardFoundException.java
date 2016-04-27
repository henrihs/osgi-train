package no.ntnu.item.its.osgi.sensors.common.exceptions;

public class NoCardFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoCardFoundException() {
		super();
	}
	
	public NoCardFoundException(String message) {
		super(message);
	}
	
	public NoCardFoundException(String message, Throwable t) {
		super(message, t);
	}
}
