package no.ntnu.item.its.osgi.map.model;

public class PositionUpdate {

	public final Position position;
	public final int estimationGrade;
	
	public PositionUpdate(Position position, int estimationGrade) {
		this.estimationGrade = estimationGrade;
		this.position = position;
	}
}
