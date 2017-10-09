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

package playground.johannes.studies.matrix2014.source.mid2008;

import org.apache.log4j.Logger;
import org.matsim.contrib.common.stats.LinearDiscretizer;
import playground.johannes.gsv.synPop.mid.Route2GeoDistance;
import playground.johannes.studies.matrix2014.analysis.NumericLegAnalyzer;
import playground.johannes.studies.matrix2014.sim.Simulator;
import playground.johannes.studies.matrix2014.sim.ValidatePersonWeight;
import playground.johannes.synpop.analysis.*;
import playground.johannes.synpop.data.CommonKeys;
import playground.johannes.synpop.data.CommonValues;
import playground.johannes.synpop.data.Person;
import playground.johannes.synpop.data.PlainFactory;
import playground.johannes.synpop.data.io.PopulationIO;
import playground.johannes.synpop.processing.TaskRunner;
import playground.johannes.synpop.processing.ValidateMissingAttribute;

import java.io.IOException;
import java.util.Set;

/**
 * @author johannes
 *
 */
public class Analyzer {

	private static final Logger logger = Logger.getLogger(Analyzer.class);
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	
		String output = "/home/johannes/gsv/matrix2014/popgen/mid-fusion";


		String personFile = "/home/johannes/gsv/matrix2014/popgen/pop/mid2008.merged.xml";
//		String personFile = "/home/johannes/gsv/germany-scenario/mid2008/pop/mid2008.midjourneys.validated.xml";
//		String personFile = "/home/johannes/gsv/germany-scenario/mid2008/pop/mid2008.midtrips.validated.xml";
		
		Set<? extends Person> persons = PopulationIO.loadFromXML(personFile, new PlainFactory());

//		logger.info("Cloning persons...");
//		Random random = new XORShiftRandom();
//		persons = PersonCloner.weightedClones((Collection<PlainPerson>) persons, 1000000, random);
//		logger.info(String.format("Generated %s persons.", persons.size()));

		TaskRunner.validatePersons(new ValidateMissingAttribute(CommonKeys.PERSON_WEIGHT), persons);
		TaskRunner.validatePersons(new ValidatePersonWeight(), persons);
		TaskRunner.run(new Route2GeoDistance(new Simulator.Route2GeoDistFunction()), persons);

		FileIOContext ioContext = new FileIOContext(output);
		Predicate predicate = new ModePredicate(CommonValues.LEG_MODE_CAR);
		HistogramWriter hWriter = new HistogramWriter(ioContext, new PassThroughDiscretizerBuilder(new
				LinearDiscretizer(50000), "linear"));

		AnalyzerTask task = NumericLegAnalyzer.create(CommonKeys.LEG_GEO_DISTANCE, true, predicate, "car", hWriter);
		AnalyzerTaskRunner.run(persons, task, ioContext);
	}

}
