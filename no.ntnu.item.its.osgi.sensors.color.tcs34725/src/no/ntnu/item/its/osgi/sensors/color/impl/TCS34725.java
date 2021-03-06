package no.ntnu.item.its.osgi.sensors.color.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import no.ntnu.item.its.osgi.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.common.interfaces.ColorControllerService;

/*
 * Light sensor
 */
public class TCS34725 implements ColorControllerService
{
	public final static int LITTLE_ENDIAN = 0;
	public final static int BIG_ENDIAN    = 1;
	private final static int TCS34725_ENDIANNESS = BIG_ENDIAN;

	public final static int TCS34725_ADDRESS = 0x29; 

	//public final static int TCS34725_ID               = 0x12; // 0x44 = TCS34721/TCS34725, 0x4D = TCS34723/TCS34727

	public final static int TCS34725_COMMAND_BIT      = 0x80;

	public final static int TCS34725_ENABLE           = 0x00;
	public final static int TCS34725_ENABLE_AIEN      = 0x10; // RGBC Interrupt Enable
	public final static int TCS34725_ENABLE_WEN       = 0x08; // Wait enable - Writing 1 activates the wait timer
	public final static int TCS34725_ENABLE_AEN       = 0x02; // RGBC Enable - Writing 1 actives the ADC, 0 disables it
	public final static int TCS34725_ENABLE_PON       = 0x01; // Power on - Writing 1 activates the internal oscillator, 0 disables it
	public final static int TCS34725_ATIME            = 0x01; // Integration time
	public final static int TCS34725_WTIME            = 0x03; // Wait time (if TCS34725_ENABLE_WEN is asserted)
	public final static int TCS34725_WTIME_2_4MS      = 0xFF; // WLONG0 = 2.4ms   WLONG1 = 0.029s
	public final static int TCS34725_WTIME_204MS      = 0xAB; // WLONG0 = 204ms   WLONG1 = 2.45s
	public final static int TCS34725_WTIME_614MS      = 0x00; // WLONG0 = 614ms   WLONG1 = 7.4s
	public final static int TCS34725_AILTL            = 0x04; // Clear channel lower interrupt threshold
	public final static int TCS34725_AILTH            = 0x05;
	public final static int TCS34725_AIHTL            = 0x06; // Clear channel upper interrupt threshold
	public final static int TCS34725_AIHTH            = 0x07;
	public final static int TCS34725_PERS             = 0x0C; // Persistence register - basic SW filtering mechanism for interrupts
	public final static int TCS34725_PERS_NONE        = 0b0000; // Every RGBC cycle generates an interrupt
	public final static int TCS34725_PERS_1_CYCLE     = 0b0001; // 1 clean channel value outside threshold range generates an interrupt
	public final static int TCS34725_PERS_2_CYCLE     = 0b0010; // 2 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_3_CYCLE     = 0b0011; // 3 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_5_CYCLE     = 0b0100; // 5 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_10_CYCLE    = 0b0101; // 10 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_15_CYCLE    = 0b0110; // 15 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_20_CYCLE    = 0b0111; // 20 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_25_CYCLE    = 0b1000; // 25 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_30_CYCLE    = 0b1001; // 30 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_35_CYCLE    = 0b1010; // 35 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_40_CYCLE    = 0b1011; // 40 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_45_CYCLE    = 0b1100; // 45 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_50_CYCLE    = 0b1101; // 50 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_55_CYCLE    = 0b1110; // 55 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_PERS_60_CYCLE    = 0b1111; // 60 clean channel values outside threshold range generates an interrupt
	public final static int TCS34725_CONFIG           = 0x0D;
	public final static int TCS34725_CONFIG_WLONG     = 0x02; // Choose between short and long (12x) wait times via TCS34725_WTIME
	public final static int TCS34725_CONTROL          = 0x0F; // Set the gain level for the sensor
	public final static int TCS34725_ID               = 0x12; // 0x44 = TCS34721/TCS34725, 0x4D = TCS34723/TCS34727
	public final static int TCS34725_STATUS           = 0x13;
	public final static int TCS34725_STATUS_AINT      = 0x10; // RGBC Clean channel interrupt
	public final static int TCS34725_STATUS_AVALID    = 0x01; // Indicates that the RGBC channels have completed an integration cycle

	public final static int TCS34725_CDATAL           = 0x14; // Clear channel data
	public final static int TCS34725_CDATAH           = 0x15;
	public final static int TCS34725_RDATAL           = 0x16; // Red channel data
	public final static int TCS34725_RDATAH           = 0x17;
	public final static int TCS34725_GDATAL           = 0x18; // Green channel data
	public final static int TCS34725_GDATAH           = 0x19;
	public final static int TCS34725_BDATAL           = 0x1A; // Blue channel data
	public final static int TCS34725_BDATAH           = 0x1B;

	public final static int TCS34725_INTEGRATIONTIME_2_4MS  = 0xFF;   //  2.4ms - 1 cycle    - Max Count: 1024
	public final static int TCS34725_INTEGRATIONTIME_24MS   = 0xF6;   // 24ms  - 10 cycles  - Max Count: 10240
	public final static int TCS34725_INTEGRATIONTIME_50MS   = 0xEB;   //  50ms  - 20 cycles  - Max Count: 20480
	public final static int TCS34725_INTEGRATIONTIME_101MS  = 0xD5;   //  101ms - 42 cycles  - Max Count: 43008
	public final static int TCS34725_INTEGRATIONTIME_154MS  = 0xC0;   //  154ms - 64 cycles  - Max Count: 65535
	public final static int TCS34725_INTEGRATIONTIME_700MS  = 0x00;   //  700ms - 256 cycles - Max Count: 65535

	public final static int TCS34725_GAIN_1X                = 0x00;   //  No gain
	public final static int TCS34725_GAIN_4X                = 0x01;   //  4x gain
	public final static int TCS34725_GAIN_16X               = 0x02;   //  16x gain
	public final static int TCS34725_GAIN_60X               = 0x03;   //  60x gain

	public final static Map<Integer, Long> INTEGRATION_TIME_DELAY = new HashMap<Integer, Long>();
	static
	{ //                                Microseconds                              
		INTEGRATION_TIME_DELAY.put(TCS34725_INTEGRATIONTIME_2_4MS,  2400L);   // 2.4ms - 1 cycle    - Max Count: 1024
		INTEGRATION_TIME_DELAY.put(TCS34725_INTEGRATIONTIME_24MS,  24000L);   // 24ms  - 10 cycles  - Max Count: 10240
		INTEGRATION_TIME_DELAY.put(TCS34725_INTEGRATIONTIME_50MS,  50000L);   // 50ms  - 20 cycles  - Max Count: 20480
		INTEGRATION_TIME_DELAY.put(TCS34725_INTEGRATIONTIME_101MS, 101000L);   // 101ms - 42 cycles  - Max Count: 43008
		INTEGRATION_TIME_DELAY.put(TCS34725_INTEGRATIONTIME_154MS, 154000L);   // 154ms - 64 cycles  - Max Count: 65535
		INTEGRATION_TIME_DELAY.put(TCS34725_INTEGRATIONTIME_700MS, 700000L);   // 700ms - 256 cycles - Max Count: 65535
	}

	private I2CBus bus;
	private I2CDevice tcs34725;

	private int integrationTime = 0xFF;

	public TCS34725() throws IOException
	{
		this(TCS34725_ADDRESS);
	}

	public TCS34725(int address) throws IOException
	{
		this(address, 0xff);
	}

	public TCS34725(boolean b, int integrationTime) throws IOException
	{
		this(TCS34725_ADDRESS, integrationTime);
	}

	public TCS34725(int address, int integrationTime) throws IOException
	{
		this.integrationTime = integrationTime;
		bus = I2CFactory.getInstance(I2CBus.BUS_1);

		// Get device itself
		tcs34725 = bus.getDevice(address);

		initialize();
	}

	private int initialize() throws IOException
	{
		int result = this.readU8(TCS34725_ID);
		if (result != 0x44)
			return -1;
		enable();
		return 0;
	}

	public void enable() throws IOException
	{
		this.write8(TCS34725_ENABLE, (byte)TCS34725_ENABLE_PON);
		waitfor(10L);
		this.write8(TCS34725_ENABLE, (byte)(TCS34725_ENABLE_PON | TCS34725_ENABLE_AEN));
	}

	public void disable() throws IOException
	{
		int reg = 0;
		reg = this.readU8(TCS34725_ENABLE);
		this.write8(TCS34725_ENABLE, (byte)(reg & ~(TCS34725_ENABLE_PON | TCS34725_ENABLE_AEN)));    
	}

	public void setIntegrationTime(int integrationTime) throws IOException
	{
		this.integrationTime = integrationTime;
		this.write8(TCS34725_ATIME, (byte)integrationTime);
	}

	public int getIntegrationTime() throws IOException 
	{
		return this.readU8(TCS34725_ATIME);
	}

	public void setGain(int gain) throws IOException
	{
		this.write8(TCS34725_CONTROL, (byte)gain);
	}

	public int getGain() throws IOException
	{
		return this.readU8(TCS34725_CONTROL);
	}

	@Override
	public int[] getRawData() throws SensorCommunicationException
	{
		
		int r;
		int b;
		int g;
		int c;
		try {
			r = this.readU16(TCS34725_RDATAL);
			b = this.readU16(TCS34725_BDATAL);
			g = this.readU16(TCS34725_GDATAL);
			c = this.readU16(TCS34725_CDATAL);
		} catch (IOException e) {
			throw new SensorCommunicationException("Communication failure", e);
		}
		waitfor((long)(INTEGRATION_TIME_DELAY.get(this.integrationTime) / 1000L));
		return new int[] {c, r, g, b};
	}

	public void setInterrupt(boolean intrpt) throws IOException
	{  
		int r = this.readU8(TCS34725_ENABLE);
		if (intrpt)
			r |= TCS34725_ENABLE_AIEN;
		else
			r &= ~TCS34725_ENABLE_AIEN;
		this.write8(TCS34725_ENABLE, (byte)r);
	}

	public void clearInterrupt() throws IOException
	{
		tcs34725.write((byte)(0x66 & 0xff));
	}

	public void setIntLimits(int low, int high) throws IOException
	{
		this.write8(0x04, (byte)(low & 0xFF));
		this.write8(0x05, (byte)(low >> 8));
		this.write8(0x06, (byte)(high & 0xFF));
		this.write8(0x07, (byte)(high >> 8));
	}

	private void write8(int register, int value) throws IOException
	{
		this.tcs34725.write(TCS34725_COMMAND_BIT | register, (byte)(value & 0xff));
	}

	private int readU16(int register) throws IOException
	{
		int lo = this.readU8(register);
		int hi = this.readU8(register + 1);
		int result = (TCS34725_ENDIANNESS == BIG_ENDIAN) ? (hi << 8) + lo : (lo << 8) + hi; // Big Endianv
		return result;
	}

	private int readU8(int reg) throws IOException
	{
		// "Read an unsigned byte from the I2C device"
		int result = 0;
		result = this.tcs34725.read(TCS34725_COMMAND_BIT | reg);
		return result;
	}

	private static String toHex(int i)
	{
		String s = Integer.toString(i, 16).toUpperCase();
		while (s.length() % 2 != 0)
			s = "0" + s;
		return "0x" + s;
	}

	private static void waitfor(long howMuch)
	{
		try { Thread.sleep(howMuch); } catch (InterruptedException ie) { ie.printStackTrace(); }
	}
}