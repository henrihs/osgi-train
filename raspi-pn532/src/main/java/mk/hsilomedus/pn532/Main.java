package mk.hsilomedus.pn532;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioTrigger;

public class Main {

	static final byte PN532_MIFARE_ISO14443A = 0x00;

	public static void main(String[] args) throws InterruptedException {
		IPN532Interface pn532Interface = new PN532I2C();
		PN532 nfc = new PN532(pn532Interface);

		final GpioController gpio = GpioFactory.getInstance();

		// provision gpio pin #02 as an input pin with its internal pull down resistor enabled
		final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.PULL_DOWN);
		
				// Start
		System.out.println("Starting up...");
		nfc.begin();
		Thread.sleep(1000);

		// configure board to read RFID tags

		long versiondata = nfc.getFirmwareVersion();
		if (versiondata == 0) {
			System.out.println("Didn't find PN53x board");
			return;
		}
		// Got ok data, print it out!
		System.out.print("Found chip PN5");
		System.out.println(Long.toHexString((versiondata >> 24) & 0xFF));

		System.out.print("Firmware ver. ");
		System.out.print(Long.toHexString((versiondata >> 16) & 0xFF));
		System.out.print('.');
		System.out.println(Long.toHexString((versiondata >> 8) & 0xFF));
		nfc.SAMConfig();
		try {
			read(nfc);
		} catch (LolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//		while (!nfc.SAMConfig()) {
		//			System.out.println("Retrying to set SAMConfig");
		//			Thread.sleep(200);
		//		}

		System.out.println("Waiting for an ISO14443A Card ...");
		System.out.println(myButton.getState());
		
		myButton.addTrigger(new GpioCallbackTrigger(PinState.LOW, new Callable<Void>() {
			private boolean waitForAck = false;
			
			@Override
			public Void call() throws Exception {
				System.out.println("LOW RECEIVED");
				if (waitForAck) {
					waitForAck = false;
				}
				else {
					System.out.println("TRIGGERING READ");
					try {
						read(nfc);
					} catch (LolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					waitForAck = true;
				}
				return null;
			}
		}));
		
		myButton.addTrigger(new GpioCallbackTrigger(PinState.HIGH, new Callable<Void>() {
			
			@Override
			public Void call() throws Exception {
				System.out.println("HIGH RECEIVED");
				return null;
			}
		}));
		
		
		for (;;) {
			Thread.sleep(500);
		}
	}

	private static void read(PN532 nfc) throws InterruptedException, LolException {
		byte[] buffer = new byte[8];
		byte[] key = new byte[6];
		Arrays.fill(key, (byte)0xFF);
		int readLength = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, buffer);

		if (readLength > 0) {
			System.out.println("Found an ISO14443A card");

			System.out.print("  UID Length: ");
			System.out.print(readLength);
			System.out.println(" bytes");

			System.out.print("  UID Value: [");
			byte[] uid = new byte[readLength];
			for (int i = 0; i < readLength; i++) {
				uid[i] = buffer[i];
				printSignedHex(buffer[i]);
			}
			System.out.println("]");

//			readBlock(nfc, key, uid);
		}
		else throw new LolException();
	}

	private static void readBlock(PN532 nfc, byte[] key, byte[] uid) throws InterruptedException {
		int block = 42;
		System.out.println(String.format("Reading block %d:", block));
		Thread.sleep(5);
		byte[] dataBuffer = new byte[16];
		//			nfc.authenticateMifareBlock((byte) block, key, uid);
		//			writeFixedDataToBlock(nfc, block, key, uid);
		nfc.authenticateMifareBlock((byte) block, key, uid);
		boolean result = nfc.readMifareBlock(block, dataBuffer);
		if (result) {
			printBuffer(block, dataBuffer);
		} else System.out.println("Failed to read block " + block);
	}

	private static void printSignedHex(byte b) {
		if (b < 0) {
			String s = Integer.toHexString(b);
			System.out.print(s.substring(s.length()-2));
		}
		else System.out.print(Integer.toHexString(b));
	}

	private static void printBuffer(int i, byte[] dataBuffer) {
		System.out.print(String.format("Block %d content: [", i));
		for (int j = 0; j < 16; j++) {
			printSignedHex( dataBuffer[j] );
		}
		System.out.println("]");
	}

	private static void writeFixedDataToBlock(PN532 nfc, int block, byte[] key, byte[] uid) throws InterruptedException {
		if (!nfc.authenticateMifareBlock((byte)block, key, uid)) {
			System.out.println("Failed to authenticate block " + block + "!");
			return;
		}
		System.out.println(String.format("Writing data to block %d:", block));
		byte[] data = new byte[16];
		Arrays.fill(data, (byte)0);
		if (nfc.writeMifareBlock(block, data))
			System.out.println(String.format("Wrote data to block %d.", block));
	}
}