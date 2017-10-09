/* *********************************************************************** *
 * project: org.matsim.*
 * NonInnovativeStrategyFactory.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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
package org.matsim.contrib.socnetsim.framework.replanning;

import com.google.inject.Provider;


/**
 * @author thibautd
 */
public abstract class NonInnovativeStrategyFactory implements Provider<GroupPlanStrategy>, GroupLevelPlanSelectorFactory {

	@Override
	public GroupPlanStrategy get() {
		return new GroupPlanStrategy( createSelector() );
	}
}

