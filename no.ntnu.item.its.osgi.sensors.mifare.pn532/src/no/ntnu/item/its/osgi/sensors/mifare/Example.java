package no.ntnu.item.its.osgi.sensors.mifare;

import no.ntnu.item.its.osgi.sensors.mifare.pn532.MifareControllerImpl;

public class Example {

	public static void main(String[] args) {
		MifareControllerService sensor = null; 
		try {
			sensor = new MifareControllerImpl();
		} catch (SensorInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (true) {
			try {
				String uid = sensor.readTagUID();
				System.out.println(String.format("Detected tag with UID: %s", uid));
				String block42Content = sensor.read(42, new MifareKeyRing(MifareKeyType.A));
				System.out.println(String.format("Content of block 42 is: %s", block42Content));
			} catch (SensorCommunicationException e) {
			} finally {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
}
