package no.ntnu.item.its.osgi.publishers.color;

import java.util.HashMap;
import java.util.Map;

import no.ntnu.item.its.osgi.common.enums.EColor;

public class ColorMapping {
	
	private static final Map<EColor,int[]> FIXED_COLORS;
	static {
		 FIXED_COLORS = new HashMap<EColor, int[]>();
		 FIXED_COLORS.put(EColor.GREEN, new int[] {36, 7, 18, 11});
		 FIXED_COLORS.put(EColor.BLUE, new int[] {51, 6, 17, 30});
		 FIXED_COLORS.put(EColor.RED, new int[] {36, 24, 7, 7});
		 FIXED_COLORS.put(EColor.YELLOW, new int[] {138, 59, 56, 25});
		 FIXED_COLORS.put(EColor.GRAY, new int[] {22, 7, 8, 7});
	}
	
	private final EColor type;
	private final int clearValue;
	private final int redValue;
	private final int greenValue;
	private final int blueValue;

	public ColorMapping(EColor colorEnum) {
		type = colorEnum;
		clearValue = FIXED_COLORS.get(colorEnum)[0];
		redValue = FIXED_COLORS.get(colorEnum)[1];
		greenValue = FIXED_COLORS.get(colorEnum)[2];
		blueValue = FIXED_COLORS.get(colorEnum)[3];
	}
	
	public EColor getType() {
		return type;
	}
	
	public int getClearValue() {
		return clearValue;
	}

	public int getRedValue() {
		return redValue;
	}

	public int getGreenValue() {
		return greenValue;
	}

	public int getBlueValue() {
		return blueValue;
	}
	
	public double compareWith(int[] rawColor) {
		
		int redDiff = componentFromRaw(clearValue, redValue) - componentFromRaw(rawColor[0], rawColor[1]);
		int greenDiff = componentFromRaw(clearValue, greenValue) - componentFromRaw(rawColor[0], rawColor[2]);
		int blueDiff = componentFromRaw(clearValue, blueValue) - componentFromRaw(rawColor[0], rawColor[3]);
		  
		return redDiff*redDiff + greenDiff*greenDiff + blueDiff*blueDiff;
	}
	
	private int componentFromRaw(int clear, int color) {
		return color*255/clear;
	}

}
