/* *********************************************************************** *
 * project: org.matsim.*
 * TrajectoryAnalyzerTask.java
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
package playground.johannes.gsv.synPop.analysis;

import gnu.trove.map.hash.TDoubleDoubleHashMap;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.matsim.contrib.common.stats.Discretizer;
import org.matsim.contrib.common.stats.FixedSampleSizeDiscretizer;
import org.matsim.contrib.common.stats.Histogram;
import org.matsim.contrib.common.stats.StatsWriter;
import playground.johannes.synpop.data.Person;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * @author illenberger
 */
public abstract class AnalyzerTask {

    private static final Logger logger = Logger.getLogger(AnalyzerTask.class);

    private static boolean overwrite = false;

    private static int overwriteBins;

    private static int overwriteMinsize;

    public static void overwriteStratification(int bins, int minsize) {
        overwrite = true;
        overwriteBins = bins;
        overwriteMinsize = minsize;
    }

    private String output;

    public void setOutputDirectory(String outputDir) {
        this.output = outputDir;
    }

    public String getOutputDirectory() {
        return output;
    }

    protected boolean outputDirectoryNotNull() {
        if (getOutputDirectory() == null) {
            logger.warn("No output directory specified.");
            return false;
        } else {
            return true;
        }
    }

    public abstract void analyze(Collection<? extends Person> persons, Map<String, DescriptiveStatistics> results);

    protected void writeHistograms(DescriptiveStatistics stats, String name, int bins, int minsize) throws IOException {
        double[] values = stats.getValues();
        if (values.length > 0) {
            if (overwrite) {
                logger.warn("Overwriting stratification!");
                bins = overwriteBins;
                minsize = overwriteMinsize;
            }

            TDoubleDoubleHashMap hist = Histogram.createHistogram(stats, FixedSampleSizeDiscretizer.create(values, minsize, bins), true);
            Histogram.normalize(hist);
            StatsWriter.writeHistogram(hist, name, "p", String.format("%1$s/%2$s.strat.txt", getOutputDirectory(), name));
        } else {
            logger.debug(String.format("Cannot create histogram: No samples for %s.", name));
        }
    }

    protected void writeHistograms(DescriptiveStatistics stats, Discretizer discretizer, String name, boolean reweight) throws IOException {
        TDoubleDoubleHashMap hist = Histogram.createHistogram(stats, discretizer, reweight);
        StatsWriter.writeHistogram(hist, name, "n", String.format("%1$s/%2$s.txt", output, name));
        Histogram.normalize(hist);
        StatsWriter.writeHistogram(hist, name, "p", String.format("%1$s/%2$s.share.txt", output, name));
    }
}
