package no.ntnu.item.its.osgi.publishers.color;

import java.util.HashMap;
import java.util.Map.Entry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import no.ntnu.item.its.train.tcs.EColor;
import no.ntnu.item.its.train.tcs.TCS34725.TCSColor;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	private static EColor colorApproximation(TCSColor colorToCompare) {
		  HashMap<EColor, Double> colors = new HashMap<EColor, Double>();
		  colors.put(EColor.RED,null);
		  colors.put(EColor.BLUE,null);
		  colors.put(EColor.GREEN,null);
		  colors.put(EColor.GRAY,null);
		  colors.put(EColor.YELLOW,null);
//		  colors.put(EColor.BROWN,null);
		  
		  for (EColor fixedColor : colors.keySet()) {
			colors.put(fixedColor, Math.abs(colorDistance(colorToCompare, new TCSColor(fixedColor))));
		  }
		  
		  Entry<EColor, Double> minEntry = null;
		  
		  for (Entry<EColor, Double> entry : colors.entrySet()) {
			  if (minEntry == null || entry.getValue() < minEntry.getValue())
				  minEntry = entry;
		  }
		  
		  if (minEntry.getValue() > 196) { // Don't make too approximated approximations!
			  return null;
		  }
		  
		  return minEntry.getKey();
	}
	
	private int getComponent(int i) {
		return i*255/c;
	}
	
	private static double colorDistance(TCSColor c1, TCSColor c2) {
		  int rmean = ( c1.getRedComponent() + c2.getRedComponent()) / 2;
		  int r = Math.abs(c1.getRedComponent() - c2.getRedComponent());
		  int g = Math.abs(c1.getGreenComponent() - c2.getGreenComponent());
		  int b = Math.abs(c1.getBlueComponent() - c2.getBlueComponent());
		  
		  return r*r + g*g + b*b;
	  }

}
