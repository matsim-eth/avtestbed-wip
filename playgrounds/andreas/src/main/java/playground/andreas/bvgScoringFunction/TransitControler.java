package playground.andreas.bvgScoringFunction;

import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.PtConstants;

/**
 * @author aneumann
 */
public class TransitControler {

	private final static Logger log = Logger.getLogger(TransitControler.class);

	private boolean useOTFVis = false;
	
	public TransitControler(MutableScenario scenario) {
//		super(scenario);
		// TODO Auto-generated constructor stub
		throw new RuntimeException( Gbl.RUN_MOB_SIM_NO_LONGER_POSSIBLE ) ;
	}

//	@Override
//	protected void runMobSim() {
//		
//		log.info("Overriding runMobSim()");
//
//        Scenario sc = this.getScenario();EventsManager eventsManager = this.getEvents();
//
//        QSimConfigGroup conf = sc.getConfig().qsim();
//        if (conf == null) {
//            throw new NullPointerException("There is no configuration set for the QSim. Please add the module 'qsim' to your config file.");
//        }
//
//		QSim qSim1 = new QSim(sc, eventsManager);
//		ActivityEngine activityEngine = new ActivityEngine(eventsManager, qSim1.getAgentCounter());
//		qSim1.addMobsimEngine(activityEngine);
//		qSim1.addActivityHandler(activityEngine);
//        QNetsimEngineModule.configure(qSim1);
//		TeleportationEngine teleportationEngine = new TeleportationEngine(sc, eventsManager);
//		qSim1.addMobsimEngine(teleportationEngine);
//        QSim qSim = qSim1;
//        AgentFactory agentFactory;
//            agentFactory = new TransitAgentFactory(qSim);
//            TransitQSimEngine transitEngine = new TransitQSimEngine(qSim);
//            transitEngine.setTransitStopHandlerFactory(new ComplexTransitStopHandlerFactory());
//            qSim.addDepartureHandler(transitEngine);
//            qSim.addAgentSource(transitEngine);
//        PopulationAgentSource agentSource = new PopulationAgentSource(sc.getPopulation(), agentFactory, qSim);
//        qSim.addAgentSource(agentSource);
//
//        QSim simulation = (QSim) qSim;
//
//		transitEngine.setTransitStopHandlerFactory(new ComplexTransitStopHandlerFactory());
////		this.events.addHandler(new LogOutputEventHandler());
//
//
//		if (simulation instanceof ObservableMobsim){
//			for (MobsimListener l : this.getMobsimListeners()) {
//				((ObservableMobsim)simulation).addQueueSimulationListeners(l);
//			}
//		}
//		if (this.useOTFVis) {
//			OnTheFlyServer server = OTFVis.startServerAndRegisterWithQSim(getConfig(),getScenario(), getEvents(), simulation);
//			OTFClientLive.run(getConfig(), server);
//		}
//		simulation.run();
//	}	
	
	void setUseOTFVis(boolean useOTFVis) {
		this.useOTFVis = useOTFVis;
	}

	public static void main(final String[] args) {
		
		String configFile = args[0];		
		Config config;
		
		// reading the config file:
		config = ConfigUtils.loadConfig(configFile);
		
		// manipulate config
		// add "pt interaction" cause controler.init() is called too late and in a protected way
		ActivityParams transitActivityParams = new ActivityParams(PtConstants.TRANSIT_ACTIVITY_TYPE);
		transitActivityParams.setTypicalDuration(120.0);
		config.planCalcScore().addActivityParams(transitActivityParams);
		
		// reading the scenario (based on the config):
		MutableScenario sc = (MutableScenario) ScenarioUtils.loadScenario(config);
		
		Controler tc = new Controler(sc);
        tc.setScoringFunctionFactory(new BvgScoringFunctionFactory(sc,  new BvgScoringFunctionConfigGroup(config)));
		


		// Not needed to use own scoring function

//		if(args.length > 1 && args[1].equalsIgnoreCase("true")){
//			tc.setUseOTFVis(true);
//		}
		tc.getConfig().controler().setOverwriteFileSetting(
				true ?
						OutputDirectoryHierarchy.OverwriteFileSetting.overwriteExistingFiles :
						OutputDirectoryHierarchy.OverwriteFileSetting.failIfDirectoryExists );
		//			tc.setCreateGraphs(false);
		tc.run();
		
	}
}
