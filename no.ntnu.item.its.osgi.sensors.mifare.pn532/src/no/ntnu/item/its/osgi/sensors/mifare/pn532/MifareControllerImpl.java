package no.ntnu.item.its.osgi.sensors.mifare.pn532;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.naming.SizeLimitExceededException;

import no.ntnu.item.its.osgi.sensors.mifare.MifareControllerService;
import no.ntnu.item.its.osgi.sensors.mifare.MifareKeyRing;
import no.ntnu.item.its.osgi.sensors.mifare.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.mifare.SensorInitializationException;

public class MifareControllerImpl implements MifareControllerService {

	private final IPN532 pn532;
	private static final byte CARDBAUDRATE = 0x00;

	public MifareControllerImpl() throws SensorInitializationException {
		pn532 = PN532Factory.getInstance();
	}
	
	@Override
	public String readTagUID() throws SensorCommunicationException {
		byte[] uid = readUid();
		String strUid = "0x";
		String s;
		for (byte b : uid) {
			if (b < 0) {
				s = Integer.toHexString(b);
				strUid += s.substring(s.length()-2); 
			} else {
				s = Integer.toHexString(b);
				if (s.length() < 2) {
					strUid += "0";
				}
				
				strUid += s;				
			}
		}
		
		return strUid;
	}
		
	@Override
	public void write(int block, MifareKeyRing keyRing, String content) throws SensorCommunicationException, SizeLimitExceededException {
		if (!authenticate(block, keyRing)) {
			throw new SensorCommunicationException("Unknown authentication error occured");
		}
		
		try {
			byte[] byteContent = pad(content.getBytes(MifareControllerService.CHARSET));
			if (!pn532.writeMifareBlock(block, byteContent)) {
				throw new SensorCommunicationException(String.format("Could not write data to block %d, check your connection", block));
			}
		} catch (UnsupportedEncodingException e) {
			// Should never get here
			e.printStackTrace();
		} catch (InterruptedException | IOException e) {
			throw new SensorCommunicationException(String.format("Could not write data to block %d", block), e);
		}
	}

	@Override
	public String read(int block, MifareKeyRing keyRing) throws SensorCommunicationException {
		if (!authenticate(block, keyRing)) {
			throw new SensorCommunicationException("Unknown authentication error occured");
		}
		
		byte[] bytesRead = new byte[16];
		try {
			if (!pn532.readMifareBlock(block, bytesRead)) {
				throw new SensorCommunicationException(String.format("Could not read data from block %d, check your connection", block));
			}
		} catch (InterruptedException | IOException e) {
			throw new SensorCommunicationException(String.format("Could not read data from block %d", block), e);
		}
		
		return parse(bytesRead);
	}
		
	private byte[] pad(byte[] oldBytes) throws SizeLimitExceededException {
		if (oldBytes.length > 16) {
			throw new SizeLimitExceededException("Too large data set");
		}
		
		byte[] newBytes = new byte[16];
		int diff = newBytes.length-oldBytes.length;
		for (int i = 0; i < newBytes.length; i++) {
			newBytes[i] = i >= diff ? oldBytes[i-diff] : 0x00;
		}
		
		return newBytes;
	}
	
	private static String parse(byte[] array) {
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		for (byte b : array) {
			if (b != 0x00) {
				bytes.add(b);
			}
		}
		
		byte[] b = new byte[bytes.size()];
		for (int i = 0; i < b.length; i++) {
			b[i] = bytes.get(i);
		}
		
		return new String(b);
	}
	
	private boolean authenticate(int block, MifareKeyRing keyRing) throws SensorCommunicationException {
		byte[] uid = readUid();
		try {
			return pn532.authenticateMifareBlock((byte)10, keyRing.type, keyRing.key, uid);
		} catch (InterruptedException | IOException e) {
			throw new SensorCommunicationException("Authentication error", e);
		}		
	}

	private byte[] readUid() throws SensorCommunicationException {
		byte[] uid = new byte[16];
		int uidLen;
		try {
			uidLen = pn532.readPassiveTargetID(CARDBAUDRATE, uid);
			if (uidLen <= 0)
				throw new IOException();
		} catch (InterruptedException | IOException e) {
			throw new SensorCommunicationException("Could authenticate mifare card, no card found!");
		}
		
		uid = Arrays.copyOf(uid, uidLen);
		return uid;
	}

}
