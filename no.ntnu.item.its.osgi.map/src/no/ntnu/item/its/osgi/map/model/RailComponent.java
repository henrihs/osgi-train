package no.ntnu.item.its.osgi.map.model;

public interface RailComponent {
	public RailComponentId id();
	public RouteElement partOfElement();
	public RailComponent lookAhead(PointConnector direction);
}
