/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.nist.test;

import java.io.IOException;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.nist.RandomExcursions;
import net.stamfest.randomtests.nist.RandomExcursions.RandomExcursionsResult;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.utils.IO;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class RandomExcursionsTest {

    /*
     * 2.14.8 Example 
     * 
     * (input) ε = "the binary expansion of e up to 1,000,000 bits" 
     * 
     * (input) n = 1000000 = 10^6
     * 
     * (processing) J = 1490 
     * 
     * State=x  χ²       P-value     Conclusion 
     * -4       3.835698 0.573306   Random 
     * -3       7.318707 0.197996   Random
     * -2       7.861927 0.164011   Random
     * -1       15.692617 0.007779  Non-random
     * +1       2.485906 0.778616   Random 
     * +2       5.429381 0.365752   Random
     * +3       2.404171 0.790853   Random
     * +4       2.393928 0.792378   Random
     
    
    BUT the original software yields these results. Because the entire section 2.14 is full of
    calculation errors, I rather go with the output of the software....
    
                RANDOM EXCURSIONS TEST 
                -------------------------------------------- 
                COMPUTATIONAL INFORMATION: 
                -------------------------------------------- 
                (a) Number Of Cycles (J) = 1490 
                (b) Sequence Length (n)  = 1000000 
                (c) Rejection Constraint = 500.000000 
                ------------------------------------------- 
SUCCESS         x = -4 chi^2 =  3.835698 p_value = 0.573306 
SUCCESS         x = -3 chi^2 =  7.318707 p_value = 0.197996 
SUCCESS         x = -2 chi^2 =  7.861927 p_value = 0.164011 
FAILURE         x = -1 chi^2 = 15.692617 p_value = 0.007779 
SUCCESS         x =  1 chi^2 =  2.430872 p_value = 0.786868 
SUCCESS         x =  2 chi^2 =  4.798906 p_value = 0.440912 
SUCCESS         x =  3 chi^2 =  2.357041 p_value = 0.797854 
SUCCESS         x =  4 chi^2 =  2.488767 p_value = 0.778186 

    
    */
    @Test
    public void example2_14_8() throws IOException {
        Bits bits = IO.readAscii(RandomExcursionsTest.class.getResourceAsStream("/data.e"), 1000000);

        RandomExcursions re = new RandomExcursions();
        Result[] results = re.runTest(bits);
        re.report(System.out, results);

        RandomExcursionsResult r;
        
        r = (RandomExcursionsResult) results[0];
        Assert.assertEquals(0.573306, r.getPValue(), 0.000001);
        r = (RandomExcursionsResult) results[1];
        Assert.assertEquals(0.197996, r.getPValue(), 0.000001);
        r = (RandomExcursionsResult) results[2];
        Assert.assertEquals(0.164011, r.getPValue(), 0.000001);
        r = (RandomExcursionsResult) results[3];
        Assert.assertEquals(0.007779, r.getPValue(), 0.000001);
        r = (RandomExcursionsResult) results[4];
        Assert.assertEquals(0.786868, r.getPValue(), 0.000001);
        r = (RandomExcursionsResult) results[5];
        Assert.assertEquals(0.440912, r.getPValue(), 0.000001);
        r = (RandomExcursionsResult) results[6];
        Assert.assertEquals(0.797854, r.getPValue(), 0.000001);
        r = (RandomExcursionsResult) results[7];
        Assert.assertEquals(0.778186, r.getPValue(), 0.000001);
    }
}
