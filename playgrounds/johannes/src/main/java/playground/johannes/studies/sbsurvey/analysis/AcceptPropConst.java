/* *********************************************************************** *
 * project: org.matsim.*
 * AcceptPropConst.java
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
package playground.johannes.studies.sbsurvey.analysis;

import com.vividsolutions.jts.geom.Point;
import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.map.hash.TDoubleDoubleHashMap;
import gnu.trove.map.hash.TDoubleIntHashMap;
import gnu.trove.map.hash.TDoubleObjectHashMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.matsim.contrib.common.gis.CartesianDistanceCalculator;
import org.matsim.contrib.common.gis.DistanceCalculator;
import org.matsim.contrib.common.stats.Discretizer;
import org.matsim.contrib.common.stats.FixedSampleSizeDiscretizer;
import org.matsim.contrib.common.stats.Histogram;
import org.matsim.contrib.socnetgen.sna.graph.Vertex;
import org.matsim.contrib.socnetgen.sna.graph.analysis.AbstractVertexProperty;
import org.matsim.contrib.socnetgen.sna.graph.analysis.AttributePartition;
import org.matsim.contrib.socnetgen.sna.graph.spatial.SpatialVertex;
import org.matsim.contrib.socnetgen.sna.graph.spatial.analysis.Distance;

import java.util.Set;

/**
 * @author illenberger
 *
 */
public class AcceptPropConst extends AbstractVertexProperty {

	private static final Logger logger = Logger.getLogger(AcceptPropConst.class);
	
	private Set<Point> destinations;
	
	private final double gamma = -1.6;
	
	private TObjectDoubleHashMap<Vertex> partitionAttributes;
	
	public void setDestinations(Set<Point> destinations) {
		this.destinations = destinations;
	}
	
	public void setPartitionAttributes(TObjectDoubleHashMap<Vertex> partitionAttributes) {
		this.partitionAttributes = partitionAttributes;
	}
	
	@Override
	public TObjectDoubleHashMap<Vertex> values(Set<? extends Vertex> vertices) {
		TObjectDoubleHashMap<Vertex> c_i = new TObjectDoubleHashMap<Vertex>();
		
		logger.info("Creating partitions...");
		
		AttributePartition partitioner = new AttributePartition(FixedSampleSizeDiscretizer.create(partitionAttributes.values(), 20, 100));
		TDoubleObjectHashMap<Set<Vertex>> partitions = partitioner.partition(partitionAttributes);
		logger.info(String.format("Created %1$s partitions.", partitions.size()));
		
		DistanceCalculator distanceCalculator = new CartesianDistanceCalculator();
		
		logger.info("Calculating prop const...");
		TDoubleObjectIterator<?> it = partitions.iterator();
		for(int i = 0; i < partitions.size(); i++) {
			
			it.advance();
			Set<? extends SpatialVertex> partition = (Set<? extends SpatialVertex>) it.value();
			
			DescriptiveStatistics stats = Distance.getInstance().statistics(partition);
			Discretizer discretizer = FixedSampleSizeDiscretizer.create(stats.getValues(), 20, 100);
			TDoubleDoubleHashMap m_d = Histogram.createHistogram(stats, discretizer, true);
			/*
			 * count number of destinations at d
			 */
			TDoubleIntHashMap M_d = new TDoubleIntHashMap();
			for(SpatialVertex vertex : partition) {
				Point p1 = vertex.getPoint();
				if(p1 != null) {
					for(Point p2 : destinations) {
						if(p2 != null) {
							double d = distanceCalculator.distance(p1, p2);
							d = discretizer.discretize(d);
							M_d.adjustOrPutValue(d, 1, 1);
						}
					}
				}
			}
			/*
			 * 
			 */
			double c_sum = 0;
			int cnt = 0;
			TDoubleDoubleIterator mdIt = m_d.iterator();
			for(int k = 0; k < m_d.size(); k++) {
				mdIt.advance();
				double d = it.key();
				d = Math.max(d, 1.0);
				int M = M_d.get(discretizer.discretize(d));
				if(M > 0) {
					c_sum += mdIt.value() / (Math.pow(d, gamma) * M);
					
					System.err.println(String.valueOf(mdIt.value() / (Math.pow(d, gamma) * M)));
					
					cnt++;
				}
			}
			double c_mean = c_sum/(double)cnt;
			
			System.out.println(it.key() + "\t" + c_mean);
			/*
			 * 
			 */
			for(SpatialVertex vertex : partition) {
				c_i.put(vertex, c_mean);
			}
		}
		
		return c_i;
	}

}
