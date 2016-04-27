package no.ntnu.item.its.osgi.map.model;

import java.util.HashMap;
import java.util.Map;

import no.ntnu.item.its.osgi.map.enums.PointConnectorEnum;

public class Point implements Lockable {
	private Map<PointConnectorEnum, PointConnector> connectors;
	private RailComponentId id;

	private TrainId lockedBy = null;
	private TrainId reservedBy = null;

	
	public Point(RailComponentId id){
		this.id = id;
		connectors = new HashMap<PointConnectorEnum, PointConnector>();
		for (PointConnectorEnum connectorType : PointConnectorEnum.values()) {
			addConnector(new PointConnector(this, connectorType));
		}
	}
	
	public RailComponentId id(){
		return id;
	}
	
	
	public Map<PointConnectorEnum, PointConnector> getConnectors() {
		return connectors;
	}
	
	public PointConnector getConnector(PointConnectorEnum type){
		return connectors.get(type);
	}

	public void addConnector(PointConnector connector) {
		connectors.put(connector.getType(), connector);
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Point) {
			Point other = (Point) arg0;
			if (id.equals(other.id()))
				return true;
		}
		
		return false;
	}
	
	@Override
	public synchronized TrainId checkLock() {
		return lockedBy;
	}
		
	@Override
	public synchronized TrainId checkReservation() {
		return reservedBy;
	}
	
	@Override
	public synchronized void reserveLock(TrainId owner) {
		if (reservedBy == null)
			reservedBy = owner;
	}
	
	@Override
	public synchronized void releaseReservation() {
		reservedBy = null;		
	}
	
	@Override
	public synchronized void performLock(TrainId owner) {
		if (reservedBy == null || reservedBy.equals(owner))
			lockedBy = owner;		
	}
	
	@Override
	public synchronized void unLock() {
		reservedBy = null;
		lockedBy = null;
	}
	
	@Override
	public String toString() {
		return id().toString();
	}
}
