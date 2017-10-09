/* *********************************************************************** *
 * project: org.matsim.*
 * SampledEgo.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
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
package org.matsim.contrib.socnetgen.sna.graph.social;

import com.vividsolutions.jts.geom.Point;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialSparseVertex;

import java.util.List;

/**
 * @author illenberger
 *
 */
public class SocialSparseVertex extends SpatialSparseVertex implements SocialVertex {

	private SocialPerson person;
	
	protected SocialSparseVertex(SocialPerson person, Point point) {
		super(point);
		this.person = person;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<? extends SocialSparseEdge> getEdges() {
		return (List<? extends SocialSparseEdge>) super.getEdges();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<? extends SocialSparseVertex> getNeighbours() {
		return (List<? extends SocialSparseVertex>) super.getNeighbours();
	}

	@Override
	public SocialPerson getPerson() {
		return person;
	}
}
