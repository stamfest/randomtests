/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.bits.test;

import junit.framework.Assert;
import net.stamfest.randomtests.bits.MutableArrayBits;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class MutableArrayBitsTest {

    private MutableArrayBits b;
    @Before
    public void setup() {
        byte t[] = new byte[] { (byte) 0x55, (byte) 0xaa, (byte) 0x99, (byte) 0x66 };
        b = new MutableArrayBits(t);
    }
    
    @Test
    public void setupOK() {
        int i = 0;
        Assert.assertEquals(0, b.bit(i++)); // 0
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        
        Assert.assertEquals(1, b.bit(i++)); // 8
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        
        Assert.assertEquals(1, b.bit(i++)); // 16
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        
        Assert.assertEquals(0, b.bit(i++)); //24
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        
        Assert.assertEquals(32, i);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBound() {
        b.bit(32);
    }
       
    
    @Test
    public void setBits() {
        b.setBit(16, 0);
        b.setBit(19, 0);

        b.setBit(0, 1);
        b.setBit(3, 5);
        b.setBit(5, 0);
        b.setBit(27, 4);
        
        int i = 0;
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        
        Assert.assertEquals(0, b.bit(i++)); // 16
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        
        Assert.assertEquals(0, b.bit(i++)); // 24
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        
        Assert.assertEquals(32, i);
    }

    @Test
    public void flipBits() {
        b.flipBit(16);
        b.flipBit(19);

        b.flipBit(0);
        b.flipBit(3);
        b.flipBit(5);
        b.flipBit(27);
        
        int i = 0;
        Assert.assertEquals(1, b.bit(i++)); // 0
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        
        Assert.assertEquals(1, b.bit(i++)); // 8
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        
        Assert.assertEquals(0, b.bit(i++)); // 16
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        
        Assert.assertEquals(0, b.bit(i++)); //24
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(1, b.bit(i++));
        Assert.assertEquals(0, b.bit(i++));
        
        Assert.assertEquals(32, i);
    }

    /**
     * check if filling with all ones works as expected due to possible
     * signedness issues of bytes
     */
    @Test
    public void allOnes()  {
        b.fill((byte) 0xff);
        
        for (int i = 0; i < 32; i++) {
            Assert.assertEquals(1, b.bit(i));
        }
    }
    
}
