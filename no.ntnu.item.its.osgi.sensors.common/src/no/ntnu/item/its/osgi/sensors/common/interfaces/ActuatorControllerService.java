package no.ntnu.item.its.osgi.sensors.common.interfaces;

import no.ntnu.item.its.osgi.sensors.common.enums.MotorCommand;

public interface ActuatorControllerService {
	public static final String EVENT_TOPIC = "no/ntnu/item/its/osgi/actuator";
	public static final String COMMAND_ISSUED_KEY = "command.processed";
	public static final String TIMESTAMP_KEY = "data.timestamp";
	
	public void send(MotorCommand command);
}
