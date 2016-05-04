package no.ntnu.item.its.osgi.common.servicetrackers;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import no.ntnu.item.its.osgi.common.enums.PublisherType;
import no.ntnu.item.its.osgi.common.interfaces.SensorSchedulerService;

public class SchedulerTrackerCustomizer implements 
ServiceTrackerCustomizer<SensorSchedulerService, SensorSchedulerService> {
	
	private BundleContext context;
	private Runnable task;
	private long period;

	public SchedulerTrackerCustomizer(BundleContext context, Runnable task, long period) {
		this.context = context;
		this.task = task;
		this.period = period;
	}

	@Override
	public SensorSchedulerService addingService(ServiceReference<SensorSchedulerService> arg0) {
		SensorSchedulerService scheduler = context.getService(arg0);
		scheduler.add(task, period, period);
		ServiceReference<LogService> logRef = context.getServiceReference(LogService.class);
		if (logRef != null) {
			context.getService(logRef).log(
					LogService.LOG_INFO, 
					String.format("Executing periodic sensor readings via %s", 
							arg0.getBundle().getSymbolicName()));
		}
		
		return scheduler;
	}

	@Override
	public void modifiedService(ServiceReference<SensorSchedulerService> arg0, SensorSchedulerService arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removedService(ServiceReference<SensorSchedulerService> arg0, SensorSchedulerService arg1) {
		ServiceReference<LogService> logRef = context.getServiceReference(LogService.class);
		if (logRef != null) {
			context.getService(logRef).log(
					LogService.LOG_WARNING, 
					"No longer executing sensor readings, scheduling service is down!");
		}
	}

}
