package no.ntnu.item.its.osgi.sensors.color;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.ColorController;
import no.ntnu.item.its.osgi.test.StatisticalGenerator;

public class ColorControllerFactory implements ColorController {
	
	public static int[][] colors = new int[][] {
		{36, 7, 18, 11},
		{51, 6, 17, 30},
		{36, 24, 7, 7},
		{138, 59, 56, 25},
		{22, 7, 8, 7}
	};
	
	public static ColorController getInstance() {
		return new ColorControllerFactory();
	}

	private StatisticalGenerator statistic;
	
	public ColorControllerFactory() {
		statistic = new StatisticalGenerator(0.1);
	}

	@Override
	public int[] getRawData() throws SensorCommunicationException {
		int i = statistic.newInt();
		return colors[i/20];
	}

}
