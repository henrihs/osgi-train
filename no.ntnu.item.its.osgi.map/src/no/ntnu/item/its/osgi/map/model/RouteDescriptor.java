package no.ntnu.item.its.osgi.map.model;

public class RouteDescriptor extends Pair<RailLeg, RouteElement> {

	public RouteDescriptor(RailLeg first, RouteElement second) {
		super(first, second);
		// TODO Auto-generated constructor stub
	}
	
	public RailLeg getStart() {
		return first;
	}
	
	public RouteElement getDestination() {
		return second;
	}
}
