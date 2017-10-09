/* *********************************************************************** *
 * project: org.matsim.*
 * GenderAccessibilityTask.java
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
package org.matsim.contrib.socnetgen.sna.graph.social.analysis;

import gnu.trove.map.hash.TDoubleDoubleHashMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.matsim.contrib.common.stats.FixedSampleSizeDiscretizer;
import org.matsim.contrib.common.stats.StatsWriter;
import org.matsim.contrib.socnetgen.sna.graph.Graph;
import org.matsim.contrib.socnetgen.sna.graph.Vertex;
import org.matsim.contrib.socnetgen.sna.graph.analysis.ModuleAnalyzerTask;
import org.matsim.contrib.socnetgen.sna.graph.analysis.VertexPropertyCorrelation;
import org.matsim.contrib.socnetgen.sna.graph.spatial.analysis.Accessibility;

/**
 * @author illenberger
 *
 */
public class GenderAccessibilityTask extends ModuleAnalyzerTask<Accessibility> {

	private TObjectDoubleHashMap<Vertex> accessValues;
	
	public GenderAccessibilityTask(TObjectDoubleHashMap<Vertex> accessValues) {
		this.accessValues = accessValues;
	}
	
	public GenderAccessibilityTask(Accessibility module) {
		this.setModule(module);
	}
	
	@Override
	public void analyze(Graph graph, Map<String, DescriptiveStatistics> statsMap) {
		if(outputDirectoryNotNull()) {
			
			TObjectDoubleHashMap<Vertex> xVals = new TObjectDoubleHashMap<Vertex>(graph.getVertices().size());
			if(module == null)
				xVals = accessValues;
			else
				xVals = module.values(graph.getVertices());
			
				
			
			TObjectDoubleHashMap<Vertex> yVals = GenderNumeric.getInstance().values(graph.getVertices());
			TDoubleDoubleHashMap correl = VertexPropertyCorrelation.mean(yVals, xVals,
					FixedSampleSizeDiscretizer.create(xVals.values(), 50, 50));

			try {
				StatsWriter.writeHistogram(correl, "A", "gender", getOutputDirectory() + "gender_mean_A.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
