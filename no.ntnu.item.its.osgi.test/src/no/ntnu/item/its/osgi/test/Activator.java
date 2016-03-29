package no.ntnu.item.its.osgi.test;

import java.io.IOException;
import java.util.Arrays;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.sensors.mifare.pn532.IPN532;
import no.ntnu.item.its.osgi.sensors.mifare.pn532.PN532Factory;
import no.ntnu.item.its.osgi.sensors.common.enums.MifareKeyType;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		readMiFareBlock10();
	}

	private void readMiFareBlock10() throws InterruptedException, SensorInitializationException, IOException {
			IPN532 pn = PN532Factory.getInstance();
			byte[] key = new byte[6];
			Arrays.fill(key, (byte)0xFF);
			byte[] uid = new byte[16];
			int uidLen = pn.readPassiveTargetID((byte)0x00, uid);
			if (uidLen > 0) {
				uid = Arrays.copyOf(uid, uidLen);
				pn.authenticateMifareBlock((byte)10, MifareKeyType.A, key, uid);
				byte[] blockContent = new byte[16];
				pn.readMifareBlock((byte)10, blockContent);
				printBuffer(10, blockContent);	
			}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	private static String printSignedHex(byte b) {
		if (b < 0) {
			String s = Integer.toHexString(b);
			return (s.substring(s.length()-2));
		}
		else return (Integer.toHexString(b));
	}

	private static void printBuffer(int i, byte[] dataBuffer) {
		String s = (String.format("Block %d content: [", i));
		for (int j = 0; j < 16; j++) {
			s += printSignedHex( dataBuffer[j] );
		}
		s += ("]");
		System.out.println(s);
	}

}
