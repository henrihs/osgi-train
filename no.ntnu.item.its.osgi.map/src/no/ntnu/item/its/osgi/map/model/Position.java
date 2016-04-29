package no.ntnu.item.its.osgi.map.model;

import bluebrick4j.model.BrickType;

import java.util.ArrayList;
import java.util.Iterator;

public class Position implements Iterable<RailComponent> {

	private ArrayList<RailComponent> partsCovered;
	private int sizeOfParentObject;
	private PointConnector direction;
	
	public Position(Iterable<RailComponent> arg0, int sizeOfParentObject) {
		partsCovered = new ArrayList<RailComponent>();
		this.sizeOfParentObject = sizeOfParentObject;
		for (RailComponent railPart : arg0) {
			partsCovered.add(0, railPart);
		}
	}
	
	public void updateDirection(PointConnector direction) {
		this.direction = direction;
	}
		
	public RailComponent head(){
		return partsCovered.get(0);
	}
	
	public RailBrick getPreviousBrick(){
		for (RailComponent railComponent : partsCovered) {
			if (railComponent != head() && railComponent instanceof RailBrick)
				return (RailBrick) railComponent;
		}
		return null;
	}
	
	public boolean tryMoveToNextStraight() {
		RailComponent straightAhead = head();
		boolean straightInSight = false;
		while (!straightInSight) {
			straightAhead = straightAhead.lookAhead(direction);
			if (straightAhead instanceof RailBrick && ((RailBrick) straightAhead).getType() == BrickType.STRAIGHT) {
				straightInSight = true;
			} else if (straightAhead instanceof PointConnector) {
				break;
			}
		}
		
		if (!straightInSight) {
			return false;
		}
		
		while (head() != straightAhead ) {
			move();
		}
		
		return true;
	}
	
	public boolean tryMoveToNextTurn() {
		RailComponent turnAhead = head();
		boolean turnInSight = false;
		while (!turnInSight) {
			turnAhead = turnAhead.lookAhead(direction);
			if (turnAhead instanceof RailBrick && ((RailBrick) turnAhead).getType() == BrickType.CURVED) {
				turnInSight = true;
			} else if (turnAhead instanceof PointConnector) {
				break;
			}
		}
		
		if (!turnInSight) {
			return false;
		}
		
		while (head() != turnAhead ) {
			move();
		}
		
		return true;
	}
	
	public boolean tryMoveToNextPoint() {
		Point pointAhead = (Point)direction.getLockableResource();
		
		while (!headIsInPointSwitch()) {
			move();
		}
		
		if (!head().id().equals(pointAhead.id())) {
			return false;
		}
		
		return true;
	}
	
	public boolean moveToEndOfLeg() {
		if (!(head() instanceof RailBrick)) {
			return false;
		}
		
		boolean endReached = false;
		while (!endReached) {
			if (lookAhead(direction) instanceof PointConnector) { endReached = true; }
			else { move(); }
		}
		
		return true;
	}
	
	public boolean moveTo(RailComponentId id) {
		RailComponent componentAhead = head();
		boolean reachedComponent = false;
		while (!reachedComponent) {
			componentAhead = componentAhead.lookAhead(direction);
			if (componentAhead.id().equals(id)) {
				reachedComponent = true;
			}
		}
		
		while (head() != componentAhead) {
			move();
		}
		
		return true;
	}
	
	public RailComponent move() {
		return moveInDirection(direction);
	}
	
	public RailComponent moveInDirection(PointConnector direction) {
		RailComponent nextComponent = lookAhead(direction);
		return moveTo(nextComponent);
	}
	
	public RailComponent moveTo(RailComponent part) {
		partsCovered.add(0, part);
		if (partsCovered.size() > sizeOfParentObject) {
			RailComponent passed = partsCovered.get(partsCovered.size() - 1);
			partsCovered.remove(passed);
			return passed;
		}
		
		return null;
	}
		
	public RailComponent lookAhead(PointConnector direction) {
		return head().lookAhead(direction);
	}
	
	public boolean headIsInPointSwitch(){
		return head() instanceof PointConnector;
	}
	
	public boolean headIsInTurn() {
		return (head() instanceof RailBrick && ((RailBrick)head()).getType() == BrickType.CURVED);
	}
	
	public boolean headIsInStraight() {
		return (head() instanceof RailBrick && ((RailBrick)head()).getType() == BrickType.STRAIGHT);
	}
	
	public void turnAround(){
		ArrayList<RailComponent> reversedParts = new ArrayList<>();
		for (RailComponent railComponent : partsCovered) {
			reversedParts.add(0, railComponent);
		}
		
		partsCovered = reversedParts;
	}
	
	public boolean isTouchingLockable(Lockable lockable) {
		for (RailComponent railComponent : partsCovered) {
			if (lockable.equals(railComponent.partOfElement().getLockableResource()))
				return true;
		}
		
		return false;
	}
	
	public boolean isTouchingElement(RouteElement element) {
		for (RailComponent railComponent : partsCovered) {
			if (element.equals(railComponent.partOfElement()))
				return true;
		}
		
		return false;
	}
	
	@Override
	public Iterator<RailComponent> iterator() {
		Iterator<RailComponent> ipart = partsCovered.iterator();
		return ipart;
	}
}
