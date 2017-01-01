/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.nist.test;

import java.io.IOException;
import junit.framework.Assert;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.nist.LinearComplexity;
import net.stamfest.randomtests.nist.LinearComplexity.LinearComplexityResult;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.utils.IO;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class LinearComplexityTest {

    /**
     * Test if the example from section 2.10.8 of the NIST paper can be
     * reproduced
     *
     * @throws IOException
     */
    @Test
    public void example2_10_8() throws IOException {
        Bits bits = IO.readAscii(LinearComplexityTest.class.getResourceAsStream("/data.e"), 1000000);
        LinearComplexity lc = new LinearComplexity(1000);
        Result[] results = lc.runTest(bits);
        lc.report(System.out, results);

        Assert.assertEquals(1, results.length);

        LinearComplexityResult r = (LinearComplexityResult) results[0];

        Assert.assertEquals(0.845406, r.getPValue(), 0.000001);
        Assert.assertEquals("chi2", 2.700348, r.getChi2(), 0.000001);
        Assert.assertEquals("nu[0]", 11, r.getNu(0));
        Assert.assertEquals("nu[1]", 31, r.getNu(1));
        Assert.assertEquals("nu[2]", 116, r.getNu(2));
        Assert.assertEquals("nu[3]", 501, r.getNu(3));
        Assert.assertEquals("nu[4]", 258, r.getNu(4));
        Assert.assertEquals("nu[5]", 57, r.getNu(5));
        Assert.assertEquals("nu[6]", 26, r.getNu(6));

        Assert.assertEquals("discarded bits", 0, r.getDiscarded());

    }
}
