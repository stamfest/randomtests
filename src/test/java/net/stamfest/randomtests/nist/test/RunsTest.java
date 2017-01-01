/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.nist.test;

import junit.framework.Assert;
import net.stamfest.randomtests.bits.ArrayBits;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.nist.Runs;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class RunsTest {

    @Test
    public void example235() {
        ArrayBits bits = new ArrayBits(new byte[]{ (byte) 0x9a, (byte) 0xc0 }, 10);

        Runs r = new Runs();

        Result[] results = r.runTest(bits);
        r.report(System.out, results);

        Assert.assertEquals(0.147232, results[0].getPValue(), 0.000001);
    }
}
