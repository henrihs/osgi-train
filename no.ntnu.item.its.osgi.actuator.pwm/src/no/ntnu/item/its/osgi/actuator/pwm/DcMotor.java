package no.ntnu.item.its.osgi.actuator.pwm;

import java.io.IOException;

import no.ntnu.item.its.osgi.common.enums.MotorCommand;

public class DcMotor {
	
	private static int[][] pins = {{8,9,10},{13,12,11},{2,3,4},{7,6,5}};
	
	private int pwmPin, in1, in2;
	private ActuatorControllerImpl controller;
	
	public DcMotor(ActuatorControllerImpl c, int port) {
		if (port < 1 || port > 4)
			throw new IllegalArgumentException("Port must be int between 0 and 3 inclusive");
		pwmPin = pins[port-1][0];
		in2 = pins[port-1][1];
		in1 = pins[port-1][2];
		controller = c;
	}
	
	public void setDirection(MotorCommand command) throws IOException {
		switch (command) {
		case FORWARD:
			controller.setPin(in2, false);
			controller.setPin(in1, true);
			break;
			
		case BACKWARD:
			controller.setPin(in1, false);
			controller.setPin(in2, true);
			break;
			
		case STOP:
			controller.setPin(in1, false);
			controller.setPin(in2, false);
			break;
		}
	}
	
	public void setSpeed(int speed) throws IOException {
		if (speed < 0) speed = 0;
		else if (speed > 255) speed = 255;
		
		controller.setPwm(pwmPin, 0, speed*16);
	}
}
