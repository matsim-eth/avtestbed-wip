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

import org.matsim.contrib.common.collections.Composite;
import playground.johannes.synpop.data.Person;

import java.util.Collection;

/**
 * @author johannes
 *
 */
public class SamplerListenerComposite extends Composite<SamplerListener> implements SamplerListener {

	@Override
	public void afterStep(Collection<? extends Person> population, Collection<? extends Person> mutations, boolean accpeted) {
		for(SamplerListener listener : components) {
			listener.afterStep(population, mutations, accpeted);
		}

	}
}
