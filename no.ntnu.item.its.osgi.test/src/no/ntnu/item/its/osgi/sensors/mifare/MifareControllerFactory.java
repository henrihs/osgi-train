package no.ntnu.item.its.osgi.sensors.mifare;

import javax.naming.SizeLimitExceededException;

import no.ntnu.item.its.osgi.sensors.common.MifareKeyRing;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareController;
import no.ntnu.item.its.osgi.test.StatisticalGenerator;

public class MifareControllerFactory implements MifareController {

	public static MifareController getInstance() {
		return new MifareControllerFactory();
	}

	private StatisticalGenerator statistic;
	
	public MifareControllerFactory() {
		statistic = new StatisticalGenerator(0.1);
	}
	
	@Override
	public void write(int block, MifareKeyRing keyRing, String content)
			throws SensorCommunicationException, SizeLimitExceededException {
	}

	@Override
	public String read(int block, MifareKeyRing keyRing) throws SensorCommunicationException {
		if (statistic.pollDistribution())
			return "This is a dummy location";
		else throw new SensorCommunicationException("No tag found (dummy message)");
	}

}
