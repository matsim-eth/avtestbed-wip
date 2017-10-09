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

package playground.johannes.gsv.synPop.mid;

import playground.johannes.synpop.data.CommonKeys;
import playground.johannes.synpop.data.Episode;
import playground.johannes.synpop.data.Person;
import playground.johannes.synpop.data.PlainPerson;
import playground.johannes.synpop.processing.PersonTask;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author johannes
 * 
 */
public class JourneyPlans2PersonTask implements PersonTask {

	private Set<PlainPerson> newPersons = new HashSet<>();

	private final double periode = 90;
	
	public Set<PlainPerson> getPersons() {
		return newPersons;
	}
	
	@Override
	public void apply(Person person1) {
		PlainPerson person = (PlainPerson) person1;
		int counter = 0;
		double w = Double.parseDouble(person.getAttribute(CommonKeys.PERSON_WEIGHT));

		Set<Episode> journeyPlans = new HashSet<>();

		for (Episode plan : person.getEpisodes()) {
			if ("midjourneys".equalsIgnoreCase(plan.getAttribute("datasource"))) {
				PlainPerson newPerson = new PlainPerson(String.format("%s.%s", person.getId(), counter++));
				for (Entry<String, String> entry : person.getAttributes().entrySet()) {
					newPerson.setAttribute(entry.getKey(), entry.getValue());
				}

				newPerson.addEpisode(plan);

				double newW = w * 1 / periode;
				newPerson.setAttribute(CommonKeys.PERSON_WEIGHT, String.valueOf(newW));

				newPersons.add(newPerson);

				journeyPlans.add(plan);
			}
		}

		for (Episode plan : journeyPlans) {
			person.getEpisodes().remove(plan);
		}

//		/*
//		 * adjust the weight of the original person
//		 */
//		double newW = w * 1 / 365.0;
//		person.setAttribute(CommonKeys.PERSON_WEIGHT, String.valueOf(newW));
//		/*
//		 * add one person with an empty plan
//		 */
//		PlainPerson newPerson = new PlainPerson(String.format("%s.%s", person.getId(), counter++));
//		for (Entry<String, String> entry : person.getAttributes().entrySet()) {
//			newPerson.setAttribute(entry.getKey(), entry.getValue());
//		}
//		newPerson.addEpisode(new PlainEpisode());
//
//		newW = w * (365 - counter) / 365.0;
//		newPerson.setAttribute(CommonKeys.PERSON_WEIGHT, String.valueOf(newW));
//
//		newPersons.add(newPerson);
	}

}
