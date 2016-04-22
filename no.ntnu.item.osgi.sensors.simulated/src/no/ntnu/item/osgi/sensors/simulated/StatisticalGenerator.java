package no.ntnu.item.osgi.sensors.simulated;

import java.util.Random;

public class StatisticalGenerator {
	private Random r;
	private double p;
	private int lastInt;

	public StatisticalGenerator(double probability) {
		if (p < 0 || p > 1) throw new IllegalArgumentException("Invalid probability (0 <= p <= 1)");
		r = new Random();
		p = probability;
	}
	
	/*
	 * returns true with probability p
	 * returns false with probability 1-p
	 */
	public boolean pollDistribution() {
		return getRandInt() <= p*100;
	}

	
	/*
	 * returns a new int between 0 and 100 with probability p
	 * returns the same int as last time with probability 1-p
	 */
	public int newInt() {
		if (getRandInt() <= p*100) {
			lastInt = getRandInt();
		}
			
		return lastInt; 
	}
	
	public int getRandInt() {
		int randInt = r.nextInt(100);
		return randInt;
	}
	
	

	public int newPolarInt() {
		int polarity = 1;
		if (r.nextBoolean())
			polarity = -1;
		return r.nextInt(100) * polarity;
	}
}
