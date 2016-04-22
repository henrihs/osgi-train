package no.ntnu.item.its.osgi.publishers.speed;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.integration.UnivariateRealIntegratorImpl;
import org.osgi.service.log.LogService;

public class VelocityData<T extends UnivariateRealIntegratorImpl> implements Comparable<VelocityData<?>> {

	private long timestamp;
	private final double a_x;
	double v_delta = 0;
	private T integrator; 

	public VelocityData(double a_x, long timestamp, T integrator) {
		this.timestamp = timestamp/1000; // microsecond accuracy
		this.a_x = a_x;
		this.integrator = integrator;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public synchronized void calculateVelocityDelta(VelocityData<?> priorEvent) {
		try {
			double a_0 = priorEvent.a_x;
			double a_1 = this.a_x;
			
			// If a_0 and a_1 are polar opposites, the change in speed is always equal to zero
			// In addition, integration over such an interval fails, iff a_0 is negative and a_1 = -(a_0)
			if (a_1 == -(a_0)) {
				v_delta = 0;
				return;
			}
			
			double t_0 = priorEvent.getTimestamp()*1E-6; // second resolution (SU-unit)
			double t_1 = getTimestamp()*1E-6; // second resolution (SU-unit)
	
			AccelFunction a = new AccelFunction(a_0, a_1, t_0, t_1);
			v_delta = integrator.integrate(a, t_0, t_1);
		} catch (FunctionEvaluationException | IllegalArgumentException | ConvergenceException e) {
			((LogService)VelocityPubActivator.logServiceTracker.getService())
			.log(LogService.LOG_ERROR, "Could not integrate acceleration expression: ", e);
		} catch (Exception e) {
			((LogService)VelocityPubActivator.logServiceTracker.getService())
			.log(LogService.LOG_ERROR, "Unknown error occured: ", e);
		}

	}

	public String toString() {
		return String.format("Time: %d ms, Acceleration: %f m/s^2, delta V: %f m/s", timestamp, a_x, v_delta);
	}

	@Override
	public int compareTo(VelocityData<?> o) {
		return Double.compare(this.timestamp, o.timestamp);
	}
}