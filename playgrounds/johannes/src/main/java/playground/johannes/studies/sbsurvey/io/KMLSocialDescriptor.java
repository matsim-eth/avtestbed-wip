/* *********************************************************************** *
 * project: org.matsim.*
 * KMLSocialDescriptor.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
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
package playground.johannes.studies.sbsurvey.io;

import net.opengis.kml._2.PlacemarkType;
import org.matsim.contrib.socnetgen.sna.graph.social.SocialSparseVertex;
import org.matsim.contrib.socnetgen.sna.snowball.SampledVertex;
import org.matsim.contrib.socnetgen.sna.snowball.SampledVertexDecorator;

/**
 * @author illenberger
 *
 */
public class KMLSocialDescriptor extends KMLSnowballDescriptor {

	@Override
	public void addDetail(PlacemarkType kmlPlacemark, Object object) {
		super.addDetail(kmlPlacemark, object);
		
		StringBuilder builder = new StringBuilder(kmlPlacemark.getDescription());
		builder.append("<br>Seed: ");
		SampledVertexDecorator<SocialSparseVertex> seed = (SampledVertexDecorator<SocialSparseVertex>) ((SampledVertex)object).getSeed(); 
		builder.append(seed.getDelegate().getPerson().toString());
		
		kmlPlacemark.setDescription(builder.toString());
	}

}
