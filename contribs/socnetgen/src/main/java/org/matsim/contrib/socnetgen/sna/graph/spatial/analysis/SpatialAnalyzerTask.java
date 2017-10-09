/* *********************************************************************** *
 * project: org.matsim.*
 * StandardAnalyzerTask.java
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

import org.matsim.contrib.socnetgen.sna.graph.analysis.AnalyzerTask;
import org.matsim.contrib.socnetgen.sna.graph.analysis.AnalyzerTaskComposite;
import org.matsim.contrib.socnetgen.sna.graph.analysis.GraphAnalyzer;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialGraph;
import org.matsim.contrib.socnetgen.sna.graph.spatial.io.SpatialGraphMLReader;

import java.io.IOException;

/**
 * @author illenberger
 *
 */
public class SpatialAnalyzerTask extends AnalyzerTaskComposite {

	public SpatialAnalyzerTask() {
		addTask(new EdgeLengthTask());
		addTask(new EdgeLengthSumTask());
		addTask(new EdgeLengthMeanTask());
		addTask(new EdgeLengthMedianTask());
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SpatialGraphMLReader reader = new SpatialGraphMLReader();
		SpatialGraph graph = reader.readGraph(args[0]);
		
		String output = null;
		if(args.length > 1) {
			output = args[1];
		}
		
		AnalyzerTask task = new SpatialAnalyzerTask();
		if(output != null)
			task.setOutputDirectoy(output);
		
		GraphAnalyzer.analyze(graph, task, output);
	}

}
