package playground.clruch;

import java.io.File;
import java.net.MalformedURLException;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.trafficmonitoring.VrpTravelTimeModules;
import org.matsim.contrib.dynagent.run.DynQSimModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

import playground.clruch.analysis.AnalyzeAll;
import playground.clruch.analysis.AnalyzeSummary;
import playground.clruch.data.ReferenceFrame;
import playground.clruch.html.DataCollector;
import playground.clruch.html.ReportGenerator;
import playground.clruch.net.DatabaseModule;
import playground.clruch.net.MatsimStaticDatabase;
import playground.clruch.net.SimulationServer;
import playground.clruch.net.StorageUtils;
import playground.clruch.utils.PropertiesExt;
import playground.sebhoerl.avtaxi.framework.AVConfigGroup;
import playground.sebhoerl.avtaxi.framework.AVModule;
import playground.sebhoerl.avtaxi.framework.AVQSimProvider;

/**
 * only one ScenarioServer can run at one time, since a fixed network port is
 * reserved to serve the simulation status
 */
public class ScenarioServer {

	public static void main(String[] args) throws MalformedURLException, Exception {
		simulate();
	}

	/* package */ static void simulate() throws MalformedURLException, Exception {
		// load options
		File workingDirectory = new File("").getCanonicalFile();
		PropertiesExt simOptions = PropertiesExt.wrap(ScenarioOptions.load(workingDirectory));

		/**
		 * set to true in order to make server wait for at least 1 client, for
		 * instance viewer client
		 */
		boolean waitForClients = simOptions.getBoolean("waitForClients");
		File configFile = new File(workingDirectory, simOptions.getString("simuConfig"));
		ReferenceFrame referenceFrame = simOptions.getReferenceFrame();

		// open server port for clients to connect to
		SimulationServer.INSTANCE.startAcceptingNonBlocking();
		SimulationServer.INSTANCE.setWaitForClients(waitForClients);

		// load MATSim configs
		System.out.println("loading config file " + configFile.getAbsoluteFile());
		GlobalAssert.that(configFile.exists());
		DvrpConfigGroup dvrpConfigGroup = new DvrpConfigGroup();
		dvrpConfigGroup.setTravelTimeEstimationAlpha(0.05);
		Config config = ConfigUtils.loadConfig(configFile.toString(), new AVConfigGroup(), dvrpConfigGroup
				);  //, //
				//new BlackListedTimeAllocationMutatorConfigGroup());
		String outputdirectory = config.controler().getOutputDirectory();
		System.out.println("outputdirectory = " + outputdirectory);

		// load scenario for simulation
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Network network = scenario.getNetwork();
		Population population = scenario.getPopulation();
		GlobalAssert.that(scenario != null && network != null && population != null);

		MatsimStaticDatabase.initializeSingletonInstance(network, referenceFrame);
		Controler controler = new Controler(scenario);

		controler.addOverridingModule(VrpTravelTimeModules.createTravelTimeEstimatorModule(0.05));
		controler.addOverridingModule(new DynQSimModule<>(AVQSimProvider.class));
		controler.addOverridingModule(new AVModule());
		controler.addOverridingModule(new DatabaseModule());
//		controler.addOverridingModule(new AVTravelTimeModule());

		// directories for saving results
		StorageUtils.OUTPUT = new File(config.controler().getOutputDirectory());
		StorageUtils.DIRECTORY = new File(StorageUtils.OUTPUT, "simobj");

		// run simulation
		controler.run();

		// close port for visualization
		SimulationServer.INSTANCE.stopAccepting();

		// perform analysis of results
		AnalyzeSummary analyzeSummary = AnalyzeAll.analyze(configFile, outputdirectory);

		DataCollector datacollector = new DataCollector(configFile, outputdirectory, controler, //
				analyzeSummary, network, population);

		// generate report
		ReportGenerator.from(configFile, outputdirectory);

	}
}
