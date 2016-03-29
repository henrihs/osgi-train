package no.ntnu.item.its.osgi.sensors.common.interfaces;

public interface SensorSchedulerService {
	
	/*
	 * Schedule new runnable task
	 * 
	 * @param r runnable task to schedule
	 * @param period duration in microseconds between each triggering
	 */
	public void add(Runnable r, long period);
	
	/*
	 * Remove runnable task from schedule
	 * 
	 * @param r runnable task to remove
	 * 
	 * @returns true if the task was removed
	 */
	public boolean remove(Runnable r);
}
