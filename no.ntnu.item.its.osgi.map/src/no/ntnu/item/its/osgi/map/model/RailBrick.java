package no.ntnu.item.its.osgi.map.model;

import bluebrick4j.model.BrickType;

public class RailBrick implements RailComponent {
	
	private RailComponentId id;
	private final RailLeg parentLeg;
	private final int sleepers;
	
	public RailBrick(String id, RailLeg parent, BrickType brickType) {
		this.id = new RailComponentId(id);
		parentLeg = parent;
		if (brickType == BrickType.CURVED || brickType == BrickType.STRAIGHT) {
			this.sleepers = 4;
		}
		else {
			this.sleepers = 10000;
		}
	}
	
	public RailLeg parentLeg(){
		return this.parentLeg;
	}
	
	public int sleepers(){
		return sleepers;
	}

	@Override
	public RailComponentId id() {
		return id;
	}

	@Override
	public RouteElement partOfElement() {
		return parentLeg();
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof RailBrick) {
			RailBrick other = (RailBrick) arg0;
			if (id.equals(other.id()) && parentLeg.equals(other.parentLeg()))
				return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "B".concat(id.toString());
	}

	@Override
	public RailComponent lookAhead(PointConnector direction) {
		return parentLeg().getNextComponent(this, direction);
	}
}
