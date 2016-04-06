package test;

import java.io.IOException;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class XtrinsicSenseBoard {
	
	static I2CBus bus;
    static I2CDevice tmp;
    static I2CDevice acc;
    static I2CDevice mag;
    
    static GpioController gpio = GpioFactory.getInstance();
	private static int calZ;
	private static int calY;
	private static int calX;
    
	
	public static void main(String[] args) {
		
		GpioPinDigitalOutput enableACC;
		
		enableACC = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00,"EnableMAG", PinState.HIGH);
		enableACC.setShutdownOptions(true, PinState.LOW,PinPullResistance.OFF);
		
		//Get BUS
		try {
			bus = I2CFactory.getInstance(I2CBus.BUS_1);	
		} catch (Throwable t) {
			System.out.println ("Unable to open device " + 0x60 + " on bus 1: ");
		}		
		
		//Get Sensors
		try {
			tmp = bus.getDevice(0x60);
			mag = bus.getDevice(0x0e);
		} catch (Throwable t) {
			System.out.println ("Unable to open device " + 0x60 + " on bus 1: ");
		}
		
		//TEMPERATURE/ALTITUDE
		//Set to Active
		byte[] writeT = new byte[1];
		writeT[0] = -127;  //in bits 10000001
		try {
			tmp.write(0x26,writeT,0,writeT.length);	
		} catch (IOException e) {
			System.out.println("WRITEFAILED Writing to I2C-bus generated error: ");
		}
		
		//Calibrate
		int seaPress1 = 101000/2;
		short sP2 = (short) seaPress1;
		byte[] cal = new byte[2];		
		cal[0] = (byte) ((sP2 >> 8) & 0xff);
		cal[1] = (byte) (sP2 & 0xff);		
		try {
			tmp.write(0x14,cal,0,cal.length);	
		} catch (IOException e) {
			System.out.println("WRITEFAILED Writing to I2C-bus generated error: ");
		}
		
		//MAGNETOMETER
		byte[] writeM = new byte[2];
		writeM[0] = 1;  //in bits 00000001
		writeM[1] = -128; //in bits 1000000
		try {
			mag.write(0x10,writeM,0,writeM.length);	
		} catch (IOException e) {
			System.out.println("WRITEFAILED Writing to I2C-bus generated error: ");
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		calibrateAccel(enableACC);
		
		while(true) {
			
			enableACC.setState(true);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Get readings TEMP/ALT
//			getTempAndAlt();		

			
			//Get readings ACCEL
			getAccel(enableACC);
			
			
			//Get readings MAG
//			getMag();
		}
		
	}


		private static void getTempAndAlt() {
		int r = 0;
		int regsT = 6; 						//we can only read 6 regs. (0x00 - 0x05);
		byte[] readT = new byte[regsT]; 
		try {
			r = tmp.read(0x00,readT,0,regsT);
		} catch (Throwable t) {
			if (bus != null) {	// Have we stopped?		
				System.out.println("READFAILED Reading from I2C generated error: ");
			}
		}
		if (r != regsT) {
			System.out.println("Reading from I2C generated error: Only got " + r + " bytes out of " + regsT + "requested");
		} else {
			// Convert Temperature
			double t = (double) 0.0;
			int temperature = readT[0x04] << 8;		// signed 12-bit integer
			temperature |= readT[0x05] & 0xff;
			temperature >>= 4;	
			t =  temperature/16.0;
			System.out.println("Temperature: " + t + " Celsius");
			
			// Convert Altitude
			double a = 0.0;			
			int a1 =  (readT[0x01] << 24);							//Signed byte!!
			int a2 =  (readT[0x02] << 16) & 0xff0000;				//Unsigned byte
			int a3 = (readT[0x03] << 8) & 0xff00;					//Unsigned byte			
			int altitude = a1 | a2 | a3;			
			a = altitude / 65536.0;
			System.out.println("Altitude: " + a + " meters");				
		}
	}


	private static void getMag() {
		int r;
		r=0;
		int regsM = 7; 						
		byte[] readM = new byte[regsM];

		try {
			r = mag.read(0x00,readM,0,regsM);
		} catch (Throwable t) {
			if (bus != null) {	// Have we stopped?		
				System.out.println("READFAILED Reading from I2C generated error: ");
			}
		}
		if (r != regsM) {
			System.out.println("Reading from I2C generated error: Only got " + r + " bytes out of " + regsM + "requested");
		} else {
			 
			short magnet = 0;
			    
			magnet = (short) ((readM[1] << 8) | (readM[2]&0xff)); //16-bit signed x value
			double xM = magnet/10.0;
			 
			magnet = (short) ((readM[3] << 8) | (readM[4]&0xff)); //16-bit signed y value     
			double yM = magnet/10.0;
			 
			magnet = (short) ((readM[5] << 8) | (readM[6]&0xff)); //16-bit signed z value     
			double zM = magnet/10.0;
			
			System.out.println("Magnetic field: x= " + xM + " microTesla, y= " + yM + " microTesla, z= " + zM + " microTesla.");
		}
	}

	private static void calibrateAccel(GpioPinDigitalOutput enableACC) {
		enableACC.setState(true);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int r;
		try {
			acc = bus.getDevice(0x55);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		r = 0;
		int regsA = 7; 						
		byte[] readA = new byte[regsA];
		long start = System.nanoTime();
		try {
			r = acc.read(0x00,readA,0,regsA);
		} catch (Throwable t) {
			if (bus != null) {	// Have we stopped?		
				System.out.println("READFAILED Reading from I2C generated error: ");
			}
		}
		if (r != regsA) {
			System.out.println("Reading from I2C generated error: Only got " + r + " bytes out of " + regsA + "requested");
		} else {
				
			calX = (readA[1] << 6) | ((readA[2]&0xff) >> 2); //14-bit signed x value		        
//				double xA = accel/1024.0;
		 
			calY = (readA[3] << 6) | ((readA[4]&0xff) >> 2); //14-bit signed y value
//				double yA = accel/1024.0;
		 
			calZ = (readA[5] << 6) | ((readA[6]&0xff) >> 2); //14-bit signed z value
//				double zA = accel/1024.0;	
		
			enableACC.setState(false);
			System.out.println("Calibrated accelerometer with values: X:" + calX + " Y:" + calY + " Z:" + calZ);
//				System.out.println("Acceleration: x= " + xA + " g, y= " + yA + " g, z= " + zA + " g." + " , readDuration was " + duration);
		}
		
	}

	private static void getAccel(GpioPinDigitalOutput enableACC) {
		int r;
		try {
			acc = bus.getDevice(0x55);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		r = 0;
		int regsA = 7; 						
		byte[] readA = new byte[regsA];
		long start = System.nanoTime();
		try {
			r = acc.read(0x00,readA,0,regsA);
		} catch (Throwable t) {
			if (bus != null) {	// Have we stopped?		
				System.out.println("READFAILED Reading from I2C generated error: ");
			}
		}
		if (r != regsA) {
			System.out.println("Reading from I2C generated error: Only got " + r + " bytes out of " + regsA + "requested");
		} else {
		
			int accel = 0;
		
			int accelX = (readA[1] << 6) | ((readA[2]&0xff) >> 2); //14-bit signed x value		        
//				double xA = accel/1024.0;
		 
			int accelY = (readA[3] << 6) | ((readA[4]&0xff) >> 2); //14-bit signed y value
//				double yA = accel/1024.0;
		 
			int accelZ = (readA[5] << 6) | ((readA[6]&0xff) >> 2); //14-bit signed z value
//				double zA = accel/1024.0;	
		
			long duration = System.nanoTime() - start;
			enableACC.setState(false);
			System.out.println("readDuration was " + duration);
			System.out.println("X:" + (accelX-calX) + " Y:" + (accelY-calY) + " Z:" + (accelZ-calZ+1024));
//				System.out.println("Acceleration: x= " + xA + " g, y= " + yA + " g, z= " + zA + " g." + " , readDuration was " + duration);
		}
	}

}
