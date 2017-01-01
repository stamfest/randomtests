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
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.nist.Serial;
import net.stamfest.randomtests.utils.IO;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class SerialTest {

    /**
     * Example 2.11.8 from the NIST paper.
     * 
     * input ε = ,000,000 bits from the binary expansion of e 
     * @throws IOException 
     */
    @Test
    public void example() throws IOException {
        Bits bits = IO.readAscii(SerialTest.class.getResourceAsStream("/data.e"), 1000000);
        Serial s = new Serial(2);
        Result[] results = s.runTest(bits);
        s.report(System.out, results);
        
        Result s1 = results[0];
        Result s2 = results[1];
        
        Assert.assertEquals("p_value1", 0.843764, s1.getPValue(), 0.000001);
        Assert.assertEquals("p_value2", 0.561915, s2.getPValue(), 0.000001);
    }
}
