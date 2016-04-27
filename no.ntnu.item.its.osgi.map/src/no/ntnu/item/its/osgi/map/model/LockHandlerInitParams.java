package no.ntnu.item.its.osgi.map.model;

import java.util.Set;

public class LockHandlerInitParams extends Pair<TrainId, Set<TrainId>> {

	public LockHandlerInitParams(TrainId id, Set<TrainId> participants) {
		super(id, participants);
	}
	
	public TrainId id() {
		return first();
	}
	
	public Set<TrainId> participants() {
		return second();
	}

}
