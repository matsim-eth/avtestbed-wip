/* *********************************************************************** *
 * project: org.matsim.*
 * Accessibility.java
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

import com.vividsolutions.jts.geom.Point;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.apache.log4j.Logger;
import org.matsim.contrib.common.collections.CollectionUtils;
import org.matsim.contrib.common.util.ProgressLogger;
import org.matsim.contrib.socnetgen.sna.gis.SpatialCostFunction;
import org.matsim.contrib.socnetgen.sna.graph.Vertex;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialVertex;
import org.matsim.contrib.socnetgen.sna.util.MultiThreading;

import java.util.List;
import java.util.Set;

/**
 * @author illenberger
 *
 */
public class Accessibility extends AbstractSpatialProperty {

	private static final Logger logger = Logger.getLogger(Accessibility.class);
	
	private final SpatialCostFunction function;
	
	private int numThreads = MultiThreading.getNumAllowedThreads();
	
	public Accessibility(SpatialCostFunction function) {
		this.function = function;
	}
	
	@Override
	public TObjectDoubleHashMap<Vertex> values(Set<? extends Vertex> vertices) {
		logger.info("Calculating accessibility...");
		
		
		@SuppressWarnings("unchecked")
		Set<? extends SpatialVertex> spatialVertices = (Set<? extends SpatialVertex>) vertices;
		Set<Point> targets = getTargets(spatialVertices);
		
		ProgressLogger.init(vertices.size(), 1, 5);
		List<? extends SpatialVertex>[] segments = CollectionUtils.split(spatialVertices, numThreads);
		
		Calculator[] threads = new Calculator[numThreads];
		for(int i = 0; i < threads.length; i++) {
			threads[i] = new Calculator(segments[i], targets);
		}
		/*
		 * start threads
		 */
		for (Calculator thread : threads) {
			thread.start();
		}
		/*
		 * wait for threads
		 */
		for (Calculator thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ProgressLogger.terminate();
		/*
		 * merge results
		 */
		TObjectDoubleHashMap<Vertex> values = new TObjectDoubleHashMap<Vertex>(vertices.size());
		for (Calculator thread : threads) {
			TObjectDoubleIterator<Vertex> it = thread.values.iterator();
			for(int i = 0; i < thread.values.size(); i++) {
				it.advance();
				values.put(it.key(), it.value()/(double)targets.size());
			}
		}
		
		return values;
	}

	private class Calculator extends Thread {
		
		private List<? extends SpatialVertex> vertices;
		
		private TObjectDoubleHashMap<Vertex> values;
		
		private Set<Point> targets;
		
		public Calculator(List<? extends SpatialVertex> vertices, Set<Point> targets) {
			this.vertices = vertices;
			this.targets = targets;
			values = new TObjectDoubleHashMap<Vertex>(vertices.size());
		}
		
		public void run() {
			for(SpatialVertex vertex : vertices) {
				Point origin = vertex.getPoint();
				if(origin != null) {
					double sum = 0;
					for(Point target : targets) {
						if(origin != target) {
							sum += Math.exp(- function.costs(origin, target));
						}
					}
					
					values.put(vertex, sum);
					
					ProgressLogger.step();
				}
			}
		}
	}

}
