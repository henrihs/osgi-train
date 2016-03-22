package no.ntnu.item.its.osgi.sensors.color.tcs34725;

import java.util.HashMap;
import java.util.Map;

import no.ntnu.item.its.osgi.sensors.common.enums.EColor;
import no.ntnu.item.its.osgi.sensors.common.interfaces.IColor;

public class Color implements IColor {
	
	private static final Map<EColor,int[]> COLORS;
	static {
		 COLORS = new HashMap<EColor, int[]>();
		 COLORS.put(EColor.GREEN, new int[] {36, 7, 18, 11});
		 COLORS.put(EColor.BLUE, new int[] {51, 6, 17, 30});
		 COLORS.put(EColor.RED, new int[] {36, 24, 7, 7});
		 COLORS.put(EColor.YELLOW, new int[] {138, 59, 56, 25});
		 COLORS.put(EColor.GRAY, new int[] {22, 7, 8, 7});
	}
	
	private int clearValue;
	private int redValue;
	private int greenValue;
	private int blueValue;

	public Color(EColor colorEnum) {
		setValues(COLORS.get(colorEnum));
	}
	
	private void setValues(int[] values){
		clearValue = values[0];
		redValue = values[1];
		greenValue = values[2];
		blueValue = values[3];
	}
	
	@Override
	public int getClearValue() {
		return clearValue;
	}

	@Override
	public int getRedValue() {
		return redValue;
	}

	@Override
	public int getGreenValue() {
		return greenValue;
	}

	@Override
	public int getBlueValue() {
		return blueValue;
	}

}
