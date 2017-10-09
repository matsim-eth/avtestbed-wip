/* *********************************************************************** *
 * project: org.matsim.*
 * DistanceAccessibilityTask.java
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
package org.matsim.contrib.socnetgen.sna.graph.spatial.analysis;

import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.TObjectDoubleHashMap;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.matsim.contrib.common.stats.Correlations;
import org.matsim.contrib.common.stats.Discretizer;
import org.matsim.contrib.common.stats.FixedSampleSizeDiscretizer;
import org.matsim.contrib.socnetgen.sna.gis.SpatialCostFunction;
import org.matsim.contrib.socnetgen.sna.graph.Graph;
import org.matsim.contrib.socnetgen.sna.graph.analysis.ModuleAnalyzerTask;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialEdge;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialGraph;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialVertex;

import com.vividsolutions.jts.geom.Point;

/**
 * @author illenberger
 *
 */
public class DistanceAccessibilityTask extends ModuleAnalyzerTask<Distance> {

	private static final Logger logger = Logger.getLogger(DistanceAccessibilityTask.class);

	private Set<Point> opportunities;
	
	private SpatialCostFunction costFunction;

	public DistanceAccessibilityTask(Set<Point> opportunities, SpatialCostFunction costFunction) {
		setModule(new Distance());
		this.costFunction = costFunction;
		this.opportunities = opportunities;
	}
	
	@Override
	public void analyze(Graph graph, Map<String, DescriptiveStatistics> statsMap) {
		if(getOutputDirectory() != null) {
			SpatialGraph spatialGraph = (SpatialGraph) graph;
			/*
			 * mean distance
			 */
			TObjectDoubleHashMap<SpatialVertex> distMap = module.vertexMean((Set<? extends SpatialVertex>) graph.getVertices());
			TObjectDoubleHashMap<SpatialVertex> accessMap = new LogAccessibility().values((Set<? extends SpatialVertex>) graph.getVertices(), costFunction, opportunities);
			
			double[] accessValues = new double[distMap.size()];
			double[] dValues = new double[distMap.size()];
			
			TObjectDoubleIterator<SpatialVertex> it = distMap.iterator();
			for(int i = 0; i < distMap.size(); i++) {
				it.advance();
				accessValues[i] = accessMap.get((SpatialVertex) it.key());
				dValues[i] = it.value();
			}
			
			try{
				Discretizer disc = FixedSampleSizeDiscretizer.create(accessValues, 20);
				Correlations.writeToFile(Correlations.mean(accessValues, dValues, disc), String.format("%1$s/d_mean_access.txt", getOutputDirectory()), "access", "d_mean");
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*
			 * edge length 
			 */
			TDoubleArrayList accessValues2 = new TDoubleArrayList(graph.getEdges().size() * 2);
			TDoubleArrayList dValues2 = new TDoubleArrayList(graph.getEdges().size() * 2);
			
			for(SpatialEdge edge : spatialGraph.getEdges()) {
				double length = edge.length();
				if(!Double.isNaN(length)) {
					accessValues2.add(accessMap.get(edge.getVertices().getFirst()));
					dValues2.add(length);
					
					accessValues2.add(accessMap.get(edge.getVertices().getSecond()));
					dValues2.add(length);
				}
			}
			try{
				accessValues = accessValues2.toArray();
				dValues = dValues2.toArray();
				Discretizer disc = FixedSampleSizeDiscretizer.create(accessValues, 100);
				Correlations.writeToFile(Correlations.mean(accessValues, dValues, disc), String.format("%1$s/d_access.txt", getOutputDirectory()), "access", "d");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			logger.warn("No output directory specified!");
		}
		
	}

}
