package no.ntnu.item.its.osgi.publishers.color;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.item.its.osgi.common.enums.EColor;

public class FixedColorData {
	static final FixedColor GRAY = new FixedColor(new Double[][] {{21.648200,0.912928},{6.914400,0.279772},{8.104500,0.410828},{7.454300,0.529067}}, EColor.GRAY);
	static final FixedColor YELLOW = new FixedColor(new Double[][] {{138.160700,6.528788*6},{59.474500,2.607595*2.6},{55.811800,2.661049*2.6},{24.974800,1.238533*1.2}}, EColor.YELLOW);
	static final FixedColor GREEN = new FixedColor(new Double[][] {{35.538400,1.841066*1.8},{7.184700,0.705114},{18.401800,0.772889},{11.218200,0.745781}}, EColor.GREEN);
	static final FixedColor BLUE = new FixedColor(new Double[][] {{50.994000,2.297469*2.2},{6.487500,0.623894},{16.967600,0.815690},{29.677000,1.361863*1.3}}, EColor.BLUE);
	static final FixedColor RED = new FixedColor(new Double[][] {{35.529700, 2.198390*2.2},{24.225000, 1.190872*1.1},{7.373800, 0.743286},{7.108800, 0.806451}}, EColor.RED);
	static final List<FixedColor> COLORS = new ArrayList<FixedColor>(); 
	
	static {
		COLORS.add(GRAY);
		COLORS.add(YELLOW);
		COLORS.add(GREEN);
		COLORS.add(BLUE);
		COLORS.add(RED);
	}
	
	static class FixedColor {
		double[] rawColorValues = new double[4];
		double[] stdDevs = new double[4];
		EColor color;
		
		public FixedColor(Double[][] data, EColor color) {
			this.color = color;
			for (int i = 0; i < data.length; i++) {
				rawColorValues[i] = data[i][0];
				stdDevs[i] = data[i][1];	
			}
		}
	}
	}
