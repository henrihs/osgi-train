package no.ntnu.item.its.osgi.map.model;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;

public class Position implements Iterable<RailComponent> {

	private ArrayList<RailComponent> parts;
	private int sizeOfParentObject;
	
	public Position(Iterable<RailComponent> arg0, int sizeOfParentObject) {
		parts = new ArrayList<RailComponent>();
		this.sizeOfParentObject = sizeOfParentObject;
		for (RailComponent railPart : arg0) {
			parts.add(0, railPart);
		}
	}
		
	public RailComponent head(){
		return parts.get(0);
	}
	
	public RailBrick getPreviousBrick(){
		for (RailComponent railComponent : parts) {
			if (railComponent != head() && railComponent instanceof RailBrick)
				return (RailBrick) railComponent;
		}
		return null;
	}
	
	public RailComponent moveTo(RailComponent part) {
		parts.add(0, part);
		if (parts.size() > sizeOfParentObject) {
			RailComponent passed = parts.get(parts.size() - 1);
			parts.remove(passed);
			return passed;
		}
		
		return null;
	}
	
	public RailComponent moveInDirection(PointConnector direction) {
		RailComponent nextComponent = lookAhead(direction);
		return moveTo(nextComponent);
	}
	
	public RailComponent lookAhead(PointConnector direction) {
		return head().lookAhead(direction);
	}
	
	public boolean headIsInPointSwitch(){
		return head() instanceof PointConnector;
	}
	
	public void turnAround(){
		ArrayList<RailComponent> reversedParts = new ArrayList<>();
		for (RailComponent railComponent : parts) {
			reversedParts.add(0, railComponent);
		}
		
		parts = reversedParts;
	}
	
	public boolean isTouchingLockable(Lockable lockable) {
		for (RailComponent railComponent : parts) {
			if (lockable.equals(railComponent.partOfElement().getLockableResource()))
				return true;
		}
		
		return false;
	}
	
	public boolean isTouchingElement(RouteElement element) {
		for (RailComponent railComponent : parts) {
			if (element.equals(railComponent.partOfElement()))
				return true;
		}
		
		return false;
	}
	
	@Override
	public Iterator<RailComponent> iterator() {
		Iterator<RailComponent> ipart = parts.iterator();
		return ipart;
	}
}
