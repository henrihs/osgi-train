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
	
	private StatisticalGenerator statistic;
	private final ArrayList<Double> decimals; 
	private Iterator<Double> decimalIterator;

	public AccelerationControllerMocker() {
		statistic = new StatisticalGenerator(0.1);
		decimals = new ArrayList<>();
		for (double i = -1.0; i < 1; i+=0.001) {
			decimals.add(i);
		}
		
		ArrayList<Double> reverseDecimals = (ArrayList<Double>) decimals.clone();
		Comparator<Double> comparator = Collections.reverseOrder();
		Collections.sort(reverseDecimals,comparator);
		decimals.addAll(reverseDecimals);
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
		Double d = getIterator().next()*1024;
		return d.intValue();
	}

	private Iterator<Double> getIterator() {
		if (decimalIterator == null || !decimalIterator.hasNext()) {
			decimalIterator = decimals.iterator();
		}
		
		return decimalIterator;
	}
}
