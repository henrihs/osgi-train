package no.ntnu.item.its.osgi.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.management.ServiceNotFoundException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.sensors.pn532.IPN532;
import no.ntnu.item.its.osgi.sensors.pn532.IPN532.MifareKeyType;
import no.ntnu.item.its.osgi.sensors.pn532.PN532Factory;

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
		Filter filter = FrameworkUtil.createFilter("(service.scope=singleton)");
		Object[] loggerRef = context.getServiceReferences(LogService.class, filter.toString()).toArray();
		if (loggerRef.length > 0 && loggerRef[0] != null ) {
			LogService logger = (context.getService((ServiceReference<LogService>) loggerRef[0]));
			System.out.println(logger.getClass().getName());
			readMiFareBlock10(logger);
		}
		else throw new ServiceNotFoundException("Could not get LogService");
	}

	private void readMiFareBlock10(LogService logger) throws InterruptedException {
		try {
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

				printBuffer(10, blockContent, logger);			
			}
//		} catch (SensorInitializationException e) {
//			logger.log(LogService.LOG_DEBUG, e.getMessage());
//			try {
//				stop(context);
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		} catch (Exception e) {
			logger.log(LogService.LOG_DEBUG, e.getMessage());
			try {
				stop(context);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

	private static void printBuffer(int i, byte[] dataBuffer, LogService logger) {
		String s = (String.format("Block %d content: [", i));
		for (int j = 0; j < 16; j++) {
			s += printSignedHex( dataBuffer[j] );
		}
		s += ("]");
		logger.log(LogService.LOG_INFO, s);
	}

}
