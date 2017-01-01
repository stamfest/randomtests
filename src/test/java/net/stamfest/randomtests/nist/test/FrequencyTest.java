/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.nist.test;

import net.stamfest.randomtests.bits.ArrayBits;
import net.stamfest.randomtests.nist.Frequency;
import net.stamfest.randomtests.nist.Result;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */public class FrequencyTest {
    @Test
    public void fifty() {
        ArrayBits fifty_fifty = new ArrayBits(new byte[]{ (byte) 85, (byte) 170, (byte) 85, (byte) 153, (byte) 195, (byte) 60 });

        Frequency f = new Frequency();
        Result[] results = f.runTest(fifty_fifty);
        f.report(System.out, results);
        Assert.assertTrue(results[0].isPassed());
    }

    @Test
    public void one() {
        ArrayBits data = new ArrayBits(new byte[] { (byte) 0x01, 0 });
        Frequency f = new Frequency();
        Result[] results = f.runTest(data);
        f.report(System.out, results);
        Assert.assertFalse(results[0].isPassed());
    }
    
    
    @Test
    public void two() {
        ArrayBits data = new ArrayBits(new byte[] { (byte) 0x01, 0 });
        Frequency f = new Frequency();
        Result[] results = f.runTest(data);
        f.report(System.out, results);
        Assert.assertFalse(results[0].isPassed());
    }

    @Test
    public void three() {
        ArrayBits data = new ArrayBits(new byte[] { (byte) 0x01 });
        Frequency f = new Frequency();
        Result[] results = f.runTest(data);
        f.report(System.out, results);
        Assert.assertTrue(results[0].isPassed());
    }
    
}
