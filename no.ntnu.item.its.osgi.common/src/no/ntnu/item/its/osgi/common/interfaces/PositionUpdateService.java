package no.ntnu.item.its.osgi.common.interfaces;

import no.ntnu.item.its.osgi.map.model.PointConnector;

public interface PositionUpdateService {
	public static String MAP_FILE_NAME = "map.bbm";
	
	public static String EVENT_TOPIC = "no/ntnu/item/its/osgi/train/positioning/update";
	public static String MAP_UPDATE_KEY = "map.update";
	public static String POS_UPDATE_KEY = "pos.update";
	public static String ESTIMATIONGRADE_KEY = "map.estimationgrade";
	
	public void updateDirection(PointConnector direction);
}
