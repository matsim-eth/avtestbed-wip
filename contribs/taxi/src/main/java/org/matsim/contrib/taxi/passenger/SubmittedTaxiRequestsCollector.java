/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
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

package org.matsim.contrib.taxi.passenger;

import java.util.*;

import org.matsim.api.core.v01.Id;
import org.matsim.contrib.dvrp.data.Request;
import org.matsim.contrib.taxi.data.TaxiRequest;


public class SubmittedTaxiRequestsCollector
{
    private final Map<Id<Request>, TaxiRequest> requests = new LinkedHashMap<>();


    public Map<Id<Request>, ? extends TaxiRequest> getRequests()
    {
        return Collections.unmodifiableMap(requests);
    }


    //to be used by TaxiRequestCreator
    void addRequest(TaxiRequest request)
    {
        requests.put(request.getId(), request);
    }


    public void reset()
    {
        requests.clear();
    }
}
