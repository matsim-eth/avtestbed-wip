/* *********************************************************************** *
 * project: org.matsim.*
 * TimeTagger.java
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
package playground.johannes.studies.sbsurvey.run;

import org.matsim.contrib.socnetgen.sna.graph.Vertex;
import org.matsim.contrib.socnetgen.sna.snowball.SampledEdgeDecorator;
import org.matsim.contrib.socnetgen.sna.snowball.SampledVertexDecorator;
import org.matsim.contrib.socnetgen.sna.snowball.sim.Sampler;
import org.matsim.contrib.socnetgen.sna.snowball.sim.SamplerListener;

import java.util.HashMap;
import java.util.Map;


/**
 * @author illenberger
 *
 */
public class TimeTagger implements SamplerListener {
	
	private Map<Object, String> timeTags = new HashMap<Object, String>();

	private int timeCode = 1;
	
	public Map<Object, String> getTimeTags() {
		return timeTags;
	}
	
	@Override
	public boolean afterSampling(Sampler<?, ?, ?> sampler, SampledVertexDecorator<?> vertex) {
		for(SampledEdgeDecorator<?> edge : vertex.getEdges()) {
			String time = String.valueOf(timeCode);
			
			if(!timeTags.containsKey(edge.getDelegate()))
				timeTags.put(edge.getDelegate(), time);
			
			Vertex v = edge.getOpposite(vertex).getDelegate();
			if(!timeTags.containsKey(v))
				timeTags.put(v, time);
			
			timeCode++;
		}
		return true;
	}

	@Override
	public boolean beforeSampling(Sampler<?, ?, ?> sampler, SampledVertexDecorator<?> vertex) {
		return true;
	}

	@Override
	public void endSampling(Sampler<?, ?, ?> sampler) {
	}

}
