package no.ntnu.item.its.osgi.sensors.mifare.pn532;

import java.io.IOException;

import no.ntnu.item.its.osgi.sensors.mifare.SensorInitializationException;

public class PN532Factory {
	
	private static IPN532 instance;

	static IPN532 getInstance() throws SensorInitializationException {
		if (instance == null) {
			initializeInstance(1000);
		}
		
		return instance;
	}
	
	static void initializeInstance(int retries) throws SensorInitializationException {
		int n = retries;
		boolean finished = false;
		PN532 pn532 = new PN532(new PN532I2C());
		while (retries-- > 0) {
			try {
				pn532.begin();
				pn532.SAMConfig();
				finished = true;
				break;
			} catch (IOException | InterruptedException e) {
				try { Thread.sleep(200); } catch (InterruptedException e1) { e1.printStackTrace(); }
				continue;
			}
		}
				
		if (!finished) {
			throw new SensorInitializationException(String.format("Could not initialize sensor after %d retries", n));
		}
		instance = pn532;
	}
}
