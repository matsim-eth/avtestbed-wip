/* *********************************************************************** *
 * project: org.matsim.*
 * RemoveIsolatesTask.java
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
package playground.johannes.studies.sbsurvey.run;

import org.apache.log4j.Logger;
import org.matsim.contrib.socnetgen.sna.graph.SparseGraph;
import org.matsim.contrib.socnetgen.sna.graph.SparseGraphBuilder;
import org.matsim.contrib.socnetgen.sna.graph.SparseVertex;
import org.matsim.contrib.socnetgen.sna.graph.analysis.GraphFilter;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialGraph;
import org.matsim.contrib.socnetgen.sna.graph.spatial.io.SpatialGraphMLReader;
import org.matsim.contrib.socnetgen.sna.graph.spatial.io.SpatialGraphMLWriter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;



/**
 * @author illenberger
 *
 */
public class RemoveIsolatesFilter implements GraphFilter<SparseGraph> {

	private static final Logger logger = Logger.getLogger(RemoveIsolatesFilter.class);
	
	@Override
	public SparseGraph apply(SparseGraph graph) {
		Set<SparseVertex> toRemove = new HashSet<SparseVertex>();
		for(SparseVertex v : graph.getVertices()) {
			if(v.getEdges().isEmpty())
				toRemove.add(v);
		}
		
		SparseGraphBuilder builder = new SparseGraphBuilder();
		for(SparseVertex v : toRemove)
			if(!builder.removeVertex(graph, v))
				throw new RuntimeException("Removing vertex failed.");

		logger.info(String.format("Removed %1$s isolated vertices.", toRemove.size()));
		
		return graph;
	}
	
	public static void main(String args[]) throws IOException {
		RemoveIsolatesFilter task = new RemoveIsolatesFilter();
		SpatialGraphMLReader reader = new SpatialGraphMLReader();
		SpatialGraph graph = reader.readGraph(args[0]);
		graph = (SpatialGraph) task.apply((SparseGraph) graph);
		SpatialGraphMLWriter writer = new SpatialGraphMLWriter();
		writer.write(graph, args[1]);
	}

}
