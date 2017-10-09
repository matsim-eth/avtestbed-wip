/* *********************************************************************** *
 * project: org.matsim.*
 * ObservedAccessability.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
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

import org.matsim.contrib.socnetgen.sna.gis.SpatialCostFunction;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialVertex;
import org.matsim.contrib.socnetgen.sna.graph.spatial.analysis.LogAccessibility;
import org.matsim.contrib.socnetgen.sna.math.Distribution;
import org.matsim.contrib.socnetgen.sna.snowball.SampledVertex;
import org.matsim.contrib.socnetgen.sna.snowball.analysis.SnowballPartitions;

import com.vividsolutions.jts.geom.Point;

/**
 * @author illenberger
 *
 */
public class ObservedLogAccessibility extends LogAccessibility {

	@Override
	public Distribution distribution(Set<? extends SpatialVertex> vertices, SpatialCostFunction costFunction,
			Set<Point> opportunities) {
		return super.distribution((Set<? extends SpatialVertex>) SnowballPartitions.createSampledPartition((Set<? extends SampledVertex>)vertices), costFunction, opportunities);
	}

	@Override
	public TObjectDoubleHashMap<SpatialVertex> values(Set<? extends SpatialVertex> vertices,
			SpatialCostFunction costFunction, Set<Point> opportunities) {
		return super.values((Set<? extends SpatialVertex>) SnowballPartitions.createSampledPartition((Set<? extends SampledVertex>)vertices), costFunction, opportunities);
	}

}
