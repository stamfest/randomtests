/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.nist.test;

import junit.framework.Assert;
import net.stamfest.randomtests.bits.ArrayBits;
import net.stamfest.randomtests.nist.BlockFrequency;
import net.stamfest.randomtests.nist.Result;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class BlockFrequencyTest {

    @Test
    public void fifty() {

        ArrayBits fifty_fifty = new ArrayBits(new byte[]{ (byte) 85, (byte) 170, (byte) 85, (byte) 153, (byte) 195, (byte) 60 });
        BlockFrequency f = new BlockFrequency(5);
        Result[] results = f.runTest(fifty_fifty);
        f.report(System.out, results);

        Assert.assertTrue(results[0].isPassed());
    }

    @Test
    public void example2_2_4() {
        // 0110 0110 10
        ArrayBits bits = new ArrayBits(new byte[]{ (byte) 0x66, (byte) 0x80 }, 10);

        BlockFrequency f = new BlockFrequency(3);

        Result[] results = f.runTest(bits);
        f.report(System.out, results);

        Assert.assertEquals(0.801252, results[0].getPValue(), 0.00001);
    }

    @Test
    public void example2_2_8() {
        // 11001001 00001111 11011010 10100010   00100001 01101000 11000010 00110100    11000100 11000110 01100010 10001011 1000    
        ArrayBits bits
                = new ArrayBits(new byte[]{ (byte) 0xc9, (byte) 0x0f, (byte) 0xda, (byte) 0xa2,
                                            (byte) 0x21, (byte) 0x68, (byte) 0xc2, (byte) 0x34,
                                            (byte) 0xc4, (byte) 0xc6, (byte) 0x62, (byte) 0x8b,
                                            (byte) 0x80 }, 100);

        BlockFrequency f = new BlockFrequency(10);
        Result[] results = f.runTest(bits);
        f.report(System.out, results);

        Assert.assertEquals(0.706438, results[0].getPValue(), 0.00001);
    }
}
