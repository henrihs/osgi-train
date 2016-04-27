package no.ntnu.item.its.osgi.map.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import bluebrick4j.api.MapFactory;
import bluebrick4j.model.*;

public class RailroadBuilder implements IRailRoadBuilder<Railroad> {

	public Railroad build(String filePath) throws IOException{
		if (filePath.endsWith(".bbm"))
		{
			return convertFromBbmFile(filePath);
		}
		else if (filePath.endsWith(".map"))
		{
			return convertFromStoredObject(filePath);
		}
		return null;
	}

	private static Railroad convert(Map map) {
		Railroad railroad = new Railroad();
		ArrayList<Brick> bricks = new ArrayList<>();
		LinkedList<Brick> pointSwitchBricks = new LinkedList<>();

		for (Layer layer : map.getLayers().getLayers()) {
			if (layer.getBricks() != null) {
				layer.getBricks().getBricks().forEach((b) -> {
					bricks.add(b);
					if (b.getBrickType() == BrickType.LEFTHANDPOINTSWITCH
							|| b.getBrickType() == BrickType.RIGHTHANDPOINTSWITCH){
						pointSwitchBricks.add(b);
					}
				});
			}
		}

		HashMap<Connexion, Brick> connexionToBrickMapping = ConnectionToBrickMapping(bricks);
		HashSet<Object> visited = new HashSet<>();

		while (!pointSwitchBricks.isEmpty()){
			Brick brick = pointSwitchBricks.getFirst();
			BrickType type = brick.getBrickType();
			if (visited.contains(brick)) { 
				continue; 
			}

			Point startPoint = railroad.findOrAddPoint(brick.getId());
			List<Connexion> connections = brick.getConnexions().getConnexions();
			for (int i = 0; i < 3; i++) {
				Connexion nextConnection = connections.get(i).getLinkedTo();
				PointConnector startPointConnector = startPoint.getConnector(ConnectorConverter.convert(type).apply(i));
				if (!railroad.hasRailLegWithConnector(startPointConnector)) {
					RailLeg leg = stepInto(railroad, connexionToBrickMapping, nextConnection, startPointConnector, visited);
					if (leg instanceof RegularLeg) {
						railroad.addRailLeg((RegularLeg) leg);
					}
					else if (leg instanceof StartLeg){
						railroad.setRailSystemStartLeg((StartLeg) leg);
					}
				}
			}

			pointSwitchBricks.removeFirst();
		}

		return railroad;
	}

	private static Railroad convertFromBbmFile(String bbmFilePath) throws IOException {
		Map map = null;
		try {
			map = MapFactory.getMapFromBBM(bbmFilePath);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convert(map);
	}

	private static Railroad convertFromStoredObject(String mapFilePath) {
		Map map = loadMapFromStoredFile(mapFilePath);
		return convert(map);
	}

	private static RailLeg stepInto(Railroad railroad,
			HashMap<Connexion, Brick> connexionToBrickMapping, 
			Connexion connexion, 
			PointConnector startConnector, 
			HashSet<Object> visited
			) 
	{
		if (connexion == null) {
			return null;
		}

		visited.add(connexion);
		Brick brick = connexionToBrickMapping.get(connexion);

		if (brick.getBrickType() != BrickType.LEFTHANDPOINTSWITCH
				&& brick.getBrickType() != BrickType.RIGHTHANDPOINTSWITCH) {

			for (Connexion nextConnexion : brick.getConnexions().getConnexions()) {
				if (nextConnexion != connexion) {
					RailLeg fullLeg;
					if (nextConnexion.getLinkedTo() != null) {
						fullLeg = stepInto(railroad, connexionToBrickMapping, nextConnexion.getLinkedTo(), startConnector, visited);
					}
					else {
						fullLeg = new StartLeg(startConnector);
					}
					fullLeg.addRailBrick(new RailBrick(brick.getId(), fullLeg, brick.getBrickType()));
					return fullLeg;

				}
			}
		}

		Point endOfLeg = railroad.findOrAddPoint(brick.getId());
		int index = brick.getConnexions().getConnexions().indexOf(connexion);
		PointConnector endConnector = endOfLeg.getConnector(ConnectorConverter.convert(brick.getBrickType()).apply(index));

		RegularLeg fullLeg = new RegularLeg(endConnector, startConnector);
		return fullLeg;
	}

	private static HashMap<Connexion, Brick> ConnectionToBrickMapping(List<Brick> bricks) {
		HashMap<Connexion, Brick> map = new HashMap<>();
		for (Brick brick : bricks) {
			for (Connexion connection : brick.getConnexions().getConnexions()) {
				map.put(connection, brick);
			}
		}

		return map;
	}

	private static Map loadMapFromStoredFile(String path) {
		try(
				InputStream file = new FileInputStream(path);
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream (buffer);
				)
		{
			return (Map)input.readObject();
		}
		catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}

		return null;
	}
}
