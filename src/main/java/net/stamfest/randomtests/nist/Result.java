/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * (c) 1999 by the National Institute Of Standards & Technology
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.nist;

import net.stamfest.randomtests.Suite;

/**
 * A class to hold a single statistical results. Currently all results carry at
 * least a P-Value, corresponding to the probability that the associated test is
 * compatible with the hypothesis of purely random input data. This value should
 * always be within the range [0..1]
 *
 * @author Peter Stamfest
 */
public class Result {
    private double pValue;
    private String description;
    
    /**
     * Construct a result from a pValue;
     *
     * @param p_value The p-value to set the Result to. This value should always
     *                be within the range [0..1] (This is not enforced,
     *                however.)
     * @param description A result description.
     */
    public Result(double p_value, String description) {
        this.pValue = p_value;
        this.description = description;
    }

    public final boolean isPassed() {
        return getPValue() >= Suite.ALPHA;
    }

    /**
     * Returns the p-value.
     *
     * @return The p-value.
     */
    public double getPValue() {
        return pValue;
    }

    /**
     * Return a description for the result.
     * @return The description.
     */
    public String getDescription() {
        return description;
    }
}
