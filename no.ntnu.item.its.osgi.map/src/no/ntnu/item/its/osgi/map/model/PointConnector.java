package no.ntnu.item.its.osgi.map.model;

import no.ntnu.item.its.osgi.map.enums.PointConnectorEnum;

public class PointConnector extends RouteElement implements RailComponent {
	private final Point point;
	private final PointConnectorEnum connector;
	private RailLeg connectedRailLeg;

	public PointConnector(Point point, PointConnectorEnum connectorType){
		this.point = point;
		this.connector = connectorType;
		point.addConnector(this);
	}
	
	PointConnector(Point pointSwitch, PointConnectorEnum connectorType, RailLeg connectedRailLeg){
		this(pointSwitch, connectorType);
		this.connectedRailLeg = connectedRailLeg;
	}

	public Point point() {
		return point;
	}

	public PointConnectorEnum getType() {
		return connector;
	}

	public RailLeg getConnectedRailLeg() {
		return connectedRailLeg;
	}

	public void setConnectedRailLeg(RailLeg connection) {
		this.connectedRailLeg = connection;
	}

	@Override
	public RouteElement[] getNext(RouteElement previous) {
		if (previous instanceof PointConnector) {
			return new RouteElement[] { connectedRailLeg };
		}
		
		switch (connector) {
		case ENTRY:
			return new RouteElement[] { point.getConnector(PointConnectorEnum.THROUGH), point.getConnector(PointConnectorEnum.DIVERT) };
		case THROUGH:		
		case DIVERT:
			return new RouteElement[] { point.getConnector(PointConnectorEnum.ENTRY) };
		default:
			return null;
		}
	}

	@Override
	public int length() {
		return 1;
	}

	@Override
	public RailComponentId id() {
		return point.id();
	}

	@Override
	public RouteElement partOfElement() {
		return this;
	}		
	
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof PointConnector) {
			PointConnector other = (PointConnector) arg0;
			if (point.equals(other.point()) && connector.equals(other.getType()))
				return true;
		}
		
		return false;
	}

	@Override
	public Lockable getLockableResource() {
		return point();
	}
	
	@Override
	public String toString() {
		return point.toString().concat(connector.toString());
	}

	@Override
	public RailComponent lookAhead(PointConnector direction) {
		if (this.point == direction.point) {
			return direction;				
		}

		return connectedRailLeg.getNextComponent(this, direction);
	}
}
