/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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

package playground.johannes.gsv.synPop.sim3;

import org.matsim.contrib.common.util.XORShiftRandom;
import playground.johannes.gsv.synPop.ProxyPlanTaskFactory;
import playground.johannes.synpop.gis.DataPool;
import playground.johannes.synpop.processing.EpisodeTask;

import java.util.Random;

/**
 * @author johannes
 *
 */
public class InitHomeBasedActLocsFactory implements ProxyPlanTaskFactory {

	private final Random random;
	
	private final DataPool pool;
	
	public InitHomeBasedActLocsFactory(DataPool pool, Random random) {
		this.pool = pool;
		this.random = random;
	}
	
	@Override
	public EpisodeTask getInstance() {
		return new InitHomeBasedActLocations(pool, new XORShiftRandom(random.nextLong()));
	}

}
