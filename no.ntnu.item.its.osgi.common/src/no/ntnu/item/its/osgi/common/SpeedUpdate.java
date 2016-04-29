package no.ntnu.item.its.osgi.common;

import no.ntnu.item.its.osgi.common.enums.MotorCommand;

public class SpeedUpdate {
	public final MotorCommand command;
	public final int speedLevel;
	
	public SpeedUpdate(MotorCommand command, int speedLevel) {
		this.command = command;
		this.speedLevel = speedLevel;
	}
}
