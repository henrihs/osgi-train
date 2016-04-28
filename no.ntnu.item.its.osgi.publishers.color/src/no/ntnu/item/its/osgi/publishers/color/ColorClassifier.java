package no.ntnu.item.its.osgi.publishers.color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import no.ntnu.item.its.osgi.common.enums.EColor;
import no.ntnu.item.its.osgi.publishers.color.FixedColorData.FixedColor;

public class ColorClassifier {
		
	private static final double MAX_STD_DEV = 2;
	
	private static ArrayList<ColorMapping> colors = new ArrayList<ColorMapping>();
	static {
		colors.add(new ColorMapping(EColor.GRAY));
		colors.add(new ColorMapping(EColor.RED));
		colors.add(new ColorMapping(EColor.BLUE));
		colors.add(new ColorMapping(EColor.GREEN));
		colors.add(new ColorMapping(EColor.YELLOW));
	}
	
	protected static EColor colorApproximation(int[] rawColor) {
		double minEntry = 10000;
		EColor minColor = null;
		
		for (ColorMapping mapping : colors) {
			double diff = mapping.compareWith(rawColor);
//			System.out.print(diff + ", ");
			if (diff < minEntry) {
//				if (diff < 10) {
//					return mapping.getType();
//				}
				minEntry = diff;
				minColor = mapping.getType();
			}
		}

		if (minEntry > 20) { // Don't make too approximated approximations!
			return null;
		}

		return minColor;
	}
	
	protected static EColor colorFiltering(int[] rawColor) {		
		List<FixedColor> nextToFilter = FixedColorData.COLORS;
		for (int i = 0; i < rawColor.length; i++) {
			final int k = i;
			List<FixedColor> filteredValues = nextToFilter.stream()
					.filter(f -> f.rawColorValues[k] > rawColor[k] - f.stdDevs[k] && 
							f.rawColorValues[k] < rawColor[k] + f.stdDevs[k]).collect(Collectors.toList());
			if (filteredValues.size() == 0) {
				return EColor.UNKNOWN;
			} else if (filteredValues.size() == 1) {
				return filteredValues.get(0).color;
			} else {
				nextToFilter = filteredValues;
			}
		}
		
		return EColor.UNKNOWN;
	}
}
