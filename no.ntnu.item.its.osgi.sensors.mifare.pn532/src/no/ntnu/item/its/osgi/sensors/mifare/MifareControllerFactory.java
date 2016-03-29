package no.ntnu.item.its.osgi.sensors.mifare;

import java.io.IOException;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.MifareController;
import no.ntnu.item.its.osgi.sensors.mifare.pn532.MifareControllerImpl;

public class MifareControllerFactory {
	private static MifareController instance;

	public static MifareController getInstance() throws SensorInitializationException, InterruptedException, IOException {
		return new MifareControllerImpl();
	}
}
