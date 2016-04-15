package no.ntnu.item.its.osgi.publishers.speed;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.integration.UnivariateRealIntegratorImpl;
import org.osgi.service.log.LogService;

public class VelocityData<T extends UnivariateRealIntegratorImpl> {

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
		double a_0 = priorEvent.a_x;
		double a_1 = this.a_x;
		double t_0 = priorEvent.getTimestamp()*1E-6; // second resolution (SU-unit)
		double t_1 = getTimestamp()*1E-6; // second resolution (SU-unit)

		AccelFunction a = new AccelFunction(a_0, a_1, t_0, t_1);
		try {
			v_delta = integrator.integrate(a, t_0, t_1);
		} catch (FunctionEvaluationException | IllegalArgumentException | ConvergenceException e) {
			((LogService)VelocityPubActivator.logServiceTracker.getService())
			.log(LogService.LOG_ERROR, "Could not integrate acceleration expression: ", e);
		}

	}

	public String toString() {
		return String.format("Time: %d ms, Acceleration: %f m/s^2, delta V: %f m/s", timestamp, a_x, v_delta);
	}
}