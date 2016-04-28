package no.ntnu.item.its.osgi.sensors.color.impl;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import no.ntnu.item.its.osgi.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.common.interfaces.ColorControllerService;

/*
 * Light sensor
 */
public class TCS34725 implements ColorControllerService
{
	private I2CBus bus;
	private I2CDevice tcs34725;

	private int integrationTime = 0xFF;

	public TCS34725() throws SensorInitializationException, SensorCommunicationException
	{
		this(Constants.TCS34725_ADDRESS);
	}

	public TCS34725(int address) throws SensorInitializationException, SensorCommunicationException
	{
		this(address, 0xff);
	}

	public TCS34725(boolean b, int integrationTime) throws SensorInitializationException, SensorCommunicationException
	{
		this(Constants.TCS34725_ADDRESS, integrationTime);
	}

	public TCS34725(int address, int integrationTime) throws SensorInitializationException, SensorCommunicationException
	{
		try {
			this.integrationTime = integrationTime;
			bus = I2CFactory.getInstance(I2CBus.BUS_1);

			// Get device itself
			tcs34725 = bus.getDevice(address);

			initialize();
			waitfor(100L);
		} catch (IOException e) {
			throw new SensorInitializationException("Could not initialize TCS34725 sensor", e);
		}
	}

	@Override
	public int[] getRawData() throws SensorCommunicationException
	{
		int c = this.readU16(Constants.TCS34725_CDATAL) / 255;
		int r = this.readU16(Constants.TCS34725_RDATAL) / 255;
		int g = this.readU16(Constants.TCS34725_GDATAL) / 255;
		int b = this.readU16(Constants.TCS34725_BDATAL) / 255;
		waitfor((long)(Constants.INTEGRATION_TIME_DELAY.get(this.integrationTime) / 1000L));
		return new int[] {c, r, g, b};
	}

	private int initialize() throws SensorCommunicationException
	{
		int result = this.readU8(Constants.TCS34725_ID);
		if (result != 0x44)
			return -1;
		enable();
		return 0;
	}

	public void enable() throws SensorCommunicationException
	{
		this.write8(Constants.TCS34725_ENABLE, (byte)Constants.TCS34725_ENABLE_PON);
		waitfor(10L);
		this.write8(Constants.TCS34725_ENABLE, (byte)(Constants.TCS34725_ENABLE_PON | Constants.TCS34725_ENABLE_AEN));
	}

	public void disable() throws SensorCommunicationException
	{
		int reg = 0;
		reg = this.readU8(Constants.TCS34725_ENABLE);
		this.write8(Constants.TCS34725_ENABLE, (byte)(reg & ~(Constants.TCS34725_ENABLE_PON | Constants.TCS34725_ENABLE_AEN)));    
	}

	public void setIntegrationTime(int integrationTime) throws SensorCommunicationException
	{
		this.integrationTime = integrationTime;
		this.write8(Constants.TCS34725_ATIME, (byte)integrationTime);
	}

	public int getIntegrationTime() throws SensorCommunicationException
	{
		return this.readU8(Constants.TCS34725_ATIME);
	}

	public void setGain(int gain) throws SensorCommunicationException
	{
		this.write8(Constants.TCS34725_CONTROL, (byte)gain);
	}

	public int getGain() throws SensorCommunicationException
	{
		return this.readU8(Constants.TCS34725_CONTROL);
	}

	public void setInterrupt(boolean intrpt) throws  SensorCommunicationException
	{  
		int r = this.readU8(Constants.TCS34725_ENABLE);
		if (intrpt)
			r |= Constants.TCS34725_ENABLE_AIEN;
		else
			r &= ~Constants.TCS34725_ENABLE_AIEN;
		this.write8(Constants.TCS34725_ENABLE, (byte)r);
	}

	public void clearInterrupt() throws IOException
	{
		tcs34725.write((byte)(0x66 & 0xff));
	}

	public void setIntLimits(int low, int high) throws SensorCommunicationException
	{
		this.write8(0x04, (byte)(low & 0xFF));
		this.write8(0x05, (byte)(low >> 8));
		this.write8(0x06, (byte)(high & 0xFF));
		this.write8(0x07, (byte)(high >> 8));
	}

	private void write8(int register, int value) throws SensorCommunicationException
	{
		try {
			this.tcs34725.write(Constants.TCS34725_COMMAND_BIT | register, (byte)(value & 0xff));			
		} catch (IOException e) {
			throw new SensorCommunicationException(String.format("Could not write '%d' to register %d", value, register), e);
		}
	}

	private int readU16(int register) throws SensorCommunicationException
	{
		int lo = this.readU8(register);
		int hi = this.readU8(register + 1);
		int result = (Constants.TCS34725_ENDIANNESS == Constants.BIG_ENDIAN) ? (hi << 8) + lo : (lo << 8) + hi; // Big Endianv
		return result;

	}

	private int readU8(int reg) throws SensorCommunicationException
	{
		try {
			int result = 0;
			result = this.tcs34725.read(Constants.TCS34725_COMMAND_BIT | reg);
			return result;			
		} catch (IOException e) {
			throw new SensorCommunicationException(String.format("Could not read from register %d", reg), e);
		}
	}

	private static void waitfor(long howMuch)
	{
		try { Thread.sleep(howMuch); } catch (InterruptedException ie) { ie.printStackTrace(); }
	}
}