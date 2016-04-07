package no.ntnu.item.its.osgi.sensors.accel;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;
import no.ntnu.item.its.osgi.test.StatisticalGenerator;

public class AccelerationControlerMocker implements AccelerationControllerService {
	
	private StatisticalGenerator statistic;

	public AccelerationControlerMocker() {
		statistic = new StatisticalGenerator(0.1);
	}

	@Override
	public int[] getRawData() throws SensorCommunicationException, SensorInitializationException {
		return new int[] {statistic.newInt(), statistic.newInt(), statistic.newInt()};
	}

	@Override
	public int[] getCalibratedData() throws SensorCommunicationException, SensorInitializationException {
		return getRawData();
	}

}
