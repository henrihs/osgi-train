package no.ntnu.item.its.osgi.map.model;

import java.util.ArrayList;
import java.util.Iterator;

public class Route implements Iterable<RouteElement> {
	protected ArrayList<RouteElement> viaPoints;
	
	public Route() {
		viaPoints = new ArrayList<>();
	}
	
	public Route(Route copyFrom) {
		viaPoints = (ArrayList<RouteElement>) copyFrom.viaPoints.clone();
	}
	
	public void add(RouteElement viaPoint){
		viaPoints.add(viaPoint);
	}
	
	public int componentLength() {
		return viaPoints.size();
	}
	
	public int brickLength() {
		int i = 0;
		for (RouteElement destination : viaPoints) {
			i += destination.length();
		}
		
		return i;
	}
	
	public void remove(RouteElement element) {
		viaPoints.remove(element);
	}
	
	public RouteElement popFirst() {
		RouteElement first = getFirstElement();
		viaPoints.remove(first);
		return first;
	}
	
	public PointConnector getNextEndOfLeg(){
		RouteElement previous = null;
		for (RouteElement current : viaPoints) {
			if (previous instanceof PointConnector 
					&& current instanceof PointConnector
					&& current != viaPoints.get(0)) {
				return (PointConnector)previous;
			}
			
			previous = current;
		}
		
		return null;
	}
	
	public PointConnector getNextDirection(PointConnector currentDirection) {
		int currentDirectionIndex = viaPoints.indexOf(currentDirection);
		RouteElement previous = null;
		for (int i = currentDirectionIndex + 1; i < viaPoints.size(); i++) {
			RouteElement current = viaPoints.get(i);
			
			if (previous instanceof PointConnector 
					&& current instanceof PointConnector) {
				return (PointConnector)current;
			}
			
			previous = current;
		}
		
		return currentDirection.getConnectedRailLeg().getOppositeConnector(currentDirection);
	}
	
	public RouteElement getFirstElement() {
		return viaPoints.get(0);
	}
		
	public RouteElement getLastElement() {
		return viaPoints.get(viaPoints.size()-1);
	}
	
	public ArrayList<RouteElement> getClonedList() {
		return (ArrayList<RouteElement>) viaPoints.clone(); 
	}

	@Override
	public Iterator<RouteElement> iterator() {
		Iterator<RouteElement> iviapoints = viaPoints.iterator();
		return iviapoints;
	}
	
	public RouteElement getNext(RouteElement current) {
		int currentIndex = viaPoints.indexOf(current);
		if (currentIndex < viaPoints.size()) {
			return viaPoints.get(currentIndex+1);
		}
		
		return null;
	}
	
	public boolean contains(RouteElement element) {
		return viaPoints.contains(element);
	}
	
	@Override
	public String toString() {
		String s = "";
		for (RouteElement routeElement : viaPoints) {
			if (routeElement instanceof RailLeg) {
				s += (routeElement.toString()).concat("\n");
			}
		}
		return s.substring(0, s.length());
	}
}
