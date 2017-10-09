package playground.gregor.misanthrope.simulation.physics;
/* *********************************************************************** *
 * project: org.matsim.*
 *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.mobsim.framework.DriverAgent;

import java.util.List;

/**
 * Created by laemmel on 07/10/15.
 */
public class CTPed {


	private final DriverAgent driver;
	private List<Id<Link>> links;
	private CTCell currentCell;
	private double dir;
	private CTCell tentativeNextCell;

	public CTPed(CTCell cell, DriverAgent driverAgent) {
		this.currentCell = cell;
		this.driver = driverAgent;
		Id<Link> current = driverAgent.getCurrentLinkId();
		CTLink l = (CTLink) cell.getParent();
		if (l.getDsLink().getId() == current) {
			this.dir = Math.PI / 2.;
		}
		else {
			if (l.getUsLink().getId() == current) {
				this.dir = -Math.PI / 2.;
			}
			else {
				throw new RuntimeException("cell does not belong to current link");
			}
		}
	}

	public DriverAgent getDriver() {
		return this.driver;
	}

	public double getDesiredDir() {
		return this.dir;
	}


	public CTCell getNextCellAndJump(double time) {
		if (this.tentativeNextCell.jumpOnPed(this, time)) {


            this.currentCell.jumpOffPed(this, time);
			this.currentCell = tentativeNextCell;

			this.tentativeNextCell = null;

		}
		return this.currentCell;
	}

	public void setTentativeNextCell(CTCell tentativeNextCell) {
		this.tentativeNextCell = tentativeNextCell;
	}

	public void notifyMoveOverNode() {
		CTNetworkEntity p = tentativeNextCell.getParent();
		if (p instanceof CTLink) {
			//TODO this.dir already set! 
			CTLink ctLink = (CTLink) p;
			Link us = ctLink.getUsLink();
			Link ds = ctLink.getDsLink();
			if (us != null && us.getId() == driver.chooseNextLinkId()) {
				this.dir = -Math.PI / 2.;
				driver.notifyMoveOverNode(driver.chooseNextLinkId());
				return;
			}
			else {
				if (ds.getId() == driver.chooseNextLinkId()) {
					this.dir = Math.PI / 2.;
					driver.notifyMoveOverNode(driver.chooseNextLinkId());
					return;
				}
			}
		}
		throw new RuntimeException("error in node-link plan logic");
	}

	public Id<Link> getNextLinkId() {
		return this.driver.chooseNextLinkId();

	}

	@Override
	public String toString() {
		return "id: " + driver.getId().toString();
	}

	public void setDir(double dir) {
		this.dir = dir;
	}
}
