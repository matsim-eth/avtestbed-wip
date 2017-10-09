/* *********************************************************************** *
 * project: org.matsim.*
 * GravityCostFunction.java
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
package org.matsim.contrib.socnetgen.sna.graph.spatial.generators;


import org.matsim.contrib.common.gis.DistanceCalculator;
import org.matsim.contrib.common.gis.OrthodromicDistanceCalculator;
import org.matsim.contrib.socnetgen.sna.gis.GravityCostFunction;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialVertex;

/**
 * @author illenberger
 *
 */
public class GravityEdgeCostFunction implements EdgeCostFunction {

	private GravityCostFunction delegate;
	
	public GravityEdgeCostFunction(double gamma, double constant) {
		this(gamma, constant, new OrthodromicDistanceCalculator());
	}
	
	public GravityEdgeCostFunction(double gamma, double constant, DistanceCalculator calculator) {
		delegate = new GravityCostFunction(gamma, constant, calculator);
	}
	
	@Override
	public double edgeCost(SpatialVertex vi, SpatialVertex vj) {
		return delegate.costs(vi.getPoint(), vj.getPoint());
	}

}
