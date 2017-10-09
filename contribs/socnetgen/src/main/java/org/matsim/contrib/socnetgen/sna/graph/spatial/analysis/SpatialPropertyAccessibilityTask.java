/* *********************************************************************** *
 * project: org.matsim.*
 * SpatialPropertyAccessibilityTask.java
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

import gnu.trove.map.hash.TDoubleDoubleHashMap;
import gnu.trove.map.hash.TDoubleObjectHashMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.matsim.contrib.common.stats.Discretizer;
import org.matsim.contrib.common.stats.DummyDiscretizer;
import org.matsim.contrib.common.stats.StatsWriter;
import org.matsim.contrib.socnetgen.sna.gis.SpatialCostFunction;
import org.matsim.contrib.socnetgen.sna.graph.Graph;
import org.matsim.contrib.socnetgen.sna.graph.analysis.Degree;
import org.matsim.contrib.socnetgen.sna.graph.analysis.ModuleAnalyzerTask;
import org.matsim.contrib.socnetgen.sna.graph.analysis.Transitivity;
import org.matsim.contrib.socnetgen.sna.graph.analysis.VertexPropertyCorrelation;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialGraph;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialVertex;

import com.vividsolutions.jts.geom.Point;

/**
 * @author illenberger
 * 
 */
public class SpatialPropertyAccessibilityTask extends ModuleAnalyzerTask<LogAccessibility> {

	private Discretizer discretizer = new DummyDiscretizer();

	private SpatialCostFunction costFunction;

	private Set<Point> points;

	public SpatialPropertyAccessibilityTask(SpatialCostFunction costFunction, Set<Point> points) {
		this.costFunction = costFunction;
		this.points = points;
	}
	
	public void setDiscretizer(Discretizer discretizer) {
		this.discretizer = discretizer;
	}

	@Override
	public void analyze(Graph g, Map<String, DescriptiveStatistics> statsMap) {
		if (outputDirectoryNotNull()) {
			try {
				SpatialGraph graph = (SpatialGraph) g;
				TObjectDoubleHashMap<SpatialVertex> xVals = module.values(graph.getVertices(), costFunction, points);
				/*
				 * degree-accessibility correlation
				 */
				TObjectDoubleHashMap yVals = Degree.getInstance().values(graph.getVertices());
				TDoubleDoubleHashMap correl = VertexPropertyCorrelation.mean(yVals, xVals, discretizer);
				StatsWriter.writeHistogram(correl, "A", "k", getOutputDirectory() + "k_A.mean.txt");
				
				TDoubleObjectHashMap<DescriptiveStatistics> statistics = VertexPropertyCorrelation.statistics(yVals, xVals, discretizer);
				StatsWriter.writeStatistics(statistics, "A", getOutputDirectory() + "k_A.stats.txt");
				/*
				 * transitivity-accessibility correlation
				 */
				yVals = Transitivity.getInstance().values(graph.getVertices());
				correl = VertexPropertyCorrelation.mean(yVals, xVals, discretizer);
				StatsWriter.writeHistogram(correl, "A", "c_local", getOutputDirectory() + "c_local_A.mean.txt");
				
				statistics = VertexPropertyCorrelation.statistics(yVals, xVals, discretizer);
				StatsWriter.writeStatistics(statistics, "A", getOutputDirectory() + "c_local_A.stats.txt");
				/*
				 * mean distance-accessibility correlation
				 */
				TObjectDoubleHashMap<SpatialVertex> dVals = Distance.getInstance().vertexMean(graph.getVertices());
				correl = VertexPropertyCorrelation.mean(dVals, xVals, discretizer);
				StatsWriter.writeHistogram(correl, "A", "d_mean", getOutputDirectory() + "d_mean_A.mean.txt");
				
				statistics = VertexPropertyCorrelation.statistics(dVals, xVals, discretizer);
				StatsWriter.writeStatistics(statistics, "A", getOutputDirectory() + "d_mean_A.stats.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
