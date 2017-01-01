/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.nist.test;

import junit.framework.Assert;
import net.stamfest.randomtests.bits.StringBits;
import net.stamfest.randomtests.nist.ApproximateEntropy;
import net.stamfest.randomtests.nist.Result;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class ApproximateEntropyTest {
    @Test
    public void example2_12_8() {
        StringBits sb = new StringBits("1100100100001111110110101010001000100001011010001100001000110100110001001100011001100010100010111000");
        
        ApproximateEntropy ae = new ApproximateEntropy(2);
        Result[] results = ae.runTest(sb);
        ae.report(System.out, results);
        Assert.assertEquals(0.235301, results[0].getPValue(), 0.000001);
    }
}
