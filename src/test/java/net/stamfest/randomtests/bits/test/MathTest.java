/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.bits.test;

import junit.framework.Assert;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class MathTest {
    @Test
    public void normal() {
        NormalDistribution nd = new NormalDistribution();
                
        Assert.assertEquals(0.42074, nd.cumulativeProbability(-0.2), 0.000001);
        Assert.assertEquals(0.5, nd.cumulativeProbability(0.0), 0.000001);
        Assert.assertEquals(0.57926, nd.cumulativeProbability(0.2), 0.000001);
    }
}
