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

package playground.johannes.synpop.source.invermo.processing;

import playground.johannes.synpop.data.Attributable;
import playground.johannes.synpop.data.Episode;
import playground.johannes.synpop.data.Person;
import playground.johannes.synpop.processing.PersonTask;
import playground.johannes.synpop.source.invermo.InvermoKeys;

/**
 * @author johannes
 *
 */
public class ReplaceLocationAliasTask implements PersonTask {

	@Override
	public void apply(Person person) {
		for(Episode plan : person.getEpisodes()) {
			for(Attributable act : plan.getActivities()) {
				String desc = act.getAttribute(InvermoKeys.LOCATION);
				if("home".equals(desc)) {
					act.setAttribute(InvermoKeys.LOCATION, person.getAttribute("homeLoc"));
				} else if("work".equals(desc)) {
					act.setAttribute(InvermoKeys.LOCATION, person.getAttribute("workLoc"));
				}
			}
		}

	}

}
