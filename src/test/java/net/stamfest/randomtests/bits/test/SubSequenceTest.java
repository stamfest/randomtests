/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits.test;

import junit.framework.Assert;
import net.stamfest.randomtests.bits.StringBits;
import net.stamfest.randomtests.bits.SubSequence;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class SubSequenceTest {

    StringBits bits;

    @Before
    public void setup() {
        bits = new StringBits("01010010 01100100 01001001 00100010 10010010 10110101 0010");   // 52 bits
    }

    @Test
    public void origlen() {
        Assert.assertEquals(52, bits.getLength());

    }

    @Test
    public void t1() {
        SubSequence sub = new SubSequence(bits, 6, 5);      //  10011
        Assert.assertEquals(5, sub.getLength());

        Assert.assertEquals(1, sub.bit(0));
        Assert.assertEquals(0, sub.bit(1));
        Assert.assertEquals(0, sub.bit(2));
        Assert.assertEquals(1, sub.bit(3));
        Assert.assertEquals(1, sub.bit(4));
    }

    @Test
    public void t2() {
        SubSequence sub = new SubSequence(bits, 50, 5);      //  10, length 2
        Assert.assertEquals(2, sub.getLength());

        Assert.assertEquals(1, sub.bit(0));
        Assert.assertEquals(0, sub.bit(1));
    }

    @Test
    public void t3() {
        SubSequence sub = new SubSequence(bits, 3, 3, 8);      //  100, length 3
        Assert.assertEquals(3, sub.getLength());

        Assert.assertEquals(1, sub.bit(0));
        Assert.assertEquals(0, sub.bit(1));
        Assert.assertEquals(0, sub.bit(2));
    }

    @Test
    public void t4() {
        SubSequence sub = new SubSequence(bits, 3, 10, 8);      //  1000111, length 7
        Assert.assertEquals(7, sub.getLength());

        int i = 0;
        Assert.assertEquals(1, sub.bit(i++));
        Assert.assertEquals(0, sub.bit(i++));
        Assert.assertEquals(0, sub.bit(i++));
        Assert.assertEquals(0, sub.bit(i++));
        Assert.assertEquals(1, sub.bit(i++));
        Assert.assertEquals(1, sub.bit(i++));
        Assert.assertEquals(0, sub.bit(i++));
    }

}
