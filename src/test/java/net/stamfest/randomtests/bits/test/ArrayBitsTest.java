/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.bits.test;

import junit.framework.Assert;
import net.stamfest.randomtests.bits.ArrayBits;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class ArrayBitsTest {
    @Test
    public void bits1() {
        ArrayBits bits = new ArrayBits(new byte[] { (byte) 0x89 });
        Assert.assertEquals(1, bits.bit(0));
        Assert.assertEquals(0, bits.bit(1));
        Assert.assertEquals(0, bits.bit(2));
        Assert.assertEquals(0, bits.bit(3));
        Assert.assertEquals(1, bits.bit(4));
        Assert.assertEquals(0, bits.bit(5));
        Assert.assertEquals(0, bits.bit(6));
        Assert.assertEquals(1, bits.bit(7));
    }
    
    @Test
    public void iteratorTest() {
        ArrayBits bits = new ArrayBits(new byte[] { (byte) 0x89 });
        int n = 0;
        for (Integer i : bits) {
            Assert.assertEquals(i, (Integer) bits.bit(n++));
        }
        Assert.assertEquals(8, n);
    }
    
    @Test 
    public void javadocTest() {
        StringBuilder sb = new StringBuilder();
        ArrayBits bits = new ArrayBits(new byte[]{ (byte) 0x49, (byte) 0xa3 }, 15);
        for (Integer bit : bits) {
            sb.append(bit).append(' ');
        }
        
        Assert.assertEquals("0 1 0 0 1 0 0 1 1 0 1 0 0 0 1 ", sb.toString());

    }
}
