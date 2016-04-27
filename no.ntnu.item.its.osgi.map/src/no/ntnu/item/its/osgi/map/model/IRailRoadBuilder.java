package no.ntnu.item.its.osgi.map.model;

import java.io.IOException;

public interface IRailRoadBuilder<IRailroad> {
	public IRailroad build(String filePath) throws IOException;
}
