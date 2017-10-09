/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2015 by the members listed in the COPYING,        *
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

package playground.ikaddoura.decongestion;

import org.apache.log4j.Logger;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;

import playground.ikaddoura.decongestion.DecongestionConfigGroup.TollingApproach;
import playground.ikaddoura.decongestion.data.DecongestionInfo;
import playground.ikaddoura.decongestion.handler.DelayAnalysis;
import playground.ikaddoura.decongestion.handler.IntervalBasedTolling;
import playground.ikaddoura.decongestion.handler.IntervalBasedTollingAll;
import playground.ikaddoura.decongestion.handler.PersonVehicleTracker;
import playground.ikaddoura.decongestion.routing.TollTimeDistanceTravelDisutilityFactory;
import playground.ikaddoura.decongestion.tollSetting.DecongestionTollSetting;
import playground.ikaddoura.decongestion.tollSetting.DecongestionTollingBangBang;
import playground.ikaddoura.decongestion.tollSetting.DecongestionTollingPID;

/**
* @author ikaddoura
*/

public class Decongestion {
	private static final Logger log = Logger.getLogger(Decongestion.class);

	private final DecongestionInfo info;
	private final Controler controler;
	
	private double sigma = 0.;
		
	public Decongestion(Controler controler, DecongestionInfo info) {
		this.info = info;
		this.controler = controler;
		prepare();
	}

	private void prepare() {
								
		final DecongestionTollSetting tollSettingApproach;
		
		if (info.getDecongestionConfigGroup().getTOLLING_APPROACH().equals(TollingApproach.PID)) {
			tollSettingApproach = new DecongestionTollingPID(info);	
			
		} else if (info.getDecongestionConfigGroup().getTOLLING_APPROACH().equals(TollingApproach.BangBang)) {
			tollSettingApproach = new DecongestionTollingBangBang(info);
			
		} else if (info.getDecongestionConfigGroup().getTOLLING_APPROACH().equals(TollingApproach.NoPricing)) {
			info.getDecongestionConfigGroup().setKp(0.);
			info.getDecongestionConfigGroup().setKd(0.);
			info.getDecongestionConfigGroup().setKi(0.);
			info.getDecongestionConfigGroup().setUPDATE_PRICE_INTERVAL(Integer.MAX_VALUE);
			info.getDecongestionConfigGroup().setTOLERATED_AVERAGE_DELAY_SEC(Double.MAX_VALUE);			
			tollSettingApproach = new DecongestionTollingPID(info);
			
		} else {
			throw new RuntimeException("Decongestion toll setting approach not implemented. Aborting...");
		}
				
		// decongestion pricing
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				
				this.bind(DecongestionInfo.class).toInstance(info);
				this.bind(DecongestionTollSetting.class).toInstance(tollSettingApproach);

				this.bind(IntervalBasedTolling.class).to(IntervalBasedTollingAll.class);
				
				this.bind(IntervalBasedTollingAll.class).asEagerSingleton();
				this.bind(DelayAnalysis.class).asEagerSingleton();
				this.bind(PersonVehicleTracker.class).asEagerSingleton();
								
				this.addEventHandlerBinding().to(IntervalBasedTollingAll.class);
				this.addEventHandlerBinding().to(DelayAnalysis.class);
				this.addEventHandlerBinding().to(PersonVehicleTracker.class);
				
				this.addControlerListenerBinding().to(DecongestionControlerListener.class);

			}
		});
		
		// toll-adjusted routing
		
		final TollTimeDistanceTravelDisutilityFactory travelDisutilityFactory = new TollTimeDistanceTravelDisutilityFactory();
		travelDisutilityFactory.setSigma(sigma);
		
		controler.addOverridingModule(new AbstractModule(){
			@Override
			public void install() {
				this.bindCarTravelDisutilityFactory().toInstance( travelDisutilityFactory );
			}
		});		
	}

	public void run() {	
		
        controler.getConfig().controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.failIfDirectoryExists);
		controler.run();
		
		log.info("Decongestion simulation run completed.");
	}

	public Controler getControler() {
		return controler;
	}
	
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

}

