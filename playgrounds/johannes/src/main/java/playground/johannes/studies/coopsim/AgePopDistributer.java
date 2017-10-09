/* *********************************************************************** *
 * project: org.matsim.*
 * AgePopDistributer.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
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
package playground.johannes.studies.coopsim;

import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.apache.commons.math.stat.StatUtils;
import org.matsim.contrib.common.gis.CartesianDistanceCalculator;
import org.matsim.contrib.socnetgen.sna.gis.GravityCostFunction;
import org.matsim.contrib.socnetgen.sna.graph.Vertex;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialSparseGraph;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialVertex;
import org.matsim.contrib.socnetgen.sna.graph.social.analysis.Age;
import org.matsim.contrib.socnetgen.sna.graph.spatial.analysis.Accessibility;
import org.matsim.core.population.PersonUtils;
import playground.johannes.studies.sbsurvey.io.SocialSparseGraphMLReader;
import playground.johannes.studies.sbsurvey.io.SocialSparseGraphMLWriter;

import java.io.IOException;

/**
 * @author illenberger
 *
 */
public class AgePopDistributer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SocialSparseGraphMLReader reader = new SocialSparseGraphMLReader();
		SocialSparseGraph graph = reader.readGraph("/Users/jillenberger/vsp/work/coopsim/data/graph.graphml");
		
		Accessibility access = new Accessibility(new GravityCostFunction(1.4, 0, CartesianDistanceCalculator.getInstance()));
		TObjectDoubleHashMap<Vertex> accessVals = access.values(graph.getVertices());
		
		Age age = new Age();
		TObjectDoubleHashMap<Vertex> ageVals = age.values(graph.getVertices());
		
		double[] accessArray = accessVals.values();
		double minAccess = StatUtils.min(accessArray);
		double maxAccess = StatUtils.max(accessArray);
		
		double[] ageArray = ageVals.values();
		double minAge = StatUtils.min(ageArray);
		double maxAge = StatUtils.max(ageArray);
		
		double A = - (maxAge - minAge) / (maxAccess - minAccess);
		double b = maxAge - A * minAccess;
		
		for(SocialVertex v : graph.getVertices()) {
			double personAge = A * accessVals.get(v) + b;
			PersonUtils.setAge(v.getPerson().getPerson(), (int) personAge);
		}
		
		SocialSparseGraphMLWriter writer = new SocialSparseGraphMLWriter();
		writer.write(graph, "/Users/jillenberger/vsp/work/coopsim/data/graph.synth-age.graphml");
	}

}
