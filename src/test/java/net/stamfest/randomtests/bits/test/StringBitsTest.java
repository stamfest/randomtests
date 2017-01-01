/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.bits.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import junit.framework.Assert;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.bits.PermutedBits;
import net.stamfest.randomtests.bits.RandomPermutation;
import net.stamfest.randomtests.bits.StringBits;
import net.stamfest.randomtests.utils.IO;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class StringBitsTest {
    @Test
    public void one() {
        StringBits s = new StringBits("1000");
        Assert.assertEquals(4, s.getLength());
        Assert.assertEquals(1, s.bit(0));
        Assert.assertEquals(0, s.bit(1));
        Assert.assertEquals(0, s.bit(2));
        Assert.assertEquals(0, s.bit(3));
    }
    @Test
    public void two() {
        StringBits s = new StringBits("10x00");
        Assert.assertEquals(4, s.getLength());
        Assert.assertEquals(1, s.bit(0));
        Assert.assertEquals(0, s.bit(1));
        Assert.assertEquals(0, s.bit(2));
        Assert.assertEquals(0, s.bit(3));
    }

    @Test
    public void permute() throws IOException {
        Random rng = new Random(0x12345678);
        StringBits s = new StringBits("10001010");
        PermutedBits p = new PermutedBits(s, new RandomPermutation(rng, s.getLength()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IO.writeAscii(baos, p);
        
        Assert.assertEquals("00100011", baos.toString());
        System.out.println();
    }
    
    @Test
    public void io() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream("1001010111".getBytes());
        Bits bits = IO.readAscii(bais, Integer.MAX_VALUE);
        
        Assert.assertEquals(10, bits.getLength());
        Assert.assertEquals(1, bits.bit(0));
        Assert.assertEquals(0, bits.bit(1));
        Assert.assertEquals(0, bits.bit(2));
        Assert.assertEquals(1, bits.bit(3));
        Assert.assertEquals(0, bits.bit(4));
        Assert.assertEquals(1, bits.bit(5));
        Assert.assertEquals(0, bits.bit(6));
        Assert.assertEquals(1, bits.bit(7));
        Assert.assertEquals(1, bits.bit(8));
        Assert.assertEquals(1, bits.bit(9));
    }
}
