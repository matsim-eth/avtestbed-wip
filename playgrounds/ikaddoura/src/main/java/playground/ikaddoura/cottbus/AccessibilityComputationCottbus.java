/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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
package playground.ikaddoura.cottbus;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.contrib.accessibility.AccessibilityConfigGroup;
import org.matsim.contrib.accessibility.FacilityTypes;
import org.matsim.contrib.accessibility.Modes4Accessibility;
import org.matsim.contrib.accessibility.utils.AccessibilityUtils;
import org.matsim.contrib.accessibility.utils.VisualizationUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.facilities.ActivityFacilities;

import com.vividsolutions.jts.geom.Envelope;

import playground.dziemke.utils.LogToOutputSaver;

/**
 * @author dziemke, ikaddoura
 */
public class AccessibilityComputationCottbus {
	public static final Logger log = Logger.getLogger(AccessibilityComputationCottbus.class);
	
	public static void main(String[] args) {
		// Input and output
		String runOutputFolder = "../../../public-svn/matsim/scenarios/countries/de/cottbus/commuter-population-only-car-traffic-only-100pct-2016-03-18/";
		String networkFile = runOutputFolder + "network_wgs84_utm33n.xml.gz";
		String facilitiesFile = "/Users/ihab/Documents/workspace/public-svn/matsim/scenarios/countries/de/cottbus/facilities_final_WGS84_UTM33N.xml";
		String plansFile = runOutputFolder + "commuter_population_wgs84_utm33n_car_only.xml";
		String configFile = runOutputFolder + "config.xml";
		String accessibilityOutputDirectory = runOutputFolder + "accessibilities_final/";	
		
		// Parameters
		final Double cellSize = 100.;
		String crs = TransformationFactory.WGS84_UTM33N; // EPSG:32633 -- UTM33N
		Envelope envelope = new Envelope(447000,5729000,461000,5740000);
		final String runId = "de_cottbus_ihab" + "_" + cellSize.toString().split("\\.")[0];
		final boolean push2Geoserver = false;
		
		// QGis parameters
		boolean createQGisOutput = true;
		boolean includeDensityLayer = true;
		Double lowerBound = .0;
		Double upperBound = 3.5;
		Integer range = 9;
		int symbolSize = 110;
		int populationThreshold = (int) (200 / (1000/cellSize * 1000/cellSize));
		
		// Storage objects
		final List<String> modes = new ArrayList<>();
		
		// Config and scenario
		Config config = ConfigUtils.loadConfig(configFile, new AccessibilityConfigGroup());
		config.network().setInputFile(networkFile);
		config.facilities().setInputFile(facilitiesFile);
		config.plans().setInputFile(plansFile);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setOutputDirectory(accessibilityOutputDirectory);
		config.controler().setLastIteration(0);
		AccessibilityConfigGroup acg = ConfigUtils.addOrGetModule(config, AccessibilityConfigGroup.GROUP_NAME, AccessibilityConfigGroup.class);
		acg.setComputingAccessibilityForMode(Modes4Accessibility.car, true); // if this is not set to true, output CSV will give NaN values
		acg.setComputingAccessibilityForMode(Modes4Accessibility.bike, true);
		acg.setComputingAccessibilityForMode(Modes4Accessibility.walk, true);
		MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);
		
		// Create facilities from plans
//		ActivityFacilities activityFacilities = AccessibilityRunUtils.createFacilitiesFromPlans(scenario.getPopulation());
//		scenario.setActivityFacilities(activityFacilities);
		
		// Infrastructure
		LogToOutputSaver.setOutputDirectory(accessibilityOutputDirectory);

		// Collect activity types
		List<String> activityTypes = new ArrayList<String>();
		activityTypes.add("work"); 
		activityTypes.add("education"); 
		activityTypes.add("grave_yard");
		activityTypes.add("police");
		activityTypes.add("medical");
		activityTypes.add("fire_station");

		// Collect homes for density layer
		String activityFacilityType = FacilityTypes.HOME;
		final ActivityFacilities densityFacilities = AccessibilityUtils.collectActivityFacilitiesWithOptionOfType(scenario, activityFacilityType);

		// Controller
		final Controler controler = new Controler(scenario);
//		controler.addControlerListener(new AccessibilityStartupListener(activityTypes, densityFacilities, crs, runId, envelope, cellSize, push2Geoserver));
		if ( true ) {
			throw new RuntimeException("AccessibilityStartupListener is no longer supported; please switch to GridBasedAccessibilityModule. kai, dec'16") ;
		}

		if ( true ) {
			throw new RuntimeException("The now following execution path is no longer supported; please set the modes in the config (as it was earlier). kai, dec'16" ) ;
		}
//		// Add calculators
//		controler.addOverridingModule(new AbstractModule() {
//			@Override
//			public void install() {
//				MapBinder<String,AccessibilityContributionCalculator> accBinder = MapBinder.newMapBinder(this.binder(), String.class, AccessibilityContributionCalculator.class);
//				{
//					String mode = "freeSpeed";
//					this.binder().bind(AccessibilityContributionCalculator.class).annotatedWith(Names.named(mode)).toProvider(new FreeSpeedNetworkModeProvider(TransportMode.car));
//					accBinder.addBinding(mode).to(Key.get(AccessibilityContributionCalculator.class, Names.named(mode)));
//					if (!modes.contains(mode)) modes.add(mode); // This install method is called four times, but each new mode should only be added once
//				}
//				{
//					final String mode = TransportMode.walk;
//					this.binder().bind(AccessibilityContributionCalculator.class).annotatedWith(Names.named(mode)).toProvider(new ConstantSpeedModeProvider(mode));
//					accBinder.addBinding(mode).to(Key.get(AccessibilityContributionCalculator.class, Names.named(mode)));
//					if (!modes.contains(mode)) modes.add(mode); // This install method is called four times, but each new mode should only be added once
//				}
//			}
//		});
		controler.run();

		// QGis
		if (createQGisOutput == true) {
			String osName = System.getProperty("os.name");
			String workingDirectory = config.controler().getOutputDirectory();
			for (String actType : activityTypes) {
				String actSpecificWorkingDirectory = workingDirectory + actType + "/";
				for (String mode : modes) {
					VisualizationUtils.createQGisOutput(actType, mode, envelope, workingDirectory, crs, includeDensityLayer,
							lowerBound, upperBound, range, symbolSize, populationThreshold);
					VisualizationUtils.createSnapshot(actSpecificWorkingDirectory, mode, osName);
				}
			}  
		}
	}
}