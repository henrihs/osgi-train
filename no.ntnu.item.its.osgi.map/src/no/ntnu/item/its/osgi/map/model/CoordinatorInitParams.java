package no.ntnu.item.its.osgi.map.model;

import java.util.Set;

public class CoordinatorInitParams {
	private TrainId coordinatorId;
	private TransactionId transactionId;
	private Set<Lockable> objectsToLock;
	private Set<TrainId> participants;

	public CoordinatorInitParams(TrainId coordinatorId, TransactionId transactionId, Set<Lockable> objectsToLock, Set<TrainId> participants) {
		this.coordinatorId = coordinatorId;
		this.transactionId = transactionId;
		this.objectsToLock = objectsToLock;
		this.participants = participants;
	}
	
	public TrainId getCoordinatorId() {
		return coordinatorId;
	}

	public TransactionId getTransactionId() {
		return transactionId;
		
	}
	
	public Set<Lockable> getObjectsToLock() {
		return objectsToLock;
	}

	public Set<TrainId> getParticipants() {
		return participants;
	}
}
