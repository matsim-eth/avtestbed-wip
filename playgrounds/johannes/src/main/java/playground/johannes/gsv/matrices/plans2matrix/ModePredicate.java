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

package playground.johannes.gsv.matrices.plans2matrix;

import playground.johannes.synpop.data.Attributable;
import playground.johannes.synpop.data.CommonKeys;
import playground.johannes.synpop.data.PlainPerson;

/**
 * @author johannes
 *
 */
public class ModePredicate implements Predicate {

	private final String mode;
	
	public ModePredicate(String mode) {
		this.mode = mode;
	}
	
	@Override
	public boolean test(PlainPerson person, Attributable leg, Attributable prev, Attributable next) {
		return mode.equalsIgnoreCase(leg.getAttribute(CommonKeys.LEG_MODE));
	}

}
