/* *********************************************************************** *
 * project: org.matsim.*
 * DegreeNormConstantTask.java
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
package org.matsim.contrib.socnetgen.sna.graph.spatial.analysis;

import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.hash.TDoubleDoubleHashMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.matsim.contrib.common.stats.FixedSampleSizeDiscretizer;
import org.matsim.contrib.common.stats.StatsWriter;
import org.matsim.contrib.socnetgen.sna.gis.SpatialCostFunction;
import org.matsim.contrib.socnetgen.sna.graph.Graph;
import org.matsim.contrib.socnetgen.sna.graph.Vertex;
import org.matsim.contrib.socnetgen.sna.graph.analysis.Degree;
import org.matsim.contrib.socnetgen.sna.graph.analysis.ModuleAnalyzerTask;
import org.matsim.contrib.socnetgen.sna.graph.analysis.VertexPropertyCorrelation;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialGraph;

/**
 * @author illenberger
 *
 */
public class DegreeAccessibilityTask extends ModuleAnalyzerTask<Accessibility> {

	public DegreeAccessibilityTask(Accessibility module) {
		setModule(module);
	}
	
	public DegreeAccessibilityTask(SpatialCostFunction function) {
		setModule(new Accessibility(function));
	}
	
	@Override
	public void analyze(Graph g, Map<String, DescriptiveStatistics> statsMap) {
		SpatialGraph graph = (SpatialGraph) g;
		
		TObjectDoubleHashMap<Vertex> xVals = module.values(graph.getVertices());
		TObjectDoubleHashMap<Vertex> yVals = Degree.getInstance().values(graph.getVertices());
		
		double[] xArray = new double[xVals.size()];
		double[] yArray = new double[xVals.size()];
		TObjectDoubleIterator<Vertex> it = xVals.iterator();
		for(int i = 0; i < xVals.size(); i++) {
			it.advance();
			xArray[i] = it.value();
			yArray[i] = yVals.get(it.key());
		}
		double r = new PearsonsCorrelation().correlation(xArray, yArray);
		
		printStats(singleValueStats("r_ka", r, statsMap), "r_kA");
		
		if(outputDirectoryNotNull()) {
			
			
			TDoubleDoubleHashMap correl = VertexPropertyCorrelation.mean(yVals, xVals, FixedSampleSizeDiscretizer.create(xVals.values(), 20, 100));
//			TDoubleDoubleHashMap correl = VertexPropertyCorrelation.mean(yVals, xVals, new LinearDiscretizer(5.0));
			try {
				StatsWriter.writeHistogram(correl, "A", "k", getOutputDirectory() + "k_mean_A.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			it = xVals.iterator();
			for(int i = 0; i < xVals.size(); i++) {
				it.advance();
				double A = it.value();
				double k = yVals.get(it.key());
				
				yVals.put(it.key(), k/A);
			}
			
			correl = VertexPropertyCorrelation.mean(yVals, xVals, FixedSampleSizeDiscretizer.create(xVals.values(), 20, 100));

			try {
				StatsWriter.writeHistogram(correl, "A", "c_i", getOutputDirectory() + "c_i_A.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
