package no.ntnu.item.its.osgi.common.interfaces;

import no.ntnu.item.its.osgi.common.enums.MotorCommand;

public interface ActuatorControllerService {
	public static final String EVENT_TOPIC = "no/ntnu/item/its/osgi/actuator";
	public static final String PREV_STATE_KEY = "state.previous";
	public static final String NEXT_STATE_KEY = "state.next";
	public static final String TIMESTAMP_KEY = "data.timestamp";
	
	public void send(MotorCommand command);

	public void send(MotorCommand command, int speed);
}
