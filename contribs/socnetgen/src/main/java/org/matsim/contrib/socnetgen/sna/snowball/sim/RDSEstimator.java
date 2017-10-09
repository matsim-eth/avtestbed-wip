/* *********************************************************************** *
 * project: org.matsim.*
 * RDSEstimator.java
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
package org.matsim.contrib.socnetgen.sna.snowball.sim;

import org.matsim.contrib.socnetgen.sna.snowball.SampledGraph;
import org.matsim.contrib.socnetgen.sna.snowball.SampledVertex;
import org.matsim.contrib.socnetgen.sna.snowball.analysis.PiEstimator;

/**
 * @author illenberger
 * 
 */
public class RDSEstimator implements PiEstimator {

	private double denominator;

	private final int N;

	public RDSEstimator(int N) {
		this.N = N;
	}
	
	@Override
	public void update(SampledGraph graph) {
		double sum_k = 0;
		for (SampledVertex v : graph.getVertices()) {
			if (v.isSampled()) {
				sum_k += 1 / (double) v.getNeighbours().size();
			}
		}

		denominator = N / sum_k;
	}

	@Override
	public double probability(SampledVertex vertex) {
		return vertex.getNeighbours().size()/denominator;
	}

	@Override
	public double probability(SampledVertex vertex, int iteration) {
		return vertex.getNeighbours().size()/denominator;
	}
}
