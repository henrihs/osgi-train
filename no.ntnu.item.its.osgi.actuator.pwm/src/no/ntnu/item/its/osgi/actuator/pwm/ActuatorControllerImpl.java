package no.ntnu.item.its.osgi.actuator.pwm;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;

import no.ntnu.item.its.osgi.common.enums.MotorCommand;
import no.ntnu.item.its.osgi.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.common.interfaces.ActuatorControllerService;

public class ActuatorControllerImpl implements ActuatorControllerService {

	private static final int SPEED_STEP_SLEEP_TIME = 2;
	private PWMDevice pwm;
	private DcMotor motor;
	private MotorCommand previousState = MotorCommand.STOP;
	private int currentSpeed = 0;

	public ActuatorControllerImpl() throws SensorInitializationException {
		this (1, 0x60);

	}

	public ActuatorControllerImpl(int bus, int address) throws SensorInitializationException {
		try {
			pwm = new PWMDevice(bus, address);
			pwm.setPWMFreqency(1600);
		} catch (IOException e) {
			throw new SensorInitializationException("Could not initialize PWM actuator", e);
		}

		motor = new DcMotor(this, 1);		
	}

	@Override
	public void send(MotorCommand command) {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					synchronized (motor) {						
						publish(command);
						if (command == MotorCommand.STOP) {
//							for (int i = 149; i >= 0; i--) {
								motor.setSpeed(0);
//								Thread.sleep(SPEED_STEP_SLEEP_TIME);
//							}
						} else {
							motor.setDirection(command);
//							for (int i = 0; i < 150; i++) {
								motor.setSpeed(110);
//								Thread.sleep(SPEED_STEP_SLEEP_TIME);
//							}
						}
					}

				} catch (IOException e) {
					if (!PwmActivator.logServiceTracker.isEmpty()) {
						((LogService)PwmActivator.logServiceTracker.getService()).log(
								LogService.LOG_ERROR,
								"Could not issue command to motors!",
								e);
					}
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
				}				
			}
		};

		new Thread(r).start();
	}

	@Override
	public void send(MotorCommand command, int speed) {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					synchronized(motor) {
						if (command == MotorCommand.STOP) {
							for (int i = currentSpeed; i >= 0; i--) {
								motor.setSpeed(i);
								Thread.sleep(SPEED_STEP_SLEEP_TIME);
							}
						} else {
							motor.setDirection(command);
							if (speed < currentSpeed) {
								for (int i = currentSpeed; i >= speed; i--) {
									motor.setSpeed(i);
									Thread.sleep(SPEED_STEP_SLEEP_TIME);
								}
							}
							else if (speed > currentSpeed) {
								for (int i = currentSpeed; i <= speed; i++) {
									motor.setSpeed(i);
									Thread.sleep(SPEED_STEP_SLEEP_TIME);
								}
							}

						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		};

		new Thread(r).start();
	}

	private void publish(MotorCommand command) {
		if (!PwmActivator.eventAdminTracker.isEmpty()) { 
			((EventAdmin)PwmActivator.eventAdminTracker.getService()).postEvent(createEvent(command));
		}
	}

	private Event createEvent(MotorCommand command) {
		Map<String, Object> properties = new Hashtable<>();
		properties.put(
				ActuatorControllerService.TIMESTAMP_KEY, 
				System.nanoTime());
		properties.put(
				ActuatorControllerService.PREV_STATE_KEY, 
				previousState);
		properties.put(
				ActuatorControllerService.NEXT_STATE_KEY, 
				command);
		previousState = command;
		return new Event(ActuatorControllerService.EVENT_TOPIC, properties);
	}


	void setPin(int inPin, boolean state) throws IOException {
		if (inPin < 0 || inPin > 15)
			throw new IllegalArgumentException("PWM Pin must be 0 <= pin <= 15");

		if (!state)
			setPwm(inPin, 0, 4096);
		else
			setPwm(inPin, 4096, 0);
	}

	void setPwm(int channel, int on, int off) throws IOException {
		pwm.getChannel(channel).setPWM(on, off);
	}

	public static void main(String[] args) {
		try {
			ActuatorControllerImpl c = new ActuatorControllerImpl(1, 0x60);
			DcMotor m = c.motor;
			m.setDirection(MotorCommand.FORWARD);
			for (int i = 0; i < 256; i++) {
				m.setSpeed(i);
				Thread.sleep(10);
			}
			m.setDirection(MotorCommand.STOP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SensorInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
