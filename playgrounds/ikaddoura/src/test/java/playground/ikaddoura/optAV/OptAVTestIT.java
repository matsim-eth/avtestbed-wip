/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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

/**
 * 
 */
package playground.ikaddoura.optAV;

import org.junit.*;
import org.matsim.api.core.v01.*;
import org.matsim.contrib.av.robotaxi.scoring.*;
import org.matsim.contrib.dvrp.data.FleetImpl;
import org.matsim.contrib.dvrp.data.file.VehicleReader;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.noise.*;
import org.matsim.contrib.noise.data.NoiseContext;
import org.matsim.contrib.noise.utils.ProcessNoiseImmissions;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.contrib.taxi.optimizer.DefaultTaxiOptimizerProvider;
import org.matsim.contrib.taxi.run.*;
import org.matsim.core.config.*;
import org.matsim.core.controler.*;
import org.matsim.core.router.costcalculators.RandomizingTimeDistanceTravelDisutilityFactory;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

import playground.ikaddoura.analysis.detailedPersonTripAnalysis.PersonTripNoiseAnalysisRun;
import playground.ikaddoura.analysis.linkDemand.LinkDemandEventHandler;
import playground.ikaddoura.decongestion.*;
import playground.ikaddoura.decongestion.DecongestionConfigGroup.TollingApproach;
import playground.ikaddoura.decongestion.data.DecongestionInfo;
import playground.ikaddoura.decongestion.handler.*;
import playground.ikaddoura.decongestion.tollSetting.*;
import playground.ikaddoura.moneyTravelDisutility.*;

/**
 * @author ikaddoura
 *
 */
public class OptAVTestIT {
	
	@Rule
	public MatsimTestUtils testUtils = new MatsimTestUtils();

	/**
	 * only taxi trips
	 */
	@Test
	public final void test1(){

		String configFile = testUtils.getPackageInputDirectory() + "config.xml";
		final boolean otfvis = false;
		 
		// ##################################################################
		// baseCase
		// ##################################################################
		
		Config config1 = ConfigUtils.loadConfig(configFile,
				new TaxiConfigGroup(),
				new DvrpConfigGroup(),
				new OTFVisConfigGroup(),
				new TaxiFareConfigGroup(),
				new NoiseConfigGroup());
		
		config1.controler().setOutputDirectory(testUtils.getOutputDirectory() + "bc1");

		DvrpConfigGroup.get(config1).setMode(TaxiOptimizerModules.TAXI_MODE);

		TaxiConfigGroup taxiCfg1 = TaxiConfigGroup.get(config1);
		config1.addConfigConsistencyChecker(new TaxiConfigConsistencyChecker());
		config1.checkConsistency();
		
		Scenario scenario1 = ScenarioUtils.loadScenario(config1);
		Controler controler1 = new Controler(scenario1);
		
		// taxi

		FleetImpl fleet = new FleetImpl();
		new VehicleReader(scenario1.getNetwork(), fleet).readFile(taxiCfg1.getTaxisFileUrl(config1.getContext()).getFile());
		
		controler1.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addEventHandlerBinding().to(TaxiFareHandler.class).asEagerSingleton();
			}
		});

		controler1.addOverridingModule(new TaxiOutputModule());
		controler1.addOverridingModule(TaxiOptimizerModules.createDefaultModule(fleet));
        
        final RandomizingTimeDistanceTravelDisutilityFactory dvrpTravelDisutilityFactory1 =
        		new RandomizingTimeDistanceTravelDisutilityFactory(DefaultTaxiOptimizerProvider.TAXI_OPTIMIZER, controler1.getConfig().planCalcScore());
		
		controler1.addOverridingModule(new AbstractModule(){
			@Override
			public void install() {
				addTravelDisutilityFactoryBinding(DefaultTaxiOptimizerProvider.TAXI_OPTIMIZER).toInstance(dvrpTravelDisutilityFactory1);
			}
		}); 
				
		// analysis
		
		if (otfvis) controler1.addOverridingModule(new OTFVisLiveModule());	
		
		LinkDemandEventHandler handler1 = new LinkDemandEventHandler(controler1.getScenario().getNetwork());
		controler1.getEvents().addHandler(handler1);
		controler1.getConfig().controler().setCreateGraphs(false);

		// run
		
        controler1.getConfig().controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        controler1.run();
		
		String outputDirectory = controler1.getConfig().controler().getOutputDirectory();
		if (!outputDirectory.endsWith("/")) {
			outputDirectory = outputDirectory + "/";
		}

		NoiseConfigGroup noiseParameters = (NoiseConfigGroup) controler1.getConfig().getModules().get(NoiseConfigGroup.GROUP_NAME);

		NoiseOfflineCalculation noiseCalculation = new NoiseOfflineCalculation(scenario1, outputDirectory);
		noiseCalculation.run();	
		
		String outputFilePath = outputDirectory + "noise-analysis_it." + scenario1.getConfig().controler().getLastIteration() + "/";
		ProcessNoiseImmissions process = new ProcessNoiseImmissions(outputFilePath + "immissions/", outputFilePath + "receiverPoints/receiverPoints.csv", noiseParameters.getReceiverPointGap());
		process.run();
		
		PersonTripNoiseAnalysisRun analysis1 = new PersonTripNoiseAnalysisRun(controler1.getConfig().controler().getOutputDirectory(), outputFilePath + controler1.getConfig().controler().getLastIteration() + ".events_NoiseImmission_Offline.xml.gz");
		analysis1.run();
		
		// ##################################################################
		// noise pricing
		// ##################################################################

		Config config2 = ConfigUtils.loadConfig(configFile,
				new TaxiConfigGroup(),
				new DvrpConfigGroup(),
				new OTFVisConfigGroup(),
				new TaxiFareConfigGroup(),
				new NoiseConfigGroup());
		
		config2.controler().setOutputDirectory(testUtils.getOutputDirectory() + "n");

		DvrpConfigGroup.get(config2).setMode(TaxiOptimizerModules.TAXI_MODE);

		TaxiConfigGroup taxiCfg2 = TaxiConfigGroup.get(config2);
		config2.addConfigConsistencyChecker(new TaxiConfigConsistencyChecker());
		config2.checkConsistency();
		
		Scenario scenario2 = ScenarioUtils.loadScenario(config2);
		Controler controler2 = new Controler(scenario2);
		
		// taxi

		FleetImpl fleet2 = new FleetImpl();
		new VehicleReader(scenario2.getNetwork(), fleet2).readFile(taxiCfg2.getTaxisFileUrl(config2.getContext()).getFile());
		
		controler2.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addEventHandlerBinding().to(TaxiFareHandler.class).asEagerSingleton();
			}
		});
		
		controler2.addOverridingModule(new TaxiOutputModule());
        controler2.addOverridingModule(TaxiOptimizerModules.createDefaultModule(fleet));
		
		final MoneyTimeDistanceTravelDisutilityFactory dvrpTravelDisutilityFactory = new MoneyTimeDistanceTravelDisutilityFactory(
				new RandomizingTimeDistanceTravelDisutilityFactory(DefaultTaxiOptimizerProvider.TAXI_OPTIMIZER,
						controler2.getConfig().planCalcScore()));
		
		controler2.addOverridingModule(new AbstractModule(){
			@Override
			public void install() {
												
				addTravelDisutilityFactoryBinding(DefaultTaxiOptimizerProvider.TAXI_OPTIMIZER).toInstance(dvrpTravelDisutilityFactory);
				
				this.bind(MoneyEventAnalysis.class).asEagerSingleton();
				this.addControlerListenerBinding().to(MoneyEventAnalysis.class);
				this.addEventHandlerBinding().to(MoneyEventAnalysis.class);
			}
		}); 
		
		// noise pricing
		
		NoiseContext noiseContext = new NoiseContext(controler2.getScenario());
		controler2.addControlerListener(new NoiseCalculationOnline(noiseContext));
		
		// analysis
		
		LinkDemandEventHandler handler2 = new LinkDemandEventHandler(controler2.getScenario().getNetwork());
		controler2.getEvents().addHandler(handler2);
		controler2.getConfig().controler().setCreateGraphs(false);
        
		if (otfvis) controler2.addOverridingModule(new OTFVisLiveModule());
		
		// run
        controler2.getConfig().controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		controler2.run();
		
		String outputDirectory2 = controler2.getConfig().controler().getOutputDirectory();
		if (!outputDirectory2.endsWith("/")) {
			outputDirectory2 = outputDirectory2 + "/";
		}

		PersonTripNoiseAnalysisRun analysis2 = new PersonTripNoiseAnalysisRun(controler2.getConfig().controler().getOutputDirectory());
		analysis2.run();

		// print outs
					
		System.out.println("----------------------------------");
		System.out.println("Base case:");
		printResults1(handler1);
	
		System.out.println("----------------------------------");
		System.out.println("Noise pricing:");
		printResults1(handler2);
		
		// the demand on the noise sensitive route should go down in case of noise pricing (n)
		Assert.assertEquals(true, getNoiseSensitiveRouteDemand(handler2) < getNoiseSensitiveRouteDemand(handler1));
	}
	
	/**
	 * car + taxi trips
	 */
	@Test
	public final void test2(){

		String configFile = testUtils.getPackageInputDirectory() + "config2.xml";
		final boolean otfvis = false;
		 
		// ##################################################################
		// baseCase
		// ##################################################################
		
		Config config1 = ConfigUtils.loadConfig(configFile,
				new TaxiConfigGroup(),
				new DvrpConfigGroup(),
				new OTFVisConfigGroup(),
				new TaxiFareConfigGroup(),
				new NoiseConfigGroup());
		
		config1.controler().setOutputDirectory(testUtils.getOutputDirectory() + "bc2");
		
		DvrpConfigGroup.get(config1).setMode(TaxiOptimizerModules.TAXI_MODE);

		TaxiConfigGroup taxiCfg1 = TaxiConfigGroup.get(config1);
		config1.addConfigConsistencyChecker(new TaxiConfigConsistencyChecker());
		config1.checkConsistency();
		
		Scenario scenario1 = ScenarioUtils.loadScenario(config1);
		Controler controler1 = new Controler(scenario1);
		
		// taxi

		FleetImpl fleet1 = new FleetImpl();
		new VehicleReader(scenario1.getNetwork(), fleet1).readFile(taxiCfg1.getTaxisFileUrl(config1.getContext()).getFile());
		
		controler1.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addEventHandlerBinding().to(TaxiFareHandler.class).asEagerSingleton();
			}
		});
		
		controler1.addOverridingModule(new TaxiOutputModule());
        controler1.addOverridingModule(TaxiOptimizerModules.createDefaultModule(fleet1));
		
        final RandomizingTimeDistanceTravelDisutilityFactory dvrpTravelDisutilityFactory1 =
        		new RandomizingTimeDistanceTravelDisutilityFactory(DefaultTaxiOptimizerProvider.TAXI_OPTIMIZER, controler1.getConfig().planCalcScore());
		
		controler1.addOverridingModule(new AbstractModule(){
			@Override
			public void install() {
				// travel disutility factory for DVRP
				addTravelDisutilityFactoryBinding(DefaultTaxiOptimizerProvider.TAXI_OPTIMIZER).toInstance(dvrpTravelDisutilityFactory1);
			}
		}); 
		
		// analysis
		
		if (otfvis) controler1.addOverridingModule(new OTFVisLiveModule());	
		
		LinkDemandEventHandler handler1 = new LinkDemandEventHandler(controler1.getScenario().getNetwork());
		controler1.getEvents().addHandler(handler1);
		controler1.getConfig().controler().setCreateGraphs(false);

		// run
		
        controler1.getConfig().controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		controler1.run();
				
		PersonTripNoiseAnalysisRun analysis1 = new PersonTripNoiseAnalysisRun(controler1.getConfig().controler().getOutputDirectory());
		analysis1.run();
		
		// ##################################################################
		// congestion pricing
		// ##################################################################

		Config config2 = ConfigUtils.loadConfig(configFile,
				new TaxiConfigGroup(),
				new DvrpConfigGroup(),
				new OTFVisConfigGroup(),
				new TaxiFareConfigGroup());
		
		DvrpConfigGroup.get(config2).setMode(TaxiOptimizerModules.TAXI_MODE);
		
		config2.controler().setOutputDirectory(testUtils.getOutputDirectory() + "c");
		
		TaxiConfigGroup taxiCfg2 = TaxiConfigGroup.get(config2);
		config2.addConfigConsistencyChecker(new TaxiConfigConsistencyChecker());
		config2.checkConsistency();
		
		Scenario scenario2 = ScenarioUtils.loadScenario(config2);
		Controler controler2 = new Controler(scenario2);
		
		// congestion pricing
		
		final DecongestionConfigGroup decongestionSettings = new DecongestionConfigGroup();
		decongestionSettings.setMsa(true);
		decongestionSettings.setTOLL_BLEND_FACTOR(0.);
		decongestionSettings.setKd(0.);
		decongestionSettings.setKi(0.);
		decongestionSettings.setKp(999.);
		decongestionSettings.setTOLLING_APPROACH(TollingApproach.PID);
		decongestionSettings.setFRACTION_OF_ITERATIONS_TO_END_PRICE_ADJUSTMENT(1.0);
		decongestionSettings.setFRACTION_OF_ITERATIONS_TO_START_PRICE_ADJUSTMENT(0.0);
		decongestionSettings.setUPDATE_PRICE_INTERVAL(1);
		decongestionSettings.setWRITE_LINK_INFO_CHARTS(false);
		decongestionSettings.setRUN_FINAL_ANALYSIS(false);
		
		DecongestionInfo info = new DecongestionInfo(decongestionSettings);
		DecongestionTollingPID tollSetting = new DecongestionTollingPID(info);
		
		controler2.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				
				this.bind(DecongestionInfo.class).toInstance(info);
				this.bind(DecongestionTollSetting.class).toInstance(tollSetting);
				
				this.bind(IntervalBasedTolling.class).to(IntervalBasedTollingAV.class);

				this.bind(IntervalBasedTollingAV.class).asEagerSingleton();
				this.bind(DelayAnalysis.class).asEagerSingleton();
				this.bind(PersonVehicleTracker.class).asEagerSingleton();
								
				this.addEventHandlerBinding().to(IntervalBasedTollingAV.class);
				this.addEventHandlerBinding().to(DelayAnalysis.class);
				this.addEventHandlerBinding().to(PersonVehicleTracker.class);
				
				this.addControlerListenerBinding().to(DecongestionControlerListener.class);

			}
		});
		
		// taxi

		FleetImpl fleet2 = new FleetImpl();
		new VehicleReader(scenario1.getNetwork(), fleet2).readFile(taxiCfg2.getTaxisFileUrl(config2.getContext()).getFile());
		
		controler2.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addEventHandlerBinding().to(TaxiFareHandler.class).asEagerSingleton();
			}
		});
		
		controler2.addOverridingModule(new TaxiOutputModule());
        controler2.addOverridingModule(TaxiOptimizerModules.createDefaultModule(fleet2));
		
		final MoneyTimeDistanceTravelDisutilityFactory dvrpTravelDisutilityFactory2 = new MoneyTimeDistanceTravelDisutilityFactory(
				new RandomizingTimeDistanceTravelDisutilityFactory(DefaultTaxiOptimizerProvider.TAXI_OPTIMIZER, controler2.getConfig().planCalcScore())
				);
		
		controler2.addOverridingModule(new AbstractModule(){
			@Override
			public void install() {
												
				// travel disutility factory for DVRP
				addTravelDisutilityFactoryBinding(DefaultTaxiOptimizerProvider.TAXI_OPTIMIZER).toInstance(dvrpTravelDisutilityFactory2);

//				this.bind(AgentFilter.class).to(AVAgentFilter.class); // TODO: add once person is not null
				
				this.bind(MoneyEventAnalysis.class).asEagerSingleton();
				this.addControlerListenerBinding().to(MoneyEventAnalysis.class);
				this.addEventHandlerBinding().to(MoneyEventAnalysis.class);
			}
		}); 
		
		// analysis
		
		LinkDemandEventHandler handler2 = new LinkDemandEventHandler(controler2.getScenario().getNetwork());
		controler2.getEvents().addHandler(handler2);
		controler2.getConfig().controler().setCreateGraphs(false);
        
		if (otfvis) controler2.addOverridingModule(new OTFVisLiveModule());
		
		// run
        controler2.getConfig().controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		controler2.run();
		
		String outputDirectory2 = controler2.getConfig().controler().getOutputDirectory();
		if (!outputDirectory2.endsWith("/")) {
			outputDirectory2 = outputDirectory2 + "/";
		}

		PersonTripNoiseAnalysisRun analysis2 = new PersonTripNoiseAnalysisRun(controler2.getConfig().controler().getOutputDirectory());
		analysis2.run();

		// print outs
					
		System.out.println("----------------------------------");
		System.out.println("Base case:");
		printResults1(handler1);
	
		System.out.println("----------------------------------");
		System.out.println("Congestion pricing:");
		printResults1(handler2);
		
		// the demand on the congested route should go down in case of congestion pricing
		Assert.assertEquals(true, getNoiseSensitiveRouteDemand(handler2) < getNoiseSensitiveRouteDemand(handler1));
	}
	
	private void printResults1(LinkDemandEventHandler handler) {
		System.out.println("long but uncongested: " + getLongUncongestedDemand(handler));
		System.out.println("high congestion cost: " + (getNoiseSensitiveRouteDemand(handler)));
	}
	
	private int getNoiseSensitiveRouteDemand(LinkDemandEventHandler handler) {
		int noiseSensitiveRouteDemand = 0;
		if (handler.getLinkId2demand().containsKey(Id.createLinkId("link_7_8"))) {
			noiseSensitiveRouteDemand = handler.getLinkId2demand().get(Id.createLinkId("link_7_8"));
		}
		return noiseSensitiveRouteDemand;
	}
	
	private int getLongUncongestedDemand(LinkDemandEventHandler handler) {
		int longUncongestedRouteDemand = 0;
		if (handler.getLinkId2demand().containsKey(Id.createLinkId("link_1_2"))) {
			longUncongestedRouteDemand = handler.getLinkId2demand().get(Id.createLinkId("link_1_2"));
		}
		return longUncongestedRouteDemand;
	}
		
}
