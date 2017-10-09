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

package org.matsim.contrib.drt.tasks;

import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.schedule.DriveTaskImpl;

/**
 * @author  jbischoff
 *	Task for driving w/o pax
 */
public class DrtDriveTask extends DriveTaskImpl implements DrtTask {

	public DrtDriveTask(VrpPathWithTravelData path) {
		super(path);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DrtTaskType getDrtTaskType() {
		
		return DrtTaskType.DRIVE_EMPTY;
	}

    @Override
    protected String commonToString()
    {
        return "[" + getDrtTaskType().name() + "]" + super.commonToString();
    }


}
