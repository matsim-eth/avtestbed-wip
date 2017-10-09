/* *********************************************************************** *
 * project: org.matsim.*
 * VisitorsAccessibilityTask.java
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
package playground.johannes.coopsim.analysis;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.TDoubleDoubleHashMap;
import gnu.trove.map.hash.TDoubleObjectHashMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.common.gis.CartesianDistanceCalculator;
import org.matsim.contrib.common.stats.Correlations;
import org.matsim.contrib.common.stats.Discretizer;
import org.matsim.contrib.common.stats.FixedSampleSizeDiscretizer;
import org.matsim.contrib.common.stats.StatsWriter;
import org.matsim.contrib.socnetgen.sna.gis.GravityCostFunction;
import org.matsim.contrib.socnetgen.sna.graph.Vertex;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialGraph;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialVertex;
import org.matsim.contrib.socnetgen.sna.graph.spatial.analysis.Accessibility;
import org.matsim.contrib.socnetgen.sna.graph.spatial.analysis.GridAccessibility;
import playground.johannes.coopsim.pysical.Trajectory;
import playground.johannes.coopsim.pysical.VisitorTracker;

import java.io.IOException;
import java.util.*;

/**
 * @author illenberger
 * 
 */
public class VisitorsAccessibilityTask extends TrajectoryAnalyzerTask {

	private final VisitorTracker tracker;

	private final SocialGraph graph;

	private final Map<Person, SocialVertex> mapping;

	public VisitorsAccessibilityTask(VisitorTracker tracker, SocialGraph graph) {
		this.tracker = tracker;
		this.graph = graph;

		mapping = new HashMap<Person, SocialVertex>(graph.getVertices().size());
		for (SocialVertex v : graph.getVertices()) {
			mapping.put(v.getPerson().getPerson(), v);
		}
	}

	@Override
	public void analyze(Set<Trajectory> trajectories, Map<String, DescriptiveStatistics> results) {
		Accessibility access = new GridAccessibility(new GravityCostFunction(1.4, 0,
				CartesianDistanceCalculator.getInstance()), 2000);
		TObjectDoubleHashMap<Vertex> values = access.values(graph.getVertices());

		Set<String> purposes = new HashSet<String>();
		for (Trajectory t : trajectories) {
			for (int i = 0; i < t.getElements().size(); i += 2) {
				purposes.add(((Activity) t.getElements().get(i)).getType());
			}
		}
		purposes.add(null);

		for (String purpose : purposes) {
			TDoubleArrayList accessVals = new TDoubleArrayList(trajectories.size());
			TDoubleArrayList visitorVals = new TDoubleArrayList(trajectories.size());
			for (Trajectory t : trajectories) {
				if (((Activity) t.getElements().get(2)).getType().equals(purpose)) {
					Person p = t.getPerson();
					SocialVertex v = mapping.get(p);
					List<Person> alters = new ArrayList<Person>(v.getNeighbours().size());
					for (SocialVertex neighbor : v.getNeighbours()) {
						alters.add(neighbor.getPerson().getPerson());
					}
					int visitors = tracker.metAlters(p, alters);

					accessVals.add(values.get(v));
					visitorVals.add(visitors);
				}
			}

			if(purpose == null)
				purpose = "all";
			
			if(accessVals.size() > 0) {
			Discretizer discretizer = FixedSampleSizeDiscretizer.create(accessVals.toArray(), 50, 50);
			TDoubleDoubleHashMap correl = Correlations.mean(accessVals.toArray(), visitorVals.toArray(),
					discretizer);
			TDoubleObjectHashMap<DescriptiveStatistics> stats = Correlations.statistics(accessVals.toArray(),
					visitorVals.toArray(), discretizer);
			try {
				StatsWriter.writeHistogram(correl, "A", "n", String.format("%1$s/visitors_A.%2$s.txt", getOutputDirectory(), purpose));
				StatsWriter.writeStatistics(stats, "access",
						String.format("%1$s/visitors_A.stats.%2$s.txt", getOutputDirectory(), purpose));
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
		}
	}

}
