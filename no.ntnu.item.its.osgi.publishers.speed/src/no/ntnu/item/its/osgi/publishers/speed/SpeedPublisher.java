package no.ntnu.item.its.osgi.publishers.speed;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.*;
import org.apache.commons.math.analysis.integration.*;

import no.ntnu.item.its.osgi.sensors.common.interfaces.AccelerationControllerService;

public class SpeedPublisher implements EventHandler {

	public static final String VX_KEY = "velocity.x";
	public static final String EVENT_TOPIC = "no/ntnu/item/its/osgi/sensors/speed";
	public static final String TIMESTAMP_KEY = "data.timestamp";
	
	private PreSpeedEvent<TrapezoidIntegrator> latestSpeedEvent;
	private TrapezoidIntegrator integrator = new TrapezoidIntegrator();

	public SpeedPublisher() {
		String[] topics = new String[] { AccelerationControllerService.EVENT_TOPIC };
		Hashtable<String, Object> serviceProps = new Hashtable<String, Object>();
		serviceProps.put(EventConstants.EVENT_TOPIC, topics);
		SpeedPubActivator.getContext().registerService(EventHandler.class.getName(), this, serviceProps);
	}

	@Override
	public void handleEvent(Event arg0) {
		double a_x = (double) arg0.getProperty(AccelerationControllerService.X_DATA_KEY);
		long timestamp = (long) arg0.getProperty(AccelerationControllerService.TIMESTAMP_KEY);
		PreSpeedEvent<TrapezoidIntegrator> preSpeedEvent = new PreSpeedEvent<TrapezoidIntegrator>(a_x, timestamp, integrator);
		if (latestSpeedEvent != null) {
			preSpeedEvent.calculateXVelocityDelta(latestSpeedEvent);
		}

		publish(preSpeedEvent.v_x);

		latestSpeedEvent = preSpeedEvent;
	}

	private void publish(double v_x) {
		if (!SpeedPubActivator.eventAdminTracker.isEmpty()) {
			Event speedEvent = createEvent(v_x);			
			((EventAdmin) SpeedPubActivator.eventAdminTracker.getService()).postEvent(speedEvent);
		}

		else if (!SpeedPubActivator.logServiceTracker.isEmpty()) {
			((LogService) SpeedPubActivator.logServiceTracker.getService()).log(
					LogService.LOG_DEBUG, 
					"Failed to publish event, no EventAdmin service available!");
		}
	}

	private Event createEvent(double v_x) {
		Map<String, Object> properties = new Hashtable<>();
		properties.put(
				SpeedPublisher.TIMESTAMP_KEY, 
				System.nanoTime());
		properties.put(SpeedPublisher.VX_KEY, v_x);
		Event speedEvent = new Event(SpeedPublisher.EVENT_TOPIC, properties);
		return speedEvent;
	}

	private static class PreSpeedEvent<T extends UnivariateRealIntegratorImpl> {

		private long timestamp;
		private final double a_x;
		private double v_x = 0;
		private T integrator; 

		public PreSpeedEvent(double a_x, long timestamp, T integrator) {
			this.timestamp = timestamp;
			this.a_x = a_x;
			this.integrator = integrator;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void calculateXVelocityDelta(PreSpeedEvent priorEvent) {
			double a_0 = priorEvent.a_x;
			double a_1 = this.a_x;
			long t_0 = priorEvent.getTimestamp();
			long t_1 = getTimestamp();

			double v_0 = priorEvent.v_x;
			Double v_delta = null;

			AccelExpression a = new AccelExpression(a_0, a_1, t_0, t_1);
			try {
				v_delta = integrator.integrate(a, t_0, t_1);
			} catch (FunctionEvaluationException | IllegalArgumentException | ConvergenceException e) {
				((LogService)SpeedPubActivator.logServiceTracker.getService())
				.log(LogService.LOG_ERROR, "Could not integrate acceleration expression", e);
			}

			this.v_x = v_0 + v_delta;
		}

		public String toString() {
			return String.format("Time: %d, Acceleration: %f, Speed: %f", timestamp, a_x, v_x);
		}
	}

	private static class AccelExpression implements UnivariateRealFunction {

		private Function<Double, Double> accelAsFuncOfTime;

		public AccelExpression(double a_0, double a_1, long t_0, long t_1) {
			accelAsFuncOfTime = getAccelAsFuncOfTime(a_0, a_1, t_0, t_1);
		}

		@Override
		public double value(double t) throws FunctionEvaluationException {
			return accelAsFuncOfTime.apply(t);
		}

		public Function<Double, Double> getAccelAsFuncOfTime(double a_0, double a_1, long t_0, long t_1) {
			return new Function<Double, Double>() {
				@Override
				public Double apply(Double t) {
					return a_0 + (a_1 - a_0) * ((t - t_0) / (t_1 - t_0));
				}
			};
		}

	}
}
