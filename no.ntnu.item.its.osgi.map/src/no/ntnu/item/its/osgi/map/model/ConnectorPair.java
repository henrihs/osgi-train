package no.ntnu.item.its.osgi.map.model;

import java.util.Iterator;

import no.ntnu.item.its.osgi.map.enums.PointConnectorEnum;

public class ConnectorPair extends Pair<PointConnector, PointConnector> implements Iterable<PointConnector> {

	public ConnectorPair(PointConnector first, PointConnector second) {
		super(first, second);
	}
		
	public boolean bothOfType(PointConnectorEnum connectorType) {
		return first.getType() == connectorType 
				&& second.getType() == connectorType;
	}
	
	public boolean hasBothEnds() {
		return first != null && second != null;
	}

	@Override
	public Iterator<PointConnector> iterator() {
		return new ConnectorPairIterator();
	}

	private class ConnectorPairIterator implements Iterator<PointConnector> {

		private int index = 0;
		
		@Override
		public boolean hasNext() {
			return index < 2;
		}

		@Override
		public PointConnector next() {
			return index++ < 1 ? first() : second();
		}
		
	}
}
