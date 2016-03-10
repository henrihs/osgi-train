package no.ntnu.item.its.osgi.test;

import java.util.Enumeration;

import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

public class ConsoleLogger implements LogReaderService {

	@Override
	public void addLogListener(LogListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Enumeration getLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLogListener(LogListener arg0) {
		// TODO Auto-generated method stub
		
	}

}
