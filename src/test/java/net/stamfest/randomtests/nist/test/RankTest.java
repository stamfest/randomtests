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
import net.stamfest.randomtests.nist.Rank;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.utils.IO;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class RankTest {
    @Test
    public void example258() throws IOException {
        Bits b = IO.readAscii(RankTest.class.getResourceAsStream("/data.e"), 100000);
        
        Rank r = new Rank();
        Result[] results = r.runTest(b);
        r.report(System.out, results);
        
        Assert.assertEquals(0.532069, results[0].getPValue(), 0.00001);
    }
}
