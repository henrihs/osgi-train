package no.ntnu.item.its.osgi.sensors.accel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorCommunicationException;
import no.ntnu.item.its.osgi.sensors.common.exceptions.SensorInitializationException;
import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;
import no.ntnu.item.osgi.sensors.simulated.StatisticalGenerator;

public class AccelerationControllerMocker implements AccelerationControllerService {
	
	private final ArrayList<Double> decimals; 
	private Iterator<Double> decimalIterator;

	public AccelerationControllerMocker() {
		decimals = new ArrayList<>();
		for (double i = 0.0; i < 1.0; i+=0.01) {
			decimals.add(i);
		}
		
		for (double i = 1.0; i > 0.0; i-=0.01) {
			decimals.add(i);
		}
		
		for (int i = 0; i < 100; i++) {
			decimals.add(0.0);
		}
		
		for (double i = 0.0; i > -1.0; i-=0.01) {
			decimals.add(i);
		}
		
		for (double i = -1.0; i < 0.0; i+=0.01) {
			decimals.add(i);
		}

		for (int i = 0; i < 100; i++) {
			decimals.add(0.0);
		}
	}
	

	@Override
	public int[] getRawData() throws SensorCommunicationException, SensorInitializationException {
//		return new int[] {statistic.newPolarInt(), statistic.newPolarInt(), statistic.newPolarInt()};
		int i = getInt();
		return new int[] {i,i,i};
	}

	@Override
	public int[] getCalibratedData() throws SensorCommunicationException, SensorInitializationException {
		return getRawData();
	}
		
	public int getInt() {
		Double d = getIterator().next()*1024/9.81;
		return d.intValue();
	}

	private Iterator<Double> getIterator() {
		if (decimalIterator == null || !decimalIterator.hasNext()) {
			decimalIterator = decimals.iterator();
		}
		
		return decimalIterator;
	}
}
