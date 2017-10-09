/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
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

package playground.agarwalamit.analysis.emission;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.VehicleEntersTrafficEvent;
import org.matsim.api.core.v01.events.VehicleLeavesTrafficEvent;
import org.matsim.api.core.v01.events.handler.VehicleEntersTrafficEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleLeavesTrafficEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.emissions.events.ColdEmissionEvent;
import org.matsim.contrib.emissions.events.ColdEmissionEventHandler;
import org.matsim.contrib.emissions.events.WarmEmissionEvent;
import org.matsim.contrib.emissions.events.WarmEmissionEventHandler;
import org.matsim.contrib.emissions.types.ColdPollutant;
import org.matsim.contrib.emissions.types.WarmPollutant;
import org.matsim.core.events.algorithms.Vehicle2DriverEventHandler;
import org.matsim.vehicles.Vehicle;

/**
 * EmissionEvents only have vehicle id, which looks fine, however, to collect emissions for every person, one need
 * a connector, which is only available via normal events file. Thus, both emission and normal event files are required.
 *
 * Created by amit on 23/12/2016.
 */

public class EmissionPersonEventHandler implements WarmEmissionEventHandler, ColdEmissionEventHandler, VehicleLeavesTrafficEventHandler, VehicleEntersTrafficEventHandler {

    private final Vehicle2DriverEventHandler delegate = new Vehicle2DriverEventHandler();

    private final Map<Id<Person>, Map<ColdPollutant, Double>> personId2ColdEmissions = new HashMap<>();
    private final Map<Id<Vehicle>, Map<ColdPollutant, Double>> vehicleId2ColdEmissions = new HashMap<>();
    private final Map<Id<Person>, Map<WarmPollutant, Double>> personId2WarmEmissions = new HashMap<>();
    private final Map<Id<Vehicle>, Map<WarmPollutant, Double>> vehicleId2WarmEmissions = new HashMap<>();

    @Override
    public void handleEvent(WarmEmissionEvent event) {
//        Id<Person> driverId = delegate.getDriverOfVehicle(event.getVehicleId());
        //TODO : following would be gone as soon as I will be able to fix the reading of two events file.
        Id<Person> driverId ;
        String vehicleIdString = event.getVehicleId().toString();

        if( vehicleIdString.endsWith("motorbike") ){
            int lastIndex = vehicleIdString.indexOf("_motorbike");
            driverId = Id.createPersonId(vehicleIdString.substring(0, lastIndex));
        } else if ( vehicleIdString.endsWith("bike") ){
            int lastIndex = vehicleIdString.indexOf("_bike");
            driverId = Id.createPersonId(vehicleIdString.substring(0, lastIndex));
        } else if ( vehicleIdString.endsWith("truck") ){
            int lastIndex = vehicleIdString.indexOf("_truck");
            driverId = Id.createPersonId(vehicleIdString.substring(0, lastIndex));
        } else {
            driverId = Id.createPersonId(vehicleIdString);
        }

        {
            Map<WarmPollutant, Double> warmEmissions = this.personId2WarmEmissions.get(driverId);

            if (warmEmissions == null ) {
                this.personId2WarmEmissions.put(driverId, event.getWarmEmissions());
            } else {
                this.personId2WarmEmissions.put(driverId,
                        Arrays.stream(WarmPollutant.values()).collect(
                                Collectors.toMap(wp -> wp,
                                        wp -> warmEmissions.get( wp ) + event.getWarmEmissions().get( wp ) ))
                );
            }
        }

        {
            Map<WarmPollutant, Double> warmEmissions = this.vehicleId2WarmEmissions.get(event.getVehicleId());

            if (warmEmissions == null ) {
                this.vehicleId2WarmEmissions.put(event.getVehicleId(), event.getWarmEmissions());
            } else {
                this.vehicleId2WarmEmissions.put(event.getVehicleId(),
                        Arrays.stream(WarmPollutant.values()).collect(
                                Collectors.toMap(wp -> wp,
                                        wp -> warmEmissions.get( wp ) + event.getWarmEmissions().get( wp ) ))
                );
            }
        }
    }

    @Override
    public void handleEvent(ColdEmissionEvent event) {
//        Id<Person> driverId = delegate.getDriverOfVehicle(event.getVehicleId());
        //TODO : following would be gone as soon as I will be able to fix the reading of two events file.
        Id<Person> driverId ;
        String vehicleIdString = event.getVehicleId().toString();

        if( vehicleIdString.endsWith("motorbike") ){
            int lastIndex = vehicleIdString.indexOf("_motorbike");
            driverId = Id.createPersonId(vehicleIdString.substring(0, lastIndex));
        } else if ( vehicleIdString.endsWith("bike") ){
            int lastIndex = vehicleIdString.indexOf("_bike");
            driverId = Id.createPersonId(vehicleIdString.substring(0, lastIndex));
        } else if ( vehicleIdString.endsWith("truck") ){
            int lastIndex = vehicleIdString.indexOf("_truck");
            driverId = Id.createPersonId(vehicleIdString.substring(0, lastIndex));
        } else {
            driverId = Id.createPersonId(vehicleIdString);
        }

        {
            Map<ColdPollutant, Double> coldEmissions = this.personId2ColdEmissions.get(driverId);

            if (coldEmissions == null ) {
                this.personId2ColdEmissions.put(driverId, event.getColdEmissions());
            } else {
                this.personId2ColdEmissions.put(driverId,
                        Arrays.stream(ColdPollutant.values()).collect(
                                Collectors.toMap(cp -> cp,
                                        cp -> coldEmissions.get( cp ) + event.getColdEmissions().get( cp ) ))
                );
            }
        }

        {
            Map<ColdPollutant, Double> coldEmissions = this.vehicleId2ColdEmissions.get(event.getVehicleId());

            if (coldEmissions == null ) {
                this.vehicleId2ColdEmissions.put(event.getVehicleId(), event.getColdEmissions());
            } else {
                this.vehicleId2ColdEmissions.put(event.getVehicleId(),
                        Arrays.stream(ColdPollutant.values()).collect(
                                Collectors.toMap(cp -> cp,
                                        cp -> coldEmissions.get( cp ) + event.getColdEmissions().get( cp ) ))
                );
            }
        }

    }

    @Override
    public void reset(int iteration) {
        this.personId2WarmEmissions.clear();
        this.personId2ColdEmissions.clear();
        this.vehicleId2ColdEmissions.clear();
        this.vehicleId2WarmEmissions.clear();
        this.delegate.reset(iteration);
    }

    @Override
    public void handleEvent(VehicleEntersTrafficEvent event) {
        delegate.handleEvent(event);
    }

    @Override
    public void handleEvent(VehicleLeavesTrafficEvent event) {
        delegate.handleEvent(event);
    }

    public Id<Person> getDriverOfVehicle(Id<Vehicle> vehicleId) {
        return delegate.getDriverOfVehicle(vehicleId);
    }

    public Map<Id<Person>, Map<ColdPollutant, Double>> getPersonId2ColdEmissions() {
        return personId2ColdEmissions;
    }

    public Map<Id<Person>, Map<WarmPollutant, Double>> getPersonId2WarmEmissions() {
        return personId2WarmEmissions;
    }

    public Map<Id<Vehicle>, Map<ColdPollutant, Double>> getVehicleId2ColdEmissions() {
        return vehicleId2ColdEmissions;
    }

    public Map<Id<Vehicle>, Map<WarmPollutant, Double>> getVehicleId2WarmEmissions() {
        return vehicleId2WarmEmissions;
    }
}
