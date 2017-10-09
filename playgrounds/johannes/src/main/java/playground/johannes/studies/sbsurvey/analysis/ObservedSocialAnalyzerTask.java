/* *********************************************************************** *
 * project: org.matsim.*
 * ObservedSocialAnalyzerTask.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
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
package playground.johannes.studies.sbsurvey.analysis;

import org.matsim.contrib.socnetgen.sna.gis.GravityCostFunction;
import org.matsim.contrib.socnetgen.sna.gis.SpatialCostFunction;
import org.matsim.contrib.socnetgen.sna.graph.social.analysis.*;
import org.matsim.contrib.socnetgen.sna.graph.spatial.analysis.Accessibility;
import org.matsim.contrib.socnetgen.sna.snowball.analysis.ObservedDegree;
import org.matsim.contrib.socnetgen.sna.snowball.social.analysis.ObservedAge;
import org.matsim.contrib.socnetgen.sna.snowball.social.analysis.ObservedGender;

/**
 * @author illenberger
 */
public class ObservedSocialAnalyzerTask extends SocialAnalyzerTask {

    public ObservedSocialAnalyzerTask() {
        addTask(new AgeTask(new ObservedAge()));
        addTask(new GenderTask(new ObservedGender()));
        addTask(new EducationTask(new ObservedEducationCategorized()));
        addTask(new DegreeEducationTask(ObservedDegree.getInstance(), EducationCategorized.getInstance()));

        addTask(new DegreeAgeTask(ObservedDegree.getInstance()));
        addTask(new DegreeGenderTask(ObservedDegree.getInstance()));
        addTask(new AgeCorrelationTask(new ObservedAge()));
        addTask(new GenderResponseRateTask());
        addTask(new EducationResponseRateTask());


        addTask(new CentralityLingAttTask<ObservedEducationCategorized>(new ObservedEducationCategorized()));
        addTask(new GenderEductionTask());
        addTask(new TrianglesEduTask());
        SpatialCostFunction function = new GravityCostFunction(1.2, 0);
        Accessibility access = new Accessibility(function);

        addTask(new AgeAccessibilityTask(access));
        addTask(new GenderAccessibilityTask(access));
    }

}
