/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
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

package playground.gregor.misanthrope.run;

import com.google.inject.Provider;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.MatsimServices;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.events.IterationStartsEvent;
import org.matsim.core.controler.listener.IterationStartsListener;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.scenario.ScenarioUtils;
import playground.gregor.misanthrope.router.CTRoutingModule;
import playground.gregor.misanthrope.simulation.CTMobsimFactory;
import playground.gregor.misanthrope.simulation.physics.CTLink;
import playground.gregor.sim2d_v4.debugger.eventsbaseddebugger.EventBasedVisDebuggerEngine;
import playground.gregor.sim2d_v4.debugger.eventsbaseddebugger.InfoBox;
import playground.gregor.sim2d_v4.debugger.eventsbaseddebugger.QSimDensityDrawer;


public class CTRunner implements IterationStartsListener {
    private static final Logger log = Logger.getLogger(CTRunner.class);
    public static boolean DEBUG = false;

    private MatsimServices controller;
    private QSimDensityDrawer qSimDrawer;

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(-1);
        }
        if (args.length >= 3) {
            CTLink.DESIRED_WIDTH_IN_CELLS = Double.parseDouble(args[2]);
        }
        String qsimConf = args[0];

        boolean vis = Boolean.parseBoolean(args[1]);
        DEBUG = vis;

        Config c = ConfigUtils.loadConfig(qsimConf);

        c.controler().setWriteEventsInterval(1);
        c.controler().setMobsim("ctsim");
        final Scenario sc = ScenarioUtils.loadScenario(c);


        sc.getPopulation().getPersons().values().parallelStream().flatMap(p -> p.getPlans().stream()).forEach(pl -> {
            Activity a0 = (Activity) pl.getPlanElements().get(0);
            Leg leg = (Leg) pl.getPlanElements().get(1);
            leg.setRoute(null);
            leg.setMode("walkct");
            a0.setType("pre-evac");
            Activity a1 = (Activity) pl.getPlanElements().get(2);
            a1.setType("post-evac");
        });


        final Controler controller = new Controler(sc);
        if (vis) {
//			Sim2DConfig conf2d = Sim2DConfigUtils.createConfig();
//			Sim2DScenario sc2d = Sim2DScenarioUtils.createSim2dScenario(conf2d);
//
//
//			sc.addScenarioElement(Sim2DScenario.ELEMENT_NAME, sc2d);
            EventBasedVisDebuggerEngine dbg = new EventBasedVisDebuggerEngine(sc);
            InfoBox iBox = new InfoBox(dbg, sc);
            dbg.addAdditionalDrawer(iBox);
            //		dbg.addAdditionalDrawer(new Branding());
//			QSimDensityDrawer qDbg = new QSimDensityDrawer(sc);
//			dbg.addAdditionalDrawer(qDbg);

            EventsManager em = controller.getEvents();
//			em.addHandler(qDbg);
            em.addHandler(dbg);
        }


        controller.getConfig().controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.overwriteExistingFiles);


        controller.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                addRoutingModuleBinding("walkct").toProvider(CTRoutingModule.class);
            }
        });


        final CTMobsimFactory factory = new CTMobsimFactory();


        controller.addOverridingModule(new AbstractModule() {

            @Override
            public void install() {
                addEventHandlerBinding().to(ArrivalCounter.class);
                addControlerListenerBinding().to(UTurnCleaner.class);
                if (getConfig().controler().getMobsim().equals("ctsim")) {
                    bind(Mobsim.class).toProvider(new Provider<Mobsim>() {
                        @Override
                        public Mobsim get() {
                            return factory.createMobsim(controller.getScenario(), controller.getEvents());
                        }
                    });
                }
            }
        });

        controller.run();
    }


    protected static void printUsage() {
        System.out.println();
        System.out.println("CTRunner");
        System.out.println("Controller for misanthrope (pedestrian) simulations.");
        System.out.println();
        System.out.println("usage : CARunner config visualize");
        System.out.println();
        System.out.println("config:   A MATSim config file.");
        System.out.println("visualize:   one of {true,false}.");
        System.out.println();
        System.out.println("---------------------");
        System.out.println("2015-2017, matsim.org");
        System.out.println();
    }

    @Override
    public void notifyIterationStarts(IterationStartsEvent event) {
        if ((event.getIteration()) % 5 == 0 || event.getIteration() > 0) {
            // this.factory.debug(this.visDebugger);
            this.controller.getEvents().addHandler(this.qSimDrawer);
            this.controller.getConfig().controler().setCreateGraphs(true);
        } else {
            // this.factory.debug(null);
            this.controller.getEvents().removeHandler(this.qSimDrawer);
            this.controller.getConfig().controler().setCreateGraphs(false);
        }
        // this.visDebugger.setIteration(event.getIteration());
    }
}
