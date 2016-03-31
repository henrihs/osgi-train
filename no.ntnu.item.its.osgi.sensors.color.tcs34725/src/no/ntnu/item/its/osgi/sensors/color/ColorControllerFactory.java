package no.ntnu.item.its.osgi.sensors.color;

import no.ntnu.item.its.osgi.sensors.color.impl.Constants;
import no.ntnu.item.its.osgi.sensors.color.impl.TCS34725;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.ColorController;

public class ColorControllerFactory {

	public static ColorController getInstance() throws SensorInitializationException, SensorCommunicationException {
		TCS34725 controller =  new TCS34725(Constants.TCS34725_ADDRESS, Constants.TCS34725_INTEGRATIONTIME_2_4MS);
		return controller;
	}
}
