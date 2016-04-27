package no.ntnu.item.its.osgi.map.model;

import java.util.function.Function;

import bluebrick4j.model.BrickType;
import no.ntnu.item.its.osgi.map.enums.PointConnectorEnum;

public class ConnectorConverter {
	
	public static Function<Integer, PointConnectorEnum> convert(BrickType type){
		if (type == BrickType.LEFTHANDPOINTSWITCH){
			return ConnectorConverter::leftHandPointSwitchConverter;
		}
		else if (type == BrickType.RIGHTHANDPOINTSWITCH){
			return ConnectorConverter::rightHandPointSwitchConverter;
		}
		return null;
	}

	private static PointConnectorEnum rightHandPointSwitchConverter(int i) {
		switch (i) {
		case 0:
			return PointConnectorEnum.ENTRY;
		case 1:
			return PointConnectorEnum.DIVERT;
		case 2:
			return PointConnectorEnum.THROUGH;
		default:
			throw new IllegalArgumentException("Cannot recognize integer ".concat(String.valueOf(i)));
		}
	}
	
	private static PointConnectorEnum leftHandPointSwitchConverter(int i) {
		switch (i) {
		case 0:
			return PointConnectorEnum.ENTRY;
		case 1:
			return PointConnectorEnum.THROUGH;
		case 2:
			return PointConnectorEnum.DIVERT;
		default:
			throw new IllegalArgumentException("Cannot recognize integer ".concat(String.valueOf(i)));
		}
	}
}
