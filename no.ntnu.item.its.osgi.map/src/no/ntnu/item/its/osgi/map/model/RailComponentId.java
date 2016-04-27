package no.ntnu.item.its.osgi.map.model;

public class RailComponentId implements Comparable<RailComponentId> {
	private final String id;
	
	public RailComponentId(long id){
		this.id = String.valueOf(id);
	}
	
	public RailComponentId(String id){
		this.id = id;
	}
	
	@Override
	public String toString(){
		return id;
	}
	
	@Override
	public boolean equals(Object arg0){
		if (arg0 instanceof RailComponentId) {
			RailComponentId other = (RailComponentId) arg0;
			return this.toString().equals(other.toString());			
		}
		return false;
	}
		
	@Override
	public int compareTo(RailComponentId other) {
		return (Integer.valueOf(toString()) - Integer.valueOf(other.toString()));
	}
}
