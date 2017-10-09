/* *********************************************************************** *
 * project: org.matsim.*
 * ObservedEdgeLengthMedian.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.contrib.socnetgen.sna.snowball.spatial.analysis;

import gnu.trove.map.hash.TObjectDoubleHashMap;

import java.util.Set;

import org.matsim.contrib.socnetgen.sna.graph.Vertex;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialVertex;
import org.matsim.contrib.socnetgen.sna.graph.spatial.analysis.EdgeLengthMedian;
import org.matsim.contrib.socnetgen.sna.snowball.spatial.SpatialSampledVertexDecorator;

/**
 * @author illenberger
 *
 */
public class ObservedEdgeLengthMedian extends EdgeLengthMedian {

	@Override
	public TObjectDoubleHashMap<Vertex> values(Set<? extends Vertex> vertices) {
		@SuppressWarnings("unchecked")
		Set<SpatialSampledVertexDecorator<SpatialVertex>> spatialVertices = (Set<SpatialSampledVertexDecorator<SpatialVertex>>)vertices;
		return super.values(spatialVertices);
	}

}
