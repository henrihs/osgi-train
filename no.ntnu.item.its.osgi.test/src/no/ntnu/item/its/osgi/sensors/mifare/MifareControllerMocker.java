package no.ntnu.item.its.osgi.sensors.mifare;

import javax.naming.SizeLimitExceededException;

import no.ntnu.item.its.osgi.sensors.common.MifareKeyRing;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareControllerService;
import no.ntnu.item.its.osgi.test.StatisticalGenerator;

public class MifareControllerMocker implements MifareControllerService {

	private StatisticalGenerator statistic;
	
	public MifareControllerMocker() {
		statistic = new StatisticalGenerator(0.1);
	}
	
	@Override
	public void write(int block, MifareKeyRing keyRing, String content)
			throws SensorCommunicationException, SizeLimitExceededException {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String read(int block, MifareKeyRing keyRing) throws SensorCommunicationException {
		if (statistic.pollDistribution())
			return "This is a dummy location";
		else throw new SensorCommunicationException("No tag found (dummy message)");
	}

}
