package org.slf4j;

public class LoggerFactory {

	public static Logger getLogger(String name) {
		return new no.ntnu.item.its.osgi.logger.Logger(name);
	}

	public static Logger getLogger(java.lang.Class clazz) {
		return getLogger(clazz.getSimpleName());
	}
	
}
