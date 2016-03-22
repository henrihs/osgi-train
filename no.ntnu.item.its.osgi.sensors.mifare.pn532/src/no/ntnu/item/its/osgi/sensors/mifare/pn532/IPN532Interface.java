package no.ntnu.item.its.osgi.sensors.mifare.pn532;

import java.io.IOException;

public interface IPN532Interface {
  
  static final byte PN532_PREAMBLE = 0x00;
  static final byte PN532_STARTCODE1 = 0x00;
  static final byte PN532_STARTCODE2 = (byte) 0xFF;
  static final byte PN532_POSTAMBLE = 0x00;

  static final byte PN532_HOSTTOPN532 = (byte) 0xD4;
  static final byte PN532_PN532TOHOST = (byte) 0xD5;

  public void begin() throws IOException;

	public abstract void wakeup();

	public abstract CommandStatus writeCommand(byte[] header, byte[] body)
			throws InterruptedException, IOException;

	public abstract CommandStatus writeCommand(byte header[]) throws InterruptedException, IOException;

	public abstract int readResponse(byte[] buffer, int expectedLength,
			int timeout) throws InterruptedException;

	public abstract int readResponse(byte[] buffer, int expectedLength)
			throws InterruptedException;
	

}