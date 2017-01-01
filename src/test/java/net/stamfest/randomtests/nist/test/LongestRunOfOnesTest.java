/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.nist.test;

import junit.framework.Assert;
import net.stamfest.randomtests.bits.StringBits;
import net.stamfest.randomtests.nist.LongestRunOfOnes;
import net.stamfest.randomtests.nist.Result;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class LongestRunOfOnesTest {
    @Test
    public void one() {
        StringBits bits = new StringBits("11001100000101010110110001001100111000000000001001001101010100010001001111010110100000001101011111001100111001101101100010110010");
        LongestRunOfOnes l = new LongestRunOfOnes();
        Result[] results = l.runTest(bits);
        l.report(System.out, results);
        
        Assert.assertEquals(0.180609, results[0].getPValue(), 0.000001);
    }
}
