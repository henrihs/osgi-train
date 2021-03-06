package no.ntnu.item.its.osgi.sensors.mifare.pn532;

import java.io.IOException;

import no.ntnu.item.its.osgi.common.exceptions.SensorInitializationException;

public class PN532Factory {
	
	private static IPN532 instance;

	static IPN532 getInstance() throws SensorInitializationException, InterruptedException, IOException {
		if (instance == null) {
			initializeInstance(1000);
		}
		
		return instance;
	}
	
	static void initializeInstance(int retries) {
		PN532 pn532 = new PN532(new PN532I2C());
		while (retries-- > 0) {
			try {
				pn532.begin();
				pn532.SAMConfig();
				break;
			} catch (IOException | InterruptedException e) {
				try { Thread.sleep(200); } catch (InterruptedException e1) { e1.printStackTrace(); }
				continue;
			}
		}
				
		instance = pn532;
	}
}
