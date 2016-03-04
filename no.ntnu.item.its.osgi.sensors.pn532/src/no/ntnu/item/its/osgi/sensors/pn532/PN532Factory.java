package no.ntnu.item.its.osgi.sensors.pn532;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.sensors.pn532.impl.PN532;
import no.ntnu.item.its.osgi.sensors.pn532.impl.PN532I2C;

public class PN532Factory {
	
	private static int NUMBER_OF_TRIES = 10;
	private static IPN532 instance;

	public static IPN532 getInstance() throws SensorInitializationException {
		if (instance == null) {
			instance = initializeInstance();
		}
		
		return instance;
	}
	
	private static IPN532 initializeInstance() throws SensorInitializationException {
		PN532 pn532 = new PN532(new PN532I2C());
		while (!shouldGiveUp()) {
			try {
				pn532.begin();
				if (pn532.SAMConfig())
					return pn532;
			} catch (InterruptedException e) {
				try { Thread.sleep(200); } catch (InterruptedException e1) { e1.printStackTrace(); }
				continue;
			} finally {
				
			}
		}
		
		throw new SensorInitializationException("Could not initialize PN532 sensor");
	}
	
	private static boolean shouldGiveUp() {
		return NUMBER_OF_TRIES-- < 0;
	}
	
}
