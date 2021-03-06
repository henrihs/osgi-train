package no.ntnu.item.its.osgi.common.interfaces;

import javax.naming.SizeLimitExceededException;

import no.ntnu.item.its.osgi.common.MifareKeyRing;
import no.ntnu.item.its.osgi.common.exceptions.NoCardFoundException;
import no.ntnu.item.its.osgi.common.exceptions.SensorCommunicationException;

/*
 * This is the public API of a Mifare reader/writer unit
 * 
 * The API encapsulates the inner workings of the reader/writer
 */
public interface MifareControllerService {
	
	public static String CHARSET = "utf-8";
	public static String EVENT_TOPIC = "no/ntnu/item/its/osgi/sensors/mifare";
	public static String LOC_ID_KEY = "LOC_ID";

	/*
	 * Write a block of data to a Mifare tag
	 * 
	 * @param block the block to write to
	 * @param keyRing the key used to authenticate the Mifare block in question
	 * @param content the data to write to the block (maximum size is 16 bytes)
	 */
	public void write(int block, MifareKeyRing keyRing, String content) throws SensorCommunicationException, SizeLimitExceededException, NoCardFoundException;
	
	/*
	 * Read a block of data from a Mifare tag
	 * 
	 * @param block the block to read from
	 * @param keyRing the key used to authenticate the Mifare block in question
	 * 
	 * @returns the content from the block in question, represented as a String value
	 */
	public String read(int block, MifareKeyRing keyRing) throws SensorCommunicationException, NoCardFoundException;
	
}
