package no.ntnu.item.its.osgi.sensors.mifare;

import java.util.Arrays;

public class MifareKeyRing {

	public final MifareKeyType type;
	public final byte[] key; 
	
	
	/*
	 * Key for authenticating mifare block
	 */
	public MifareKeyRing(MifareKeyType type, byte[] key) {
		this.type = type;
		this.key = key;
	}
	
	/*
	 * Key for authenticating mifare block (with default key)
	 */
	public MifareKeyRing(MifareKeyType type) {
		this.type = type;
		key = new byte[6];
		Arrays.fill(key, (byte)0xFF);
	}
}
