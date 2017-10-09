/* *********************************************************************** *
 * project: org.matsim.*
 * SocialSparseGraphMLWriter.java
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
package playground.johannes.studies.sbsurvey.io;

import org.matsim.contrib.socnetgen.sna.graph.Edge;
import org.matsim.contrib.socnetgen.sna.graph.Vertex;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialSparseEdge;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialSparseVertex;
import org.matsim.contrib.socnetgen.sna.graph.social.io.SocialGraphMLWriter;
import org.matsim.core.utils.collections.Tuple;

import java.util.List;

/**
 * @author illenberger
 * 
 */
public class SocialSparseGraphMLWriter extends SocialGraphMLWriter {

	@Override
	protected List<Tuple<String, String>> getVertexAttributes(Vertex v) {
		List<Tuple<String, String>> attrs = super.getVertexAttributes(v);
		
		String val = ((SocialSparseVertex) v).getPerson().getCitizenship();
		if (val != null) {
			Tuple<String, String> attr = new Tuple<String, String>(SocialSparseGraphML.CITIZENSHIP_ATTR, val);
			attrs.add(attr);
		}
		
		val = ((SocialSparseVertex) v).getPerson().getEducation();
		if (val != null) {
			Tuple<String, String> attr = new Tuple<String, String>(SocialSparseGraphML.EDUCATION_ATTR, val);
			attrs.add(attr);
		}
		
		val = String.valueOf(((SocialSparseVertex) v).getPerson().getIncome());
		if (val != null) {
			Tuple<String, String> attr = new Tuple<String, String>(SocialSparseGraphML.INCOME_ATTR, val);
			attrs.add(attr);
		}
		
		val = ((SocialSparseVertex) v).getPerson().getCiviStatus();
		if (val != null) {
			Tuple<String, String> attr = new Tuple<String, String>(SocialSparseGraphML.CIVI_STATUS_ATTR, val);
			attrs.add(attr);
		}
		
		return attrs;
	}

	@Override
	protected List<Tuple<String, String>> getEdgeAttributes(Edge e) {
		List<Tuple<String, String>> attrs = super.getEdgeAttributes(e);
		Tuple<String, String> tuple = new Tuple<String, String>(SocialSparseGraphML.FREQUENCY_ATTR, String
				.valueOf(((SocialSparseEdge) e).getFrequency()));
		attrs.add(tuple);
		
		String val = ((SocialSparseEdge) e).getType();
		if(val != null) {
			tuple = new Tuple<String, String>(SocialSparseGraphML.EDGE_TYPE_ATTR, ((SocialSparseEdge) e).getType());
			attrs.add(tuple);
		}
		return attrs;
	}

}
