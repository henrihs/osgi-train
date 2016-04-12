package no.ntnu.item.its.osgi.sensors.mifare.pn532;

import java.io.IOException;

import no.ntnu.item.its.osgi.sensors.mifare.MifareKeyType;

/*
 * This interface represents a PN532 RFID/NFC card for the Raspberry Pi
 */
public interface IPN532 {

	
	/*
	 * Authenticate a single Mifare block
	 * 
	 * @param block the block to authenticate
	 * @param keyType the key type to use (A or B)
	 * @param key the key to use (which is stored in the key block of the given sector)
	 * @param uid the unique id of the Mifare tag (@see readPassiveTargetID)
	 * 
	 * @return true if the authentication was successful
	 */
	boolean authenticateMifareBlock(byte block, MifareKeyType keyType, byte[] key, byte[] uid)
			throws InterruptedException, IOException;
	
	/*
	 * Write to a MiFare block
	 * 
	 * @param blockNumber the block to write to
	 * @param the content to write, should be 16 bytes long
	 * 
	 * @return true if the write was successful
	 */
	boolean writeMifareBlock(int blockNumber, byte[] content) throws InterruptedException, IOException;

	
	/*
	 * Read from a MiFare block
	 * 
	 * @param blockNumber the block to write to
	 * @param the content to write, should be 16 bytes long
	 * 
	 * @return true if the read was successful
	 */
	boolean readMifareBlock(int blockNumber, byte[] buffer) throws InterruptedException, IOException;


	/*
	 * Read the unique ID (uid) of a MiFare-card to a given buffer
	 * 
	 * @param cardbaudrate Baud rate of Mifare card (use 0 for default)
	 * @param buffer Bugger to write response to
	 * 
	 * @return length of response
	 * 
	 */
	int readPassiveTargetID(byte cardbaudrate, byte[] buffer) throws InterruptedException, IOException;
}
