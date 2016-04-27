package no.ntnu.item.its.osgi.map.model;

import java.io.Serializable;

public interface Lockable extends Serializable {
	
	public Object id();
	
	public TrainId checkReservation();
	public void reserveLock(TrainId owner);
	public void releaseReservation();
	
	public TrainId checkLock();
	public void performLock(TrainId owner);
	public void unLock();
}
