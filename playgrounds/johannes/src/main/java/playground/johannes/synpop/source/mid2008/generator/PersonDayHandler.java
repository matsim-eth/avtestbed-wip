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

package playground.johannes.synpop.source.mid2008.generator;

import playground.johannes.synpop.data.CommonKeys;
import playground.johannes.synpop.data.CommonValues;
import playground.johannes.synpop.data.Person;

import java.util.Map;

/**
 * @author johannes
 *
 */
public class PersonDayHandler implements PersonAttributeHandler {

	@Override
	public void handle(Person person, Map<String, String> attributes) {
		String day = attributes.get(VariableNames.SURVEY_DAY);

		if(day.equalsIgnoreCase("1")) {
			person.setAttribute(CommonKeys.DAY, CommonValues.MONDAY);
		} else if(day.equalsIgnoreCase("2")) {
			person.setAttribute(CommonKeys.DAY, CommonValues.TUESDAY);
		} else if(day.equalsIgnoreCase("3")) {
			person.setAttribute(CommonKeys.DAY, CommonValues.WEDNESDAY);
		} else if(day.equalsIgnoreCase("4")) {
			person.setAttribute(CommonKeys.DAY, CommonValues.THURSDAY);
		} else if(day.equalsIgnoreCase("5")) {
			person.setAttribute(CommonKeys.DAY, CommonValues.FRIDAY);
		} else if(day.equalsIgnoreCase("6")) {
			person.setAttribute(CommonKeys.DAY, CommonValues.SATURDAY);
		} else if(day.equalsIgnoreCase("7")) {
			person.setAttribute(CommonKeys.DAY, CommonValues.SUNDAY);
		}
	}

}
