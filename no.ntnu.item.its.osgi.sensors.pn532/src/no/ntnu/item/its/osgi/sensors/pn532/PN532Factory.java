package no.ntnu.item.its.osgi.sensors.pn532;

import no.ntnu.item.its.osgi.sensors.pn532.impl.PN532;
import no.ntnu.item.its.osgi.sensors.pn532.impl.PN532I2C;

public class PN532Factory {

	public static IPN532 getInstance() {
		PN532 pn = new PN532(new PN532I2C());
		try {
			// TODO Try this n times if it fails!
			pn.begin();
			pn.SAMConfig();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pn;
	}
	
}
