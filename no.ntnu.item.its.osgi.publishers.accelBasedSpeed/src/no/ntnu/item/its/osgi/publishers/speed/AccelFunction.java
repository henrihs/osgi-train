package no.ntnu.item.its.osgi.publishers.speed;

import java.util.function.Function;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

public class AccelFunction implements UnivariateRealFunction {

	private Function<Double, Double> accelAsFuncOfTime;

	public AccelFunction(double a_0, double a_1, double t_0, double t_1) {
		accelAsFuncOfTime = getAccelAsFuncOfTime(a_0, a_1, t_0, t_1);
	}

	@Override
	public double value(double t) throws FunctionEvaluationException {
		return accelAsFuncOfTime.apply(t);
	}

	public Function<Double, Double> getAccelAsFuncOfTime(double a_0, double a_1, double t_0, double t_1) {
		return new Function<Double, Double>() {
			@Override
			public Double apply(Double t) {
				return a_0 + (a_1 - a_0) * ((t - t_0) / (t_1 - t_0));
			}
		};
	}

}