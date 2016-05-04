package no.ntnu.item.its.osgi.common.interfaces;

public interface SensorSchedulerService {
	
	/*
	 * Schedule new runnable task
	 * 
	 * @param r runnable task to schedule
	 * @param initialDelay delay for the first trigger
	 * @param period duration in microseconds between each triggering
	 * 
	 */
	public void add(Runnable r, long initialDelay, long period);
	
	/*
	 * Remove runnable task from schedule
	 * 
	 * @param r runnable task to remove
	 * @param interrupt indicates if the running task should be interrupted
	 * @returns true if the task was removed
	 */
	public boolean remove(Runnable r, boolean interrupt);

}
