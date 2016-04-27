package no.ntnu.item.its.osgi.common;

public class Pair<T, S> {
	
	protected final T first;
	protected final S second;
	
	public Pair(T first, S second){
		this.first = first;
		this.second = second;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Pair<?, ?>)) {
			return false;
		}
				
		@SuppressWarnings("unchecked")
		Pair<T, S> pair = (Pair<T, S>)other;
		
		return this.contains(pair.first) && this.contains(pair.second);
	}
	
	public boolean contains(Object object){
		return first.equals(object) || second.equals(object);
	}
	
	public T first(){
		return first;
	}
	
	public S second(){
		return second;
	}
}
