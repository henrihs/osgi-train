package no.ntnu.item.its.osgi.sensors.mag.mag3110;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import no.ntnu.item.its.osgi.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.common.interfaces.MagControllerService;

public class MagController implements MagControllerService {

	private I2CDevice mag;
	private I2CBus bus;

	public MagController() throws SensorInitializationException, SensorCommunicationException {
		try {
			bus = I2CFactory.getInstance(I2CBus.BUS_1);
			mag = bus.getDevice(0x0e);
		} catch (IOException e) {
			throw new SensorInitializationException("Could not initialize magnetometer; ", e);
		}

		byte[] writeM = new byte[2];
		writeM[0] = 1;  //in bits 00000001
		writeM[1] = -128; //in bits 1000000
		try {
			mag.write(0x10,writeM,0,writeM.length);	
		} catch (IOException e) {
			throw new SensorInitializationException("Could not read from magnetometer; ", e);
		}
	}

	@Override
	public int[] getRawData() throws SensorCommunicationException {
		int r;
		r=0;
		int regsM = 7; 						
		byte[] readM = new byte[regsM];

		try {
			r = mag.read(0x00,readM,0,regsM);
		} catch (Throwable t) {
			throw new SensorCommunicationException("Could not read from magnetometer; ", t);
		}
		
		if (r != regsM) {
			throw new SensorCommunicationException(
					"Reading from I2C generated error: Only got " + 
							r + " bytes out of " + regsM + "requested");
		}

		short magnet = 0;

		magnet = (short) ((readM[1] << 8) | (readM[2]&0xff)); //16-bit signed x value
		int xM = magnet;

		magnet = (short) ((readM[3] << 8) | (readM[4]&0xff)); //16-bit signed y value     
		int yM = magnet;

		magnet = (short) ((readM[5] << 8) | (readM[6]&0xff)); //16-bit signed z value     
		int zM = magnet;

		return new int[] {xM, yM, zM};
	}

}
