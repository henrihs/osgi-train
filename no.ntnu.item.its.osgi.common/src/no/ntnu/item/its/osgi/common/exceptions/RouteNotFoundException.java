package no.ntnu.item.its.osgi.common.exceptions;

import no.ntnu.item.its.osgi.map.model.RouteDescriptor;

public class RouteNotFoundException extends Exception {

	public RouteNotFoundException(RouteDescriptor descriptor) {
		this("No route found from start '" + 
				descriptor.getStart().toString() + 
				"' to destination '" + 
				descriptor.getDestination().toString() + 
				"'.");
	}
	
	public RouteNotFoundException(String message) {
		super(message);
	}
}
