package no.ntnu.item.its.osgi.map.model;

public abstract class RouteElement {
	public abstract RouteElement[] getNext(RouteElement previous);
	public abstract int length();
	public abstract Lockable getLockableResource();
}
