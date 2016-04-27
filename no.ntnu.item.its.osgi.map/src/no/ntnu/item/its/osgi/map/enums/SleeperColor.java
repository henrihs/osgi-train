package no.ntnu.item.its.osgi.map.enums;

public enum SleeperColor {
	RED, GREEN, BLUE, YELLOW, WHITE, BLACK, BROWN, NONE;
	
	public static SleeperColor convertFromLejosColor(int lejosInt) {
		switch (lejosInt) {
		case 0:
			return RED;
		case 1:
			return GREEN;
		case 2:
			return BLUE;
		case 3:
			return YELLOW;
		case 6:
			return WHITE;
		case 7:
			return BLACK;
		case 13:
			return BROWN;
		default:
			return NONE;
		}
	}
	
	public static SleeperColor regularSleeper() {
		return SleeperColor.BLACK;
	}
	
	public PointConnectorEnum convertToConnector() {
		switch (this) {
		
		case RED:
			return PointConnectorEnum.ENTRY;
		case YELLOW:
			return PointConnectorEnum.THROUGH;
		case GREEN:
			return PointConnectorEnum.DIVERT;
		
		default:
			return null;
		}
	}
}