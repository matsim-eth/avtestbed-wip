package playground.balac.twowaycarsharingredisigned.controler;

import java.util.List;

import com.google.inject.Provider;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.router.*;
import org.matsim.core.scenario.ScenarioUtils;

import playground.balac.freefloating.router.FreeFloatingRoutingModule;
import playground.balac.twowaycarsharingredisigned.config.TwoWayCSConfigGroup;
import playground.balac.twowaycarsharingredisigned.qsim.TwoWayCSQsimFactory;
import playground.balac.twowaycarsharingredisigned.router.TwoWayCSRoutingModule;
import playground.balac.twowaycarsharingredisigned.scoring.TwoWayCSScoringFunctionFactory;

public class TwoWayCSControler {

	public static void main(final String[] args) {
		
    	final Config config = ConfigUtils.loadConfig(args[0]);
    	TwoWayCSConfigGroup configGroup = new TwoWayCSConfigGroup();
    	config.addModule(configGroup);
		final Scenario sc = ScenarioUtils.loadScenario(config);
		
		
		final Controler controler = new Controler( sc );

		controler.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                bindMobsim().toProvider(new Provider<Mobsim>() {
                    @Override
                    public Mobsim get() {
                        return new TwoWayCSQsimFactory(sc, controler).createMobsim(controler.getScenario(), controler.getEvents());
                    }
                });
            }
        });

		  controler.addOverridingModule(new AbstractModule() {

				@Override
				public void install() {

					addRoutingModuleBinding("twowaycarsharing").toInstance(new TwoWayCSRoutingModule());

					bind(MainModeIdentifier.class).toInstance(new MainModeIdentifier() {

	                    final MainModeIdentifier defaultModeIdentifier = new MainModeIdentifierImpl();
						
						@Override
						public String identifyMainMode(List<? extends PlanElement> tripElements) {

							for ( PlanElement pe : tripElements ) {
	                            if ( pe instanceof Leg && ((Leg) pe).getMode().equals( "twowaycarsharing" ) ) {
	                                return "twowaycarsharing";
	                            }
	                           
	                        }
	                        // if the trip doesn't contain a carsharing leg,
	                        // fall back to the default identification method.
	                        return defaultModeIdentifier.identifyMainMode( tripElements );
						
						}				
						
					});		
					
				}
				
			});

		TwoWayCSScoringFunctionFactory onewayScoringFunctionFactory = new TwoWayCSScoringFunctionFactory(
				config,
				sc.getNetwork());
		controler.setScoringFunctionFactory(onewayScoringFunctionFactory);


//		    super.loadControlerListeners();
		controler.addControlerListener(new TWListener(controler.getConfig().getModule("TwoWayCarsharing").getValue("statsFileName")));

		controler.run();
	}

}
