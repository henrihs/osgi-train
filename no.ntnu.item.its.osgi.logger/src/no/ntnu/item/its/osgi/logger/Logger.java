package no.ntnu.item.its.osgi.logger;

import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Logger implements org.slf4j.Logger {
	
	private ServiceTracker<LogService, Object> logTracker;
	private final String name;
	
	public Logger(String name) {
		this.name = " [" + name + "] ";
		logTracker = 
				new ServiceTracker<LogService, Object>
						(Activator.getContext(), LogService.class, null);
		logTracker.open();
	}
	
	private LogService logger() {
		return (LogService)logTracker.getService();
	}

	@Override
	public void warn(String s1) {
		logger().log(LogService.LOG_WARNING, name.concat(s1));
	}

	@Override
	public void warn(String s1, String s2) {
		logger().log(LogService.LOG_WARNING, String.format(name.concat(s1), s2));	
	}

	@Override
	public void warn(String s1, Throwable t) {
		logger().log(LogService.LOG_WARNING, name.concat(s1), t);	
	}

	@Override
	public void error(String s1) {
		logger().log(LogService.LOG_ERROR, name.concat(s1));
	}

	@Override
	public void error(String s1, String s2) {
		logger().log(LogService.LOG_ERROR, String.format(name.concat(s1), s2));	
	}

	@Override
	public void error(String s1, Throwable t) {
		logger().log(LogService.LOG_ERROR, name.concat(s1), t);	
	}
	
	@Override
	public void info(String s1) {
		logger().log(LogService.LOG_INFO, name.concat(s1));
	}

	@Override
	public void info(String s1, String s2) {
		logger().log(LogService.LOG_INFO, String.format(name.concat(s1), s2));	
	}

	@Override
	public void info(String s1, Throwable t) {
		logger().log(LogService.LOG_INFO, name.concat(s1), t);	
	}
	
	@Override
	public void debug(String s1) {
		logger().log(LogService.LOG_DEBUG, name.concat(s1));
	}

	@Override
	public void debug(String s1, String s2) {
		logger().log(LogService.LOG_DEBUG, String.format(name.concat(s1), s2));	
	}

	@Override
	public void debug(String s1, Throwable t) {
		logger().log(LogService.LOG_DEBUG, name.concat(s1), t);	
	}
	
	@Override
	public void trace(String s1) {
		logger().log(LogService.LOG_DEBUG, name.concat(s1));
	}

	@Override
	public void trace(String s1, String s2) {
		logger().log(LogService.LOG_DEBUG, String.format(name.concat(s1), s2));	
	}

	@Override
	public void trace(String s1, Throwable t) {
		logger().log(LogService.LOG_DEBUG, name.concat(s1), t);	
	}

	@Override
	public void trace(String s1, Object o) {
		logger().log(LogService.LOG_DEBUG, String.format(name.concat(s1), o));
	}

}
