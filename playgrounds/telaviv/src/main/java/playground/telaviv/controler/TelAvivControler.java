/* *********************************************************************** *
 * project: org.matsim.*
 * TelAvivControler.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
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

package playground.telaviv.controler;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

public final class TelAvivControler {
		
	public static void main(final String[] args) {
		if ((args == null) || (args.length == 0)) {
			System.out.println("No argument given!");
			System.out.println("Usage: TelAvivControler config-file base-path");
			System.out.println();
		} else {
			String basePath = null;
			if (args.length > 1) basePath = args[1];
			
			Config config = ConfigUtils.loadConfig(args[0]);
			Scenario scenario = ScenarioUtils.loadScenario(config);
			
			new TelAvivRunner().run(scenario, basePath);
		}
		System.exit(0);
	}

}