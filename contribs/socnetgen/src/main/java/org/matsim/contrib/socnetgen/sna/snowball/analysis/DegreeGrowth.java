/* *********************************************************************** *
 * project: org.matsim.*
 * DegreeGroth.java
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
package org.matsim.contrib.socnetgen.sna.snowball.analysis;

import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.map.hash.TDoubleDoubleHashMap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.matsim.contrib.common.stats.Discretizer;
import org.matsim.contrib.common.stats.Histogram;
import org.matsim.contrib.common.stats.LinearDiscretizer;
import org.matsim.contrib.socnetgen.sna.snowball.SampledVertexDecorator;
import org.matsim.contrib.socnetgen.sna.snowball.sim.Sampler;
import org.matsim.contrib.socnetgen.sna.snowball.sim.SamplerListener;

/**
 * @author illenberger
 */
public class DegreeGrowth implements SamplerListener {

    private int lastIteration = 0;

    private DescriptiveStatistics prevStats;

    private Discretizer discretizer = new LinearDiscretizer(1.0);

    private String output;

    private int prevN = 0;

    private TreeMap<Integer, TDoubleDoubleHashMap> growthTable = new TreeMap<Integer, TDoubleDoubleHashMap>();

    public DegreeGrowth(String output) {
        this.output = output;
    }

    @Override
    public boolean beforeSampling(Sampler<?, ?, ?> sampler, SampledVertexDecorator<?> vertex) {
        if (sampler.getIteration() > lastIteration) {
            dump(sampler);
            lastIteration = sampler.getIteration();
        }

        return true;

    }

    @Override
    public boolean afterSampling(Sampler<?, ?, ?> sampler, SampledVertexDecorator<?> vertex) {
        return true;
    }

    @Override
    public void endSampling(Sampler<?, ?, ?> sampler) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(output + "k_growth-table.txt"));

            double[] ks = growthTable.get(growthTable.lastKey()).keys();
            Arrays.sort(ks);
            for (double k : ks) {
                writer.write("\t");
                writer.write(String.valueOf(k));
            }
            writer.newLine();

            for (Entry<Integer, TDoubleDoubleHashMap> entry : growthTable.entrySet()) {
                writer.write(entry.getKey().toString());

                for (double k : ks) {
                    writer.write("\t");
                    double val = entry.getValue().get(k);
                    if (val == 0)
                        writer.write("NA");
                    else
                        writer.write(String.valueOf(val));
                }
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dump(Sampler<?, ?, ?> sampler) {
        DescriptiveStatistics stats = ObservedDegree.getInstance().statistics(sampler.getSampledGraph().getVertices());
        if (prevStats != null) {
            TDoubleDoubleHashMap hist = Histogram.createHistogram(stats, discretizer, false);
            TDoubleDoubleHashMap prevHist = Histogram.createHistogram(prevStats, discretizer, false);

            TDoubleDoubleHashMap growth = new TDoubleDoubleHashMap();
            TDoubleDoubleIterator it = hist.iterator();
            for (int i = 0; i < hist.size(); i++) {
                it.advance();

                double g = it.value() / prevHist.get(it.key());
                if (Double.isInfinite(g))
                    g = 0.0;
                growth.put(it.key(), g);
            }

            growthTable.put(sampler.getIteration() - 1, growth);
        }

        prevStats = stats;
    }
}
