package no.ntnu.item.its.osgi.map.model;

public class TrainId {

	private final String value;
	public static final String ID_KEY = "TRAIN_ID";
	public static final String VESSEL_SIZE_KEY = "TRAIN_SIZE";
	
	public TrainId(String value){
		if (isValid(value)) this.value = value;
		else throw new IllegalArgumentException("Invalid train id: ".concat(value));
	}


	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof TrainId) {
			TrainId other = (TrainId) arg0;
			if (toString().equals(other.toString()))
				return true;
		}
		
		return false;
	}
	
//	@Override
//	public boolean equals(TransactionId arg0) {
//		if (id.equals(arg0.toString())) {
//			return true;
//		}
//		
//		return false;
//	}
	
	private boolean isValid(String value) {
		
		return true;
	}
}
