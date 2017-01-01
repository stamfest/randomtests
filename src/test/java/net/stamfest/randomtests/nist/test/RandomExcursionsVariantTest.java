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
import net.stamfest.randomtests.nist.RandomExcursionsVariant;
import net.stamfest.randomtests.nist.RandomExcursionsVariant.RandomExcursionsVariantResult;
import net.stamfest.randomtests.utils.IO;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class RandomExcursionsVariantTest {

    /*
     *
     Example 2.15.8 
    
    (input) ε = "the binary expansion of e up to 1,000,000 bits" 
    (input) n = 1000000 = 10^6 
    (processing) J = 1490 
    
    State( x ) Counts  P-value Conclusion 
    -9 1450 0.858946 Random 
    -8 1435 0.794755 Random
    -7 1380 0.576249 Random
    -6 1366 0.493417 Random
    -5 1412 0.633873 Random
    -4 1475 0.917283 Random
    -3 1480 0.934708 Random
    -2 1468 0.816012 Random 
    -1 1502 0.826009 Random 
    +1 1409 0.137861 Random 
    +2 1369 0.200642 Random 
    +3 1396 0.441254 Random 
    +4 1479 0.939291 Random 
    +5 1599 0.505683 Random 
    +6 1628 0.445935 Random 
    +7 1619 0.512207 Random 
    +8 1620 0.538635 Random
    +9 1610 0.593930 Random
     */
    @Test
    public void example2_15_8() throws IOException {
        Bits bits = IO.readAscii(RandomExcursionsVariantTest.class.getResourceAsStream("/data.e"), 1000000);

        RandomExcursionsVariant re = new RandomExcursionsVariant();
        RandomExcursionsVariantResult[] results = (RandomExcursionsVariantResult[]) re.runTest(bits);
        re.report(System.out, results);
       
        Assert.assertEquals("-9", 0.858946, results[0].getPValue(), 0.000001);
        Assert.assertEquals("-8", 0.794755, results[1].getPValue(), 0.000001);
        Assert.assertEquals("-7", 0.576249, results[2].getPValue(), 0.000001);
        Assert.assertEquals("-6", 0.493417, results[3].getPValue(), 0.000001);
        Assert.assertEquals("-5", 0.633873, results[4].getPValue(), 0.000001);
        Assert.assertEquals("-4", 0.917283, results[5].getPValue(), 0.000001);
        Assert.assertEquals("-3", 0.934708, results[6].getPValue(), 0.000001);
        Assert.assertEquals("-2", 0.816012, results[7].getPValue(), 0.000001);
        Assert.assertEquals("-1", 0.826009, results[8].getPValue(), 0.000001);
        Assert.assertEquals("+1", 0.137861, results[9].getPValue(), 0.000001);
        Assert.assertEquals("+2", 0.200642, results[10].getPValue(), 0.000001);
        Assert.assertEquals("+3", 0.441254, results[11].getPValue(), 0.000001);
        Assert.assertEquals("+4", 0.939291, results[12].getPValue(), 0.000001);
        Assert.assertEquals("+5", 0.505683, results[13].getPValue(), 0.000001);
        Assert.assertEquals("+6", 0.445935, results[14].getPValue(), 0.000001);
        Assert.assertEquals("+7", 0.512207, results[15].getPValue(), 0.000001);
        Assert.assertEquals("+8", 0.538635, results[16].getPValue(), 0.000001);
        Assert.assertEquals("+9", 0.593930, results[17].getPValue(), 0.000001);
    }
}
