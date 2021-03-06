package no.ntnu.item.its.osgi.sensors.mifare.pn532;

import java.io.IOException;
import java.util.Arrays;

import no.ntnu.item.its.osgi.common.enums.MifareKeyType;
import no.ntnu.item.its.osgi.common.enums.Status;

public class PN532 implements IPN532 {

	static final int PN532_COMMAND_GETFIRMWAREVERSION = 0x02;
	static final int PN532_COMMAND_SAMCONFIGURATION = 0x14;
	static final int PN532_COMMAND_INLISTPASSIVETARGET = 0x4A;
	static final int PN532_COMMAND_INDATAEXCHANGE = 0x40;
	
	// Mifare Commands
	static final int MIFARE_CMD_AUTH_A = 0x60;
	static final int MIFARE_CMD_AUTH_B = 0x61;
	static final int MIFARE_CMD_READ = 0x30;
	static final int MIFARE_CMD_WRITE = 0xA0;
	static final int MIFARE_CMD_TRANSFER = 0xB0;
	static final int MIFARE_CMD_DECREMENT = 0xC0;
	static final int MIFARE_CMD_INCREMENT = 0xC1;
	static final int MIFARE_CMD_STORE = 0xC2;
	static final int MIFARE_ULTRALIGHT_CMD_WRITE = 0xA2;
	

	private IPN532Interface medium;
	private byte[] pn532_packetbuffer;

	public PN532(IPN532Interface medium) {
		this.medium = medium;
		this.pn532_packetbuffer = new byte[64];
	}
	
	public void begin() throws IOException {
		medium.begin();
		medium.wakeup();
	}

	public long getFirmwareVersion() throws InterruptedException, IOException {
		long response;

		byte[] command = new byte[1];
		command[0] = PN532_COMMAND_GETFIRMWAREVERSION;

		if (medium.writeCommand(command) != CommandStatus.OK) {
			return 0;
		}

		// read data packet
		int status = medium.readResponse(pn532_packetbuffer, 12);
		if (status < 0) {
			return 0;
		}

		int offset = 0; // medium.getOffsetBytes();

		response = pn532_packetbuffer[offset + 0];
		response <<= 8;
		response |= pn532_packetbuffer[offset + 1];
		response <<= 8;
		response |= pn532_packetbuffer[offset + 2];
		response <<= 8;
		response |= pn532_packetbuffer[offset + 3];

		return response;
	}

	public boolean SAMConfig() throws InterruptedException, IOException {
		byte[] command = new byte[2];
		command[0] = PN532_COMMAND_SAMCONFIGURATION;
		command[1] = 0x01; // normal mode;
		// command[2] = 0x14; // timeout 50ms * 20 = 1 second
		// command[3] = 0x01; // use IRQ pin!

		if (medium.writeCommand(command) != CommandStatus.OK) {
			return false;
		}

		return medium.readResponse(pn532_packetbuffer, 8) > 0;
	}

	/* (non-Javadoc)
	 * @see no.ntnu.item.its.osgi.sensors.pn532.impl.IPN532#writeMifareBlock(int, byte[])
	 */
	@Override
	public boolean writeMifareBlock(int blockNumber, byte[] content) throws InterruptedException, IOException {
		if (content.length != 16)
			return false;
		Arrays.fill(pn532_packetbuffer, (byte) 0xFF);
		byte[] command = new byte[4];
		command[0] = PN532_COMMAND_INDATAEXCHANGE;
		command[1] = 0x01;
		command[2] = (byte) (MIFARE_CMD_WRITE);
		command[3] = (byte) (blockNumber & 0xFF);

		if (medium.writeCommand(command, content) != CommandStatus.OK)
			return false;

		if (medium.readResponse(pn532_packetbuffer, 10) < 0) {
			return false;
		}

		if (pn532_packetbuffer[0] != 0) {
			return false;
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see no.ntnu.item.its.osgi.sensors.pn532.impl.IPN532#readMifareBlock(int, byte[])
	 */
	@Override
	public boolean readMifareBlock(int blockNumber, byte[] buffer) throws InterruptedException, IOException {
		Arrays.fill(pn532_packetbuffer, (byte) 0xFF);
		byte[] command = new byte[4];
		command[0] = PN532_COMMAND_INDATAEXCHANGE;
		command[1] = 0x01;
		command[2] = MIFARE_CMD_READ;
		command[3] = (byte) blockNumber;

		if (medium.writeCommand(command) != CommandStatus.OK) {
			return false;
		}

		if (medium.readResponse(pn532_packetbuffer, 26) < 0) {
			return false;
		}

		if (pn532_packetbuffer[0] != 0) {
			return false;
		}

		for (int i = 0; i < 16; i++) {
			buffer[i] = pn532_packetbuffer[i + 1];
		}

		return true;
	}

	/*
	 * Authenticate a single Mifare block
	 */
	/* (non-Javadoc)
	 * @see no.ntnu.item.its.osgi.sensors.pn532.impl.IPN532#authenticateMifareBlock(byte, no.ntnu.item.its.osgi.sensors.pn532.IPN532.MifareKeyType, byte[], byte[])
	 */
	@Override
	public boolean authenticateMifareBlock(byte block, MifareKeyType keyType, byte[] key, byte[] uid) throws InterruptedException, IOException {
		Arrays.fill(pn532_packetbuffer, (byte) 0xFF);
		byte[] command = new byte[14];
		command[0] = PN532_COMMAND_INDATAEXCHANGE;
		command[1] = 0x01; // index of card
		command[2] = (byte) ((keyType == MifareKeyType.A) ? MIFARE_CMD_AUTH_A : MIFARE_CMD_AUTH_B);
		command[3] = block;
		for (int i = 0; i < 6; i++) {
			command[i + 4] = key[i];
		}
		for (int i = 0; i < 4; i++) {
			command[i + 10] = uid[i];
		}

		if (medium.writeCommand(command) != CommandStatus.OK) {
			return false;
		}

		if (medium.readResponse(pn532_packetbuffer, 10) < 0) {
			return false;
		}

		if (pn532_packetbuffer[0] != 0) {
			return false;
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see no.ntnu.item.its.osgi.sensors.pn532.impl.IPN532#readPassiveTargetID(byte, byte[])
	 */
	@Override
	public int readPassiveTargetID(byte cardbaudrate, byte[] buffer) throws InterruptedException, IOException {
		byte[] command = new byte[3];
		command[0] = PN532_COMMAND_INLISTPASSIVETARGET;
		command[1] = 1; // max 1 cards at once (we can set this to 2 later)
		command[2] = (byte) cardbaudrate;

		if (medium.writeCommand(command) != CommandStatus.OK) {
			return -1; // command failed
		}

		if (medium.readResponse(pn532_packetbuffer, 20) < 0) {
			return -1; // negative response code
		}

		int offset = 0; // medium.getOffsetBytes();

		if (pn532_packetbuffer[offset + 0] != 1) {
			return -1; // response not ok
		}
		
		int uidLength = pn532_packetbuffer[offset + 5];

		for (int i = 0; i < uidLength; i++) {
			buffer[i] = pn532_packetbuffer[offset + 6 + i];
		}

		return uidLength;
	}

}
