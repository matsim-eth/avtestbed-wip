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
package playground.dziemke.accessibility;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.accessibility.AccessibilityConfigGroup;
import org.matsim.contrib.accessibility.utils.AccessibilityUtils;
import org.matsim.contrib.accessibility.utils.VisualizationUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlansConfigGroup;
import org.matsim.core.config.groups.VspExperimentalConfigGroup.VspDefaultsCheckingLevel;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author dziemke
 */
public class AccessibilityComputationBerlin {
	public static final Logger LOG = Logger.getLogger(AccessibilityComputationBerlin.class);
	
	private static final Double cellSize = 5000.;
	
	public static void main(String[] args) {
		// Input and output
//		String networkFile = "../../shared-svn/projects/bvg_3_bln_inputdata/rev554B-bvg00-0.1sample/network/network.all.xml";
//		String networkFile = "../../shared-svn/studies/countries/de/berlin/counts/iv_counts/network.xml";
//		String networkFile = "../../../shared-svn/projects/accessibility_berlin/iv_counts/network.xml"; // same network at alternate location
		String networkFile = "../../../shared-svn/projects/accessibility_berlin/network/2015-05-26/network.xml";
		
//		String facilitiesFile = "../../shared-svn/projects/accessibility_berlin/osm/facilities_amenities_modified.xml";
		// now using work facilities
//		String facilitiesFile = "../../../../SVN/shared-svn/projects/accessibility_berlin/osm/berlin/combined/01/facilities.xml";
//		String facilitiesFile = "../../../shared-svn/projects/accessibility_berlin/osm/berlin/combined/01/facilities.xml";
		String facilitiesFile = "../../../shared-svn/projects/accessibility_berlin/refugee/initiatives.xml";

//		String outputDirectory = "../../../../SVN/shared-svn/projects/accessibility_berlin/output/08_test/";
//		String outputDirectory = "../../../shared-svn/projects/accessibility_berlin/output/08_test_2016-11-09/";
		String outputDirectory = "../../../shared-svn/projects/accessibility_berlin/output/refugee/";
		
//		String travelTimeMatrix = "/Users/dominik/Workspace/shared-svn/projects/accessibility_berlin/pt/be_04/travelTimeMatrix.csv.gz";
//		String travelDistanceMatrix = "/Users/dominik/Workspace/shared-svn/projects/accessibility_berlin/pt/be_04/travelDistanceMatrix.csv.gz";
//		String ptStops = "/Users/dominik/Workspace/shared-svn/projects/accessibility_berlin/pt/be_04/stops.csv.gz";
		
//		LogToOutputSaver.setOutputDirectory(outputDirectory);
		
		// Parameters
		final String crs = "EPSG:31468"; // = DHDN GK4
		final Envelope envelope = new Envelope(4574000, 4620000, 5802000, 5839000); // all Berlin
		final String runId = "de_berlin_" + AccessibilityUtils.getDate() + "_" + cellSize.toString().split("\\.")[0];
		final boolean push2Geoserver = true;
		
		// QGis parameters
		boolean createQGisOutput = false;
		final boolean includeDensityLayer = false;
		final Double lowerBound = -3.5; // (upperBound - lowerBound) ideally nicely divisible by (range - 2)
		final Double upperBound = 3.5;
		final Integer range = 9;
		final int symbolSize = 5000; // Choose slightly higher than cell size
		final int populationThreshold = (int) (200 / (1000/cellSize * 1000/cellSize));
		
		// Storage objects
		final List<String> modes = new ArrayList<>();

		// Config and scenario
		final Config config = ConfigUtils.createConfig(new AccessibilityConfigGroup()
				//, new MatrixBasedPtRouterConfigGroup()
				);
		config.network().setInputFile(networkFile);
		config.facilities().setInputFile(facilitiesFile);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setOutputDirectory(outputDirectory);
		config.controler().setLastIteration(0);
//		AccessibilityConfigGroup acg = ConfigUtils.addOrGetModule(config, AccessibilityConfigGroup.GROUP_NAME, AccessibilityConfigGroup.class);
//		acg.setComputingAccessibilityForMode(Modes4Accessibility.car, true); // if this is not set to true, output CSV will give NaN values
//		acg.setComputingAccessibilityForMode(Modes4Accessibility.bike, true);
//		acg.setComputingAccessibilityForMode(Modes4Accessibility.walk, true);
		final Scenario scenario = ScenarioUtils.loadScenario(config);

		// ##### Matrix-based pt
//		MatrixBasedPtRouterConfigGroup mbpcg = (MatrixBasedPtRouterConfigGroup) config.getModule(MatrixBasedPtRouterConfigGroup.GROUP_NAME);
//		mbpcg.setPtStopsInputFile(ptStopsFilePT);
//		mbpcg.setUsingTravelTimesAndDistances(true);
//		mbpcg.setPtTravelDistancesInputFile(travelDistanceMatrixFilePT);
//		mbpcg.setPtTravelTimesInputFile(travelTimeMatrixFilePT);

		// plansClacRoute parameters
//		PlansCalcRouteConfigGroup plansCalcRoute = config.plansCalcRoute();

		// if no travel matrix (distances and times) is provided, the teleported mode speed for pt needs to be set
		// teleported mode speed for pt also required, see PtMatrix:120
//		ModeRoutingParams ptParameters = new ModeRoutingParams(TransportMode.pt);
//		ptParameters.setTeleportedModeSpeed(50./3.6);
//		plansCalcRoute.addModeRoutingParams(ptParameters);

		// by adding ModeRoutingParams (as done above for pt), the other parameters are deleted
		// the walk and bike parameters are needed, however. This is why they have to be set here again

		// teleported mode speed for walking also required, see PtMatrix:141
//		ModeRoutingParams walkParameters = new ModeRoutingParams(TransportMode.walk);
//		walkParameters.setTeleportedModeSpeed(3./3.6);
//		plansCalcRoute.addModeRoutingParams(walkParameters );
//
//		// teleported mode speed for bike also required, see AccessibilityControlerListenerImpl:168
//		ModeRoutingParams bikeParameters = new ModeRoutingParams(TransportMode.bike);
//		bikeParameters.setTeleportedModeSpeed(15./3.6);
//		plansCalcRoute.addModeRoutingParams(bikeParameters );

		// pt matrix
//		BoundingBox boundingBox = BoundingBox.createBoundingBox(scenario.getNetwork());
//		final PtMatrix ptMatrix = PtMatrix.createPtMatrix(plansCalcRoute, boundingBox, mbpcg);
		
		// ##### end of PT block

		// Collect activity types
//		final List<String> activityTypes = AccessibilityRunUtils.collectAllFacilityTypes(scenario);
//		log.warn( "found activity types: " + activityTypes );
		// yyyy there is some problem with activity types: in some algorithms, only the first letter is interpreted, in some
		// other algorithms, the whole string.  BEWARE!  This is not good software design and should be changed.  kai, feb'14
		final List<String> activityTypes = new ArrayList<String>();
		activityTypes.add("Refugee_Initiative");
		
		// Settings for VSP check
		config.timeAllocationMutator().setMutationRange(7200.);
		config.timeAllocationMutator().setAffectingDuration(false);
		config.plans().setRemovingUnneccessaryPlanAttributes(true);
		config.plans().setActivityDurationInterpretation(PlansConfigGroup.ActivityDurationInterpretation.tryEndTimeThenDuration);
		config.vspExperimental().setVspDefaultsCheckingLevel(VspDefaultsCheckingLevel.warn);

		// Collect homes for density layer
//		String activityFacilityType = FacilityTypes.HOME;
//		final ActivityFacilities densityFacilities = AccessibilityUtils.collectActivityFacilitiesWithOptionOfType(scenario, activityFacilityType);

		// Network density points
		ActivityFacilities measuringPoints = AccessibilityUtils.createMeasuringPointsFromNetworkBounds(scenario.getNetwork(), cellSize);
		double maximumAllowedDistance = 0.5 * cellSize;
		final ActivityFacilities densityFacilities = AccessibilityUtils.createNetworkDensityFacilities(scenario.getNetwork(), measuringPoints, maximumAllowedDistance);

		// Controller
		final Controler controler = new Controler(scenario);
//		controler.addControlerListener(new AccessibilityStartupListener(activityTypes, densityFacilities, crs, runId, envelope, cellSize, push2Geoserver));
		if ( true ) {
			throw new RuntimeException("AccessibilityStartupListener is no longer supported; please switch to GridBasedAccessibilityModule. kai, dec'16") ;
		}
		
		if ( true ) {
			throw new RuntimeException("The now following execution path is no longer supported; please set the modes in the config (as it was earlier). kai, dec'16" ) ;
		}
		// Add calculators
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
//					String mode = TransportMode.car;
//					this.binder().bind(AccessibilityContributionCalculator.class).annotatedWith(Names.named(mode)).toProvider(new NetworkModeProvider(mode));
//					accBinder.addBinding(mode).to(Key.get(AccessibilityContributionCalculator.class, Names.named(mode)));
//					if (!modes.contains(mode)) modes.add(mode); // This install method is called four times, but each new mode should only be added once
//				}
//				{ 
//					String mode = TransportMode.bike;
//					this.binder().bind(AccessibilityContributionCalculator.class).annotatedWith(Names.named(mode)).toProvider(new ConstantSpeedModeProvider(mode));
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