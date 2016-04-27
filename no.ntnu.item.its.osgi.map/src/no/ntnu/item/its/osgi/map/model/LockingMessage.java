package no.ntnu.item.its.osgi.map.model;

public interface LockingMessage {

	TrainId collector();

	TransactionId transactionId();

	RequestType type();

}