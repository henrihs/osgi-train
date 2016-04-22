package test;

import org.apache.commons.math.analysis.integration.TrapezoidIntegrator;

import no.ntnu.item.its.osgi.publishers.speed.VelocityData;

//import no.ntnu.item.its.osgi.publishers.speed.SpeedPubActivator;
//import no.ntnu.item.its.osgi.publishers.speed.SpeedPublisher.AccelExpression;
//import no.ntnu.item.its.osgi.publishers.speed.SpeedPublisher.PreSpeedEvent;

public class Main {
	
	public static void main(String[] args) {
		TrapezoidIntegrator t = new TrapezoidIntegrator();
		VelocityData<TrapezoidIntegrator> v0 = new VelocityData<TrapezoidIntegrator>(-0.011112890624999996, 9317271594525L, t);
		VelocityData<TrapezoidIntegrator> v1 = new VelocityData<TrapezoidIntegrator>(0.007664062500000001, 9317446991589L, t);
		
		v1.calculateVelocityDelta(v0);
		System.out.println(v0);
		System.out.println(v1);
	}
	
	/*
	 * 9317271594525, -0.011112890624999996
	 * 9317446991589, 0.007664062500000001
	 */
}
		
//		TrapezoidIntegrator integrator = new TrapezoidIntegrator();
////		measureIntegrationTime(integrator);
////		SimpsonIntegrator simpsons = new SimpsonIntegrator();
////		measureIntegrationTime(simpsons);
////		RombergIntegrator romberg = new RombergIntegrator();
////		measureIntegrationTime(romberg);
//		
//		VelocityData<TrapezoidIntegrator> first = new VelocityData<TrapezoidIntegrator>(0, 0, integrator);
//		VelocityData<TrapezoidIntegrator> second = new VelocityData<TrapezoidIntegrator>(1, 10000000000L, integrator);
//		second.calculateVelocityDelta(first);
//		VelocityData<TrapezoidIntegrator> third = new VelocityData<TrapezoidIntegrator>(0, 20000000000L, integrator);
//		third.calculateVelocityDelta(second);
//		System.out.println(first);
//		System.out.println(second);
//		System.out.println(third);
//		
////		AccelFunction accel = new AccelFunction(0, 1, 0, 1);
////		AccelFunction brreak = new AccelFunction(1, 0, 1, 2);
////		for (int i = 1; i < 11; i++) {
////			try {
////				System.out.println(accel.value(i*0.1));
////			} catch (FunctionEvaluationException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////		}
////		
////		for (int i = 1; i < 11; i++) {
////			try {
////				System.out.println(brreak.value(1+i*0.1));
////			} catch (FunctionEvaluationException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////		}
//		
//		
//	}
//
//
//
//	private static void measureIntegrationTime(UnivariateRealIntegratorImpl integrator) {
//		long s1 = System.nanoTime();
//		PreSpeedEvent[] events = new PreSpeedEvent[10000];
//		events[0] = new PreSpeedEvent(0, integrator);
//		for (int i = 1; i < events.length; i++) {
//			PreSpeedEvent event = new PreSpeedEvent(i, integrator);
//			event.calculateXVelocityDelta(events[i-1]);
//			events[i] = event;
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		long s2 = System.nanoTime();
//		System.out.println(String.format("%s: %d", integrator.getClass().getSimpleName(), (s2-s1)/events.length-1000000));
//	}
//	
//	private static class PreSpeedEvent<T extends UnivariateRealIntegratorImpl> {
//		
//		private long timestamp;
//		private final double a_x;
//		private double v_x = 0;
//		private T integrator; 
//		
//		public PreSpeedEvent(double a_x, T integrator) {
//			timestamp = System.nanoTime();
//			this.a_x = a_x;
//			this.integrator = integrator;
//		}
//		
//		public long getTimestamp() {
//			return timestamp;
//		}
//		
//		public void setTimestamp(long t) {
//			timestamp = t;
//		}
//		
//		public void calculateXVelocityDelta(PreSpeedEvent priorEvent) {
//			double a_0 = priorEvent.a_x;
//			double a_1 = this.a_x;
//			long t_0 = priorEvent.getTimestamp();
//			long t_1 = getTimestamp();
//			
//			double v_0 = priorEvent.v_x;
//			Double v_delta = null;
//			
//			AccelExpression a = new AccelExpression(a_0, a_1, t_0, t_1);
//			try {
//				v_delta = integrator.integrate(a, t_0, t_1);
//			} catch (FunctionEvaluationException | IllegalArgumentException | ConvergenceException e) {
//				e.printStackTrace();
//			} 
//			
//			this.v_x = v_0 + v_delta;
//		}
//		
//		public String toString() {
//			return String.format("Time: %d, Acceleration: %f, Speed: %f", timestamp, a_x, v_x);
//		}
//	}
//	
//	private static class AccelExpression implements UnivariateRealFunction {
//	
//		private Function<Double, Double> accelAsFuncOfTime;
//		
//		public AccelExpression(double a_0, double a_1, long t_0, long t_1) {
//			accelAsFuncOfTime = getAccelAsFuncOfTime(a_0, a_1, t_0, t_1);
//		}
//		
//		@Override
//		public double value(double t) throws FunctionEvaluationException {
//			return accelAsFuncOfTime.apply(t);
//		}
//		
//		public Function<Double, Double> getAccelAsFuncOfTime(double a_0, double a_1, long t_0, long t_1) {
//			return new Function<Double, Double>() {
//				@Override
//				public Double apply(Double t) {
//					return a_0 + (a_1 - a_0) * ((t - t_0) / (t_1 - t_0));
//				}
//			};
//		}
//
//	}
//
//
//
//	private static void arrayTest() {
//		long start1 = Clock.systemUTC().millis();
//		for (int i = 0; i < 1000000000; i++) {
//			int[] a = new int[] {i, i+1, i+2, i+3};
////			int j = a[0];
////			int k = a[1];
////			int l = a[2];
////			int m = a[3];
//		}
//		
//		long end1 = Clock.systemUTC().millis();
//		
//		long start2 = Clock.systemUTC().millis();
//		for (int i = 0; i < 1000000000; i++) {
//			ArrayEncapsulator a = new ArrayEncapsulator(i, i+1, i+2, i+3);
////			int j = a.c;
////			int k = a.r;
////			int l = a.g;
////			int m = a.b;
//		}
//		long end2 = Clock.systemUTC().millis();
//		
//		System.out.println(end1-start1);
//		System.out.println(end2-start2);
//	}
//	
//	
//	
//	private static void byteToStringTest() throws UnsupportedEncodingException, SizeLimitExceededException {
//		byte[] b1 = "012345678".getBytes("utf-8");
//		byte[] b2 = pad(b1);
//		
//		System.out.println(b1.length);
//		System.out.println(b2.length);
//		
//		for (byte b : b1) {
//			System.out.print(b);
//		}
//		System.out.println();
//		for (byte b : b2) {
//			System.out.print(b);
//		}
//		System.out.println();
//		
//		System.out.println(new String(b1));
//		System.out.println(new String(b2));
//		
//		System.out.println(parse(b1));
//		System.out.println(parse(b2));
//	}
//	
//	private static String printSignedHex(byte b) {
//		if (b < 0) {
//			String s = Integer.toHexString(b);
//			return (s.substring(s.length()-2));
//		}
//		else return (Integer.toHexString(b));
//	}
//
//	private static void printBuffer(int i, byte[] dataBuffer) {
//		String s = (String.format("Block %d content: [", i));
//		for (int j = 0; j < 16; j++) {
//			s += printSignedHex( dataBuffer[j] );
//		}
//		s += ("]");
//		System.out.println(s);
//	}
//	
//	private static byte[] pad(byte[] oldBytes) throws SizeLimitExceededException {
//		if (oldBytes.length > 16) {
//			throw new SizeLimitExceededException("Too large data set");
//		}
//		
//		byte[] newBytes = new byte[16];
//		int diff = newBytes.length-oldBytes.length;
//		for (int i = 0; i < newBytes.length; i++) {
//			newBytes[i] = i >= diff ? oldBytes[i-diff] : 0x00;
//		}
//		
//		return newBytes;
//	}
//	
//	private static String parse(byte[] array) {
//		ArrayList<Byte> bytes = new ArrayList<Byte>();
//		for (byte b : array) {
//			if (b != 0x00) {
//				bytes.add(b);
//			}
//		}
//		
//		byte[] b = new byte[bytes.size()];
//		for (int i = 0; i < b.length; i++) {
//			b[i] = bytes.get(i);
//		}
//		
//		return new String(b);
//	}
//}
