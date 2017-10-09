/* *********************************************************************** *
 * project: org.matsim.*
 * SocialSampledVertexDecorator.java
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
package org.matsim.contrib.socnetgen.sna.snowball.social;

import com.vividsolutions.jts.geom.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialPerson;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialVertex;
import org.matsim.contrib.socnetgen.sna.snowball.SampledVertexDecorator;

import java.util.List;

/**
 * @author illenberger
 *
 */
public class SocialSampledVertexDecorator<V extends SocialVertex> extends SampledVertexDecorator<V> implements SocialVertex {

	protected SocialSampledVertexDecorator(V delegate) {
		super(delegate);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends SocialSampledEdgeDecorator<?>> getEdges() {
		return (List<? extends SocialSampledEdgeDecorator<?>>) super.getEdges();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends SocialSampledVertexDecorator<V>> getNeighbours() {
		return (List<? extends SocialSampledVertexDecorator<V>>) super.getNeighbours();
	}

	@Override
	public SocialPerson getPerson() {
		return getDelegate().getPerson();
	}

	/**
	 * @deprecated
	 */
	@Override
	public Coord getCoordinate() {
		return getDelegate().getCoordinate();
	}

	@Override
	public Point getPoint() {
		return getDelegate().getPoint();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("id=");
		builder.append(getDelegate().getPerson().getId().toString());
		builder.append(", sampled=");
		builder.append(String.valueOf(getIterationSampled()));
		builder.append(", detected=");
		builder.append(String.valueOf(getIterationDetected()));
		return builder.toString();
	}

}
