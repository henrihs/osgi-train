package org.slf4j;

public interface Logger {
	public void warn(String s1);
	public void warn(String s1, String s2);
	public void warn(String s1, Throwable t);

	public void info(String s1);
	public void info(String s1, String s2);
	public void info(String s1, Throwable t);

	public void debug(String s1);
	public void debug(String s1, String s2);
	public void debug(String s1, Throwable t);
	
	public void error(String s1);
	public void error(String s1, String s2);
	public void error(String s1, Throwable t);

	public void trace(String s1);
	public void trace(String s1, String s2);
	public void trace(String s1, Throwable t);
	public void trace(String s1, Object o);
	public void trace(String s1, Object o1, Object o2);
	public void warn(String s1, Object o1, Object o2);
}
