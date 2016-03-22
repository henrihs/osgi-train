package no.ntnu.item.its.osgi.sensors.mifare;

import java.io.IOException;
import java.util.Arrays;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.sensors.mifare.IPN532.MifareKeyType;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext arg0) throws Exception {
		Activator.context = arg0;
		ServiceReference<LogService> logRef = context.getServiceReference(LogService.class);
		try {
			PN532Factory.initializeInstance(100);
		} catch (Exception e) {
			logAndStop(arg0, logRef, e);
		}
		if (PN532Factory.getInstance() != null) {
			context.getService(logRef).log(LogService.LOG_INFO, "PN532 sensor successfully initialized");;
		} else logAndStop(arg0, logRef, new SensorInitializationException("Could not initialize PN532 sensor"));
	}

	private void logAndStop(BundleContext arg0, ServiceReference<LogService> logRef, Exception e) throws Exception {
		context.getService(logRef).log(LogService.LOG_ERROR, e.getMessage(), e);
		stop(arg0);
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		Activator.context = null;

	}

	public static void main(String[] args) {
		IPN532 pn;
		try {
			PN532Factory.initializeInstance(20);
			readMiFareBlock10();
		} catch (SensorInitializationException | InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	private static void readMiFareBlock10() throws InterruptedException, SensorInitializationException, IOException {
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
