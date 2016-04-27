package no.ntnu.item.its.osgi.publishers.color;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import no.ntnu.item.its.osgi.common.enums.EColor;

public class ColorClassifier {
	
	private static HashMap<ColorMapping, Double> colors = new HashMap<ColorMapping, Double>();
	static {
		colors.put(new ColorMapping(EColor.RED),null);
		colors.put(new ColorMapping(EColor.BLUE),null);
		colors.put(new ColorMapping(EColor.GREEN),null);
		colors.put(new ColorMapping(EColor.GRAY),null);
		colors.put(new ColorMapping(EColor.YELLOW),null);
	}
	
	protected static EColor colorApproximation(int[] rawColor) {
		Map<ColorMapping, Double> distances = (Map<ColorMapping, Double>) colors.clone();

		for (ColorMapping mapping : colors.keySet()) {
			colors.put(mapping, mapping.compareWith(rawColor));
		}

		Entry<ColorMapping, Double> minEntry = null;

		for (Entry<ColorMapping, Double> entry : colors.entrySet()) {
			if (minEntry == null || entry.getValue() < minEntry.getValue())
				minEntry = entry;
		}

		if (minEntry.getValue() > 196) { // Don't make too approximated approximations!
			return EColor.UNKNOWN;
		}

		return minEntry.getKey().getType();
	}
}
