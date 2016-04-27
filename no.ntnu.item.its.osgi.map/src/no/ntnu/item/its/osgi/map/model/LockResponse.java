package no.ntnu.item.its.osgi.map.model;

public class LockResponse implements LockingMessage {
	
	private LockRequest request;
	private TrainId respondent;
	private boolean success;
	
	public LockResponse(TrainId respondent, LockRequest request, boolean success) {
		this.request = request;
		this.success = success;
		this.respondent = respondent;
	}
	
	public boolean success() {
		return success;
	}
	
	@Override
	public TrainId collector() {
		return request.collector();
	}
	
	@Override
	public TransactionId transactionId() {
		return request.transactionId();
	}
	
	public TrainId respondent() {
		return respondent;
	}
	
	@Override
	public RequestType type() {
		return request.type();
	}
	
	@Override
	public String toString() {
		return "Transaction: " + transactionId().toString() + ", " + 
				"Respondent: " + respondent.toString() + ", " +
				"Success: " + success;
				
	}
}
