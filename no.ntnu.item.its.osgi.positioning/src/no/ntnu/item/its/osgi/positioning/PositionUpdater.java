package no.ntnu.item.its.osgi.positioning;

import java.io.IOException;
import org.osgi.service.log.LogService;

import no.ntnu.item.its.osgi.map.model.Railroad;
import no.ntnu.item.its.osgi.map.model.RailroadBuilder;

public class PositionUpdater {
	private Railroad railroad;
	
	public PositionUpdater(String mapFilePath) {
		try {
			railroad = new RailroadBuilder().build(mapFilePath);
			
		} catch (IOException e) {
			((LogService) PositioningActivator.logServiceTracker.getService()).log(
					LogService.LOG_ERROR, 
					"Failed to generate railroad map");
		}
	}
}
