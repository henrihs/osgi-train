package no.ntnu.item.its.osgi.sensors.accel.mma8491q;

import java.io.IOException;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;

public class AccelerationControllerImpl implements AccelerationControllerService {

	private static Pin ENABLE_PIN_NO = RaspiPin.GPIO_00;
	private static GpioController gpio = GpioFactory.getInstance();

	private GpioPinDigitalOutput enablePin;
	private I2CBus bus;
	private int[] calibrationData;

	public AccelerationControllerImpl() throws SensorInitializationException, SensorCommunicationException {

		enablePin = gpio.provisionDigitalOutputPin(
				ENABLE_PIN_NO,"EnableMMA", 
				PinState.HIGH);
		enablePin.setShutdownOptions(true, PinState.LOW,PinPullResistance.OFF);

		try {
			bus = I2CFactory.getInstance(I2CBus.BUS_1);
		} catch (Exception e) {
			throw new SensorInitializationException("Could not initialize I2C bus", e);
		}
		
		calibrate();
	}

	@Override
	public int[] getRawData() throws SensorCommunicationException, SensorInitializationException {
		int[] set = read();
		return new int[] {
				set[0] - calibrationData[0], 
				set[1] - calibrationData[1], 
				set[2] - calibrationData[2]
				};
	}
	
	@Override
	public int[] getCalibratedData() throws SensorCommunicationException, SensorInitializationException {
		int[] set = read();
		return new int[] {
				set[0] - calibrationData[0], 
				set[1] - calibrationData[1], 
				set[2] - calibrationData[2]
				};
	} 

	private void calibrate() throws SensorCommunicationException, SensorInitializationException {
		int[][] calibrationRawData = new int[100][];
		int sumX = 0;
		int sumY = 0;
		int sumZ = 0;
		for (int[] set : calibrationRawData) {
			set = read();
			sumX += set[0];
			sumY += set[1];
			sumZ += set[2];
		}
		
		calibrationData = new int[] {sumX/100, sumY/100, sumZ/100};
	}

	private int[] read() throws SensorCommunicationException, SensorInitializationException {
		I2CDevice acc = null;
		int x, y, z;
		enablePin.setState(true);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int r;
		try {
			acc = bus.getDevice(0x55);
		} catch (IOException e1) {
			throw new SensorCommunicationException("Could not get device on address 0x55", e1);
		}

		r = 0;
		int regsA = 7; 						
		byte[] readA = new byte[regsA];
		try {
			r = acc.read(0x00,readA,0,regsA);
		} catch (Throwable t) {
			if (bus != null) {
				throw new SensorCommunicationException("Could not read from I2C bus", t);
			}
			else {
				throw new SensorCommunicationException("I2C bus is not initialized", t);
			}
		}
		if (r != regsA) {
			throw new SensorCommunicationException(
					"Number of bytes read != 7, are all axis data ready?");
		} else {
			x = (readA[1] << 6) | ((readA[2]&0xff) >> 2); //14-bit signed x value
			y = (readA[3] << 6) | ((readA[4]&0xff) >> 2); //14-bit signed y value
			z = (readA[5] << 6) | ((readA[6]&0xff) >> 2); //14-bit signed z value
		}
		
		enablePin.setState(false);
		return new int[] {x, y, z};
	}
}
