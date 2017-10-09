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

package playground.johannes.synpop.source.invermo.generator;

import playground.johannes.synpop.data.Attributable;
import playground.johannes.synpop.source.invermo.InvermoKeys;

/**
 * @author johannes
 *
 */
public class LegDestinationLocHandler implements LegAttributeHandler {

	@Override
	public void handle(Attributable leg, String key, String value) {
		if(key.endsWith("ziel0")) {
			if(value.equals("1")) {
				leg.setAttribute(InvermoKeys.DESTINATION_LOCATION, "home");
			} else if(value.equals("2")){
				leg.setAttribute(InvermoKeys.DESTINATION_LOCATION, "work");
			}
		} else if(key.endsWith("zielland") || key.endsWith("zieldort") || key.endsWith("ziela3")) {
			String desc = leg.getAttribute(InvermoKeys.DESTINATION_LOCATION);
			if(desc == null) {
				desc = value;
			} else {
				desc = desc + ", " + value;
			}
			leg.setAttribute(InvermoKeys.DESTINATION_LOCATION, desc);
		}

	}

}
