/* *********************************************************************** *
 * project: org.matsim.*
 * SnowballColorizer.java
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

import org.matsim.contrib.socnetgen.sna.graph.spatial.io.ColorUtils;
import org.matsim.contrib.socnetgen.sna.graph.spatial.io.Colorizable;
import org.matsim.contrib.socnetgen.sna.snowball.SampledVertex;

import java.awt.*;
import java.util.Collection;

/**
 * @author illenberger
 *
 */
public class SnowballColorizer implements Colorizable {

	private int minIteration = Integer.MAX_VALUE;
	
	private int maxIteration = Integer.MIN_VALUE;
	
	public SnowballColorizer(Collection<? extends SampledVertex> vertices) {
		for(SampledVertex v : vertices) {
			if(v.isSampled()) {
				/*
				 * offset iteration with 1 to distinguish from unsampled vertices
				 */
				int it = v.getIterationSampled() + 1;
				minIteration = Math.min(minIteration, it);
				maxIteration = Math.max(maxIteration, it);
			}
		}
		minIteration = 0;
		maxIteration = Math.max(maxIteration, 7);
		maxIteration++;
		
	}
	
	@Override
	public Color getColor(Object object) {
		double val = 0;
		if(((SampledVertex) object).isSampled())
			val = (((SampledVertex) object).getIterationSampled() + 1 - minIteration)/(double)(maxIteration - minIteration);
		else
			val = (((SampledVertex) object).getIterationDetected() + 2 - minIteration)/(double)(maxIteration - minIteration);
		return ColorUtils.getGRBColor(val);
	}

}
