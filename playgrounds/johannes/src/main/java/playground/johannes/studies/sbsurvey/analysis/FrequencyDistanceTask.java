/* *********************************************************************** *
 * project: org.matsim.*
 * FrequencyDistanceTask.java
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
package playground.johannes.studies.sbsurvey.analysis;

import com.vividsolutions.jts.geom.Point;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.TDoubleDoubleHashMap;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.matsim.contrib.common.gis.CartesianDistanceCalculator;
import org.matsim.contrib.common.gis.DistanceCalculator;
import org.matsim.contrib.common.stats.*;
import org.matsim.contrib.socnetgen.sna.graph.Graph;
import org.matsim.contrib.socnetgen.sna.graph.analysis.AnalyzerTask;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialEdge;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialGraph;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialEdge;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialGraph;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialVertex;
import org.matsim.contrib.socnetgen.sna.math.Distribution;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author illenberger
 *
 */
public class FrequencyDistanceTask extends AnalyzerTask {

	private Set<Point> choiceSet;
	
	public FrequencyDistanceTask(Set<Point> choiceSet) {
		this.choiceSet = choiceSet;
	}
	
	/* (non-Javadoc)
	 * @see org.matsim.contrib.sna.graph.analysis.AnalyzerTask#analyze(org.matsim.contrib.sna.graph.Graph, java.util.Map)
	 */
	@Override
	public void analyze(Graph graph, Map<String, DescriptiveStatistics> stats) {
		try {
			Distribution wDistr = new Distribution();
			
		SocialGraph g = (SocialGraph) graph;
		TDoubleArrayList values1 = new TDoubleArrayList();
		TDoubleArrayList values2 = new TDoubleArrayList();
		for(SocialEdge edge : g.getEdges()) {
			double freq = edge.getFrequency();
			double d =edge.length();
			if(!Double.isNaN(d) && d > 0 && freq > 0) {
				values1.add(freq);
				values2.add(d);
				wDistr.add(d, freq);
			}
		}
		
		TDoubleDoubleHashMap map = Correlations.mean(values1.toArray(), values2.toArray(), new LogDiscretizer(1.12, 52));
		
			Correlations.writeToFile(map, getOutputDirectory() + "d_freq.txt", "frequency", "distance");
//			Correlations.writeToFile(map, getOutputDirectory() + "cost_freq.txt", "frequency", "cost");
			
//			map = Correlations.correlationMean(values2.toArray(), values1.toArray(), new LinearDiscretizer(1000));
			map = Correlations.mean(values2.toArray(), values1.toArray(), FixedSampleSizeDiscretizer.create(values2.toArray(), 200));
//			map = Correlations.correlationMean(values2.toArray(), values1.toArray(), new LogDiscretizer(2, 1000));
			Correlations.writeToFile(map, getOutputDirectory() + "freq_d.txt", "distance", "frequency");
			
//			Distribution.writeHistogram(wDistr.normalizedDistribution(wDistr.absoluteDistributionFixed(100)), getOutputDirectory()+"/d_freqWeighted.txt");
			Distribution.writeHistogram(wDistr.normalizedDistribution(wDistr.absoluteDistribution(1000)), getOutputDirectory()+"/d_freqWeighted.txt");
			
			/*
			 * accept of frequence_d
			 * 
			 */
			DistanceCalculator distanceCalculator = new CartesianDistanceCalculator();
			Discretizer discretizer = new LinearDiscretizer(1000);
			Distribution distribution = new Distribution();
			Set<SpatialEdge> touched = new HashSet<SpatialEdge>();
			SpatialGraph sGraph = (SpatialGraph) graph;
				for (SpatialVertex vertex : sGraph.getVertices()) {
				Point p1 = vertex.getPoint();

				TDoubleDoubleHashMap n_d = new TDoubleDoubleHashMap();
				for (Point p2 : choiceSet) {
					if (p1 != null && p2 != null) {
						double d = distanceCalculator.distance(p1, p2);
						n_d.adjustOrPutValue(discretizer.discretize(d), 1, 1);
					}
				}

				for (int i = 0; i < vertex.getEdges().size(); i++) {
					SpatialEdge e = vertex.getEdges().get(i);
					if (touched.add(e) ) {
						SpatialVertex neighbor = e.getOpposite(vertex);

						if (p1 != null && neighbor.getPoint() != null) {
							double d = distanceCalculator.distance(p1, neighbor.getPoint());
							double f = ((SocialEdge)e).getFrequency();
							double n = n_d.get(discretizer.discretize(d));
							if (n > 0) {
								for(int k = 0; k < f; k++)
									distribution.add(d, 1 / n);
							}
						}
					}
				}
			}
				
				Distribution.writeHistogram(distribution.normalizedDistribution(1000), getOutputDirectory()+"/p_travel.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
