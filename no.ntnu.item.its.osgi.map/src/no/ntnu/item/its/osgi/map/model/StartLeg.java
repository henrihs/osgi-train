package no.ntnu.item.its.osgi.map.model;

import java.util.ArrayList;

public class StartLeg extends RailLeg {
	
	private PointConnector connector;
	
	public StartLeg(PointConnector connector) {
		super();
		this.connector = connector;
		if (connector != null) {
			connector.setConnectedRailLeg(this);
		}
	}
	
	public PointConnector getConnector() {
		return connector;
	}

	@Override
	public RouteElement[] getNext(RouteElement previous) {
		return new RouteElement[] { connector };
	}

	@Override
	public RailComponent getNextComponent(RailComponent previous, PointConnector direction) {
		if (direction.point() != connector.point()) {
			return null;
		}
		
		int previousIndex = railBricks.indexOf(previous);
		int nextIndex = previousIndex + 1;
		if (nextIndex >= railBricks.size()) {
			return connector;
		}
		
		return railBricks.get(nextIndex);
	}
	
	public Iterable<RailComponent> getStartOfLeg(int components) {
		if (components > railBricks.size())
			throw new IllegalArgumentException("This StartLeg has too few components to accomodate the vessel");
		return new ArrayList<RailComponent>(railBricks.subList(0, components - 1));
	}

	@Override
	public String id() {
		return connector.id().toString().concat(".");
	}

	@Override
	public PointConnector getOppositeConnector(PointConnector connector) {
		// TODO Auto-generated method stub
		return null;
	}
}
