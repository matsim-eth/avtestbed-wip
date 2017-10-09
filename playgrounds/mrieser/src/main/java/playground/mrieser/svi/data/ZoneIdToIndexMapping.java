/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
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

package playground.mrieser.svi.data;

/**
 * @author mrieser
 */
public class ZoneIdToIndexMapping {

	private int nOfZones = 0;
	private String[] array = new String[0];

	public void addMapping(final String zoneId, final int index) {
		if (this.array.length < (index + 1)) {
			String[] tmp = new String[index * 2 + 1];
			System.arraycopy(this.array, 0, tmp, 0, this.array.length);
			this.array = tmp;
		}
		this.array[index] = zoneId;
		if (this.nOfZones < index) {
			this.nOfZones = index;
		}
	}

	public int getNumberOfZones() {
		return this.nOfZones;
	}

	public String[] getIndexToIdMapping() {
		if ((this.array.length + 1) != this.nOfZones) {
			String[] tmp = new String[this.nOfZones + 1];
			System.arraycopy(this.array, 0, tmp, 0, this.nOfZones + 1);
			this.array = tmp;
		}
		return this.array;
	}
}
