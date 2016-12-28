/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.utils;

import java.util.Arrays;
import org.apache.commons.math3.special.Gamma;

/**
 * A histogram classifies values into categories. This implementation classifies
 * numeric values within a range into equally sized bins. Values outside of the
 * value range get classified into the lowest or highest bin, as appropriate.
 *
 * An optional passValue can be set. This value defines a border within the
 * range. After all updates are executed, the corresponding pass count is the
 * number of times the update value has exceeded the pass value.
 *
 * @author Peter Stamfest
 */
public class Histogram {

    private final int binCount;
    private final double min;
    private final double max;
    private final double delta;
    private int bins[];
    private int total = 0;

    private double passValue = 0.0;
    private int passCount = 0;

    private String description = null;
    
    /**
     * Construct a new Histogram
     *
     * @param binCount The number of bins.
     * @param min      The minimum value of the value range.
     * @param max      The maximum value of the value range.
     */
    public Histogram(int binCount, double min, double max) {
        this.binCount = binCount;
        this.min = min;
        this.max = max;
        this.delta = max - min;

        bins = new int[binCount];
        Arrays.fill(bins, 0);

    }

    /**
     * Classify a single value. This increases the hit-count of exactly one bin.
     *
     * @param value The value to classify.
     */
    public void update(double value) {
        // ignore NaN - may be used as an "ignore me" value
        if (Double.isNaN(value)) {
            return;
        }
        int index = (int) (((value - min) / delta) * binCount);
        if (index < 0) {
            index = 0;
        }
        if (index >= binCount) {
            index = binCount - 1;
        }
        synchronized (this) {
            bins[index]++;
            total++;
            if (value > passValue) {
                passCount++;
            }
        }
    }

    /**
     * Returns a string containing the counts of all bins.
     *
     * @param format A printf-style format to format every single bin count.
     *               MUST contain a format usable for integers. If null, a
     *               format of "%3d " is assumed, that is a 3-digit integer
     *               followed by a single blank character for spacing.
     *
     * @return The formatted String.
     */
    public String toString(String format) {
        if (format == null) {
            format = "%3d ";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bins.length; i++) {
            sb.append(String.format(format, bins[i]));
        }

        double uniformity = uniformity();
        if (Double.isNaN(uniformity)) {
            sb.append("   ----   ");
        } else {
            sb.append(String.format(" %8.6f ", uniformity));
        }
        
        if (total == 0) {
            sb.append(String.format(" ------- ", passCount, total));
        } else {
            sb.append(String.format("%4d/%-4d", passCount, total));
        }

        
        return sb.toString();
    }

    /**
     * Get the number of bins.
     *
     * @return The number of bins;
     */
    public int getBinCount() {
        return binCount;
    }

    /**
     * Get the lower value of the range.
     *
     * @return The lower value of the range.
     */
    public double getMin() {
        return min;
    }

    /**
     * Get the higher value of the range.
     *
     * @return The higher value of the range.
     */
    public double getMax() {
        return max;
    }

    /**
     * Get the total number of classified values within the Histogram.
     *
     * @return The total number of classified values within the Histogram.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Returns a probability that the histogram is compatible with a uniform
     * distribution. This is based on a chi-square statistic with an expectation
     * value of total/binCount for every bin.
     *
     * @return The uniformity p-value.
     */
    public double uniformity() {
        if (total == 0) {
            return Double.NaN;
        }
        double chi2 = 0.0;
        double expCount = getTotal() / binCount;
        for (int i = 0; i < binCount; i++) {
            chi2 += Math.pow(bins[i] - expCount, 2) / expCount;
        }

        double uniformity = Gamma.regularizedGammaQ((binCount - 1) / 2.0, chi2 / 2.0);
        return uniformity;
    }

    /**
     * Sets the pass value to compare all updates to.
     *
     * @param passValue The new pass value.
     */
    public void setPassValue(double passValue) {
        this.passValue = passValue;
    }

    /**
     * Gets the current pass value.
     *
     * @return The current pass value.
     */
    public double getPassValue() {
        return passValue;
    }

    /**
     * Returns the current pass count.
     *
     * @return The current pass count.
     */
    public int getPassCount() {
        return passCount;
    }
    
    /**
     * Get the count for a given bin.
     * @param bin The bin number.
     * @return The count for the bin.
     */
    public int getBin(int bin) {
        return bins[bin];
    }

    /**
     * Get a description for the histogram (if set).
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set a new description for the histogram.
     *
     * @param description The description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
