package no.ntnu.item.its.osgi.map.model;

import java.util.HashSet;
import java.util.Set;

public class LockRequest implements LockingMessage {
	
	private final TransactionId id;
	private final TrainId collector;
	private final Set<String> objectsIDs;
	private RequestType type;
	
	public LockRequest(TransactionId id, TrainId collector, Set<Lockable> objects, RequestType type) {
		this.id = id;
		this.collector = collector;
		this.type = type;
		objectsIDs = new HashSet<>();
		for (Lockable lockable : objects) {
			objectsIDs.add(lockable.id().toString());
		}
	}

	@Override
	public TransactionId transactionId() {
		return id;
	}
	
	@Override
	public TrainId collector() {
		return collector;
	}
	
	public Set<String> lockableIDs() {
		return objectsIDs;
	}
	
	public void setType(RequestType type) {
		this.type = type;
	}
	
	@Override
	public RequestType type() {
		return type;
	}
}
