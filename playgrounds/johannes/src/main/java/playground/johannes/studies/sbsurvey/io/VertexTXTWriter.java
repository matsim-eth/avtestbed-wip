/* *********************************************************************** *
 * project: org.matsim.*
 * VertexTXTWriter.java
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
package playground.johannes.studies.sbsurvey.io;

import org.matsim.contrib.socnetgen.sna.graph.social.SocialSparseEdge;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialSparseGraph;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialSparseVertex;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialVertex;
import org.matsim.contrib.socnetgen.sna.snowball.analysis.SnowballPartitions;
import org.matsim.contrib.socnetgen.sna.snowball.social.SocialSampledGraphProjection;
import org.matsim.core.population.PersonUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * @author illenberger
 *
 */
public class VertexTXTWriter {

	public void write(Set<? extends SocialVertex> vertices, String filename) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		
		writer.write("id\tgender\tdegree");
		writer.newLine();
		
		for(SocialVertex vertex : vertices) {
			String id = vertex.getPerson().getId().toString();
			String gender = PersonUtils.getSex(vertex.getPerson().getPerson());
			int k = vertex.getNeighbours().size();
			
			if(gender != null) {
				writer.write(id);
				writer.write("\t");
				writer.write(gender);
				writer.write("\t");
				writer.write(String.valueOf(k));
				writer.newLine();
			}
		}
		
		writer.close();
	}
	
	public static void main(String args[]) throws IOException {
		SocialSampledGraphProjection<SocialSparseGraph,SocialSparseVertex,SocialSparseEdge> graph = GraphReaderFacade.read("/Users/jillenberger/Work/socialnets/data/ivt2009/01-2011/graph/graph.graphml");
		
		Set vertices = SnowballPartitions.createSampledPartition(graph.getVertices());
		
		VertexTXTWriter writer = new VertexTXTWriter();
		writer.write(vertices, "/Users/jillenberger/Work/socialnets/data/ivt2009/01-2011/graph/egos.txt");
	}
}
