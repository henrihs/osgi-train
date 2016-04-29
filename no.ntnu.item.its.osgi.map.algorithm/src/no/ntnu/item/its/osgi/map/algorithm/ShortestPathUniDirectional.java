package no.ntnu.item.its.osgi.map.algorithm;

import no.ntnu.item.its.osgi.common.exceptions.RouteNotFoundException;
import no.ntnu.item.its.osgi.map.model.IRailroad;
import no.ntnu.item.its.osgi.map.model.PointConnector;
import no.ntnu.item.its.osgi.map.model.Route;
import no.ntnu.item.its.osgi.map.model.RouteDescriptor;

public interface ShortestPathUniDirectional {
	Route findSingleShortestPath(IRailroad railroad, RouteDescriptor routeDescriptor, PointConnector direction) throws RouteNotFoundException;
}
