/* *********************************************************************** *
 * project: org.matsim.*
 * SampledGraphProjMLReader.java
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
package org.matsim.contrib.socnetgen.sna.snowball.io;

import java.io.File;

import org.matsim.contrib.socnetgen.sna.graph.Edge;
import org.matsim.contrib.socnetgen.sna.graph.Graph;
import org.matsim.contrib.socnetgen.sna.graph.GraphUtils;
import org.matsim.contrib.socnetgen.sna.graph.Vertex;
import org.matsim.contrib.socnetgen.sna.graph.io.AbstractGraphMLReader;
import org.matsim.contrib.socnetgen.sna.snowball.SampledEdgeDecorator;
import org.matsim.contrib.socnetgen.sna.snowball.SampledGraphProjection;
import org.matsim.contrib.socnetgen.sna.snowball.SampledGraphProjectionBuilder;
import org.matsim.contrib.socnetgen.sna.snowball.SampledVertexDecorator;
import org.xml.sax.Attributes;


/**
 * @author illenberger
 *
 */
public class SampledGraphProjMLReader<G extends Graph, V extends Vertex, E extends Edge> extends AbstractGraphMLReader<SampledGraphProjection<G, V, E>, SampledVertexDecorator<V>, SampledEdgeDecorator<E>> {

	private AbstractGraphMLReader<G, V, E> delegateReader;
	
	private String parent;
	
	private G delegate;
	
	private SampledGraphProjectionBuilder<G, V, E> builder;
	
	public SampledGraphProjMLReader(AbstractGraphMLReader<G, V, E> delegateReader) {
		super();
		this.delegateReader = delegateReader;
	}
	
	public void setGraphProjectionBuilder(SampledGraphProjectionBuilder<G, V, E> builder) {
		this.builder = builder;
	}
	
	@Override
	public SampledGraphProjection<G, V, E> readGraph(String file) {
		parent = new File(file).getParent();
		return super.readGraph(file);
	}

	@Override
	protected SampledEdgeDecorator<E> addEdge(SampledVertexDecorator<V> v1,
			SampledVertexDecorator<V> v2, Attributes attrs) {
		V v_i = v1.getDelegate();
		V v_j = v2.getDelegate();
		E e = (E) GraphUtils.findEdge(v_i, v_j);
		if(e == null)
			throw new RuntimeException("Delegate edge not found!");
		
		return builder.addEdge(getGraph(), v1, v2, e);
	}
	
	@Override
	protected SampledVertexDecorator<V> addVertex(Attributes attrs) {
		int idx = Integer.parseInt(attrs.getValue(SampledGraphProjML.DELEGATE_IDX_ATTR));
		V vDelegate = delegateReader.getVertexIndices().get(idx);
		if(vDelegate == null)
			throw new RuntimeException("Vertex delegate must not be null! idx=" + idx);
		
		SampledVertexDecorator<V> vertex = builder.addVertex(getGraph(), vDelegate);
		SampledGraphML.applyDetectedState(vertex, attrs);
		SampledGraphML.applySampledState(vertex, attrs);
		return vertex;
	}

	@Override
	protected SampledGraphProjection<G, V, E> newGraph(Attributes attrs) {
		String filename = attrs.getValue(SampledGraphProjML.DELEGATE_FILE);
		if(filename == null)
			throw new RuntimeException("Delegate file not specified!");
		
		delegate = delegateReader.readGraph(String.format("%1$s/%2$s", parent, filename));
		if(builder == null)
			builder = new SampledGraphProjectionBuilder<G, V, E>();
		
		return builder.createGraph(delegate);
	}

}
