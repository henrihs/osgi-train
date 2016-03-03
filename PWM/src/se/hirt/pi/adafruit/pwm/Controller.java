package se.hirt.pi.adafruit.pwm;

import java.io.IOException;

public class Controller {

	private PWMDevice pwm;
	private DcMotor[] motors;

	public Controller(int bus, int address) throws IOException {
		pwm = new PWMDevice(bus, address);
		pwm.setPWMFreqency(1600);
		motors = new DcMotor[] { 
				new DcMotor(this, 1),
				new DcMotor(this, 2),
				new DcMotor(this, 3),
				new DcMotor(this, 4)
				};
		
	}

	public void setPin(int inPin, boolean state) throws IOException {
		if (inPin < 0 || inPin > 15)
			throw new IllegalArgumentException("PWM Pin must be 0 <= pin <= 15");

		if (!state)
			setPwm(inPin, 0, 4096);
		else
			setPwm(inPin, 4096, 0);
	}

	public void setPwm(int channel, int on, int off) throws IOException {
		pwm.getChannel(channel).setPWM(on, off);
	}

	public DcMotor getMotor(int port) {
		if (port < 1 || port > 4)
			throw new IllegalArgumentException("Port number must be 0 < port <= 4");
		return motors[port-1];
	}
	
	public static void main(String[] args) {
		try {
			Controller c = new Controller(1, 0x60);
			DcMotor m = c.getMotor(1);
			m.setDirection(MotorCommand.FORWARD);
			for (int i = 0; i < 256; i++) {
				m.setSpeed(i);
				Thread.sleep(10);
			}
			m.setDirection(MotorCommand.RELEASE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
