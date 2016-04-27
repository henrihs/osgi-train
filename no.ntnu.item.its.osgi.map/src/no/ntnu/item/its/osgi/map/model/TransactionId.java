package no.ntnu.item.its.osgi.map.model;

public class TransactionId {
	public String id;
	
	public TransactionId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return id;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof TransactionId) {
			TransactionId other = (TransactionId) arg0;
			if (id.equals(other.toString())) {
				return true;
			}
		}
		
		return false;
	}
}
