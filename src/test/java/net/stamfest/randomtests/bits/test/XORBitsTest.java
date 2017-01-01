/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits.test;

import junit.framework.Assert;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.bits.StringBits;
import net.stamfest.randomtests.bits.XORBits;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class XORBitsTest {
    @Test
    public void xor2() {
        Bits base = new StringBits("1001010010010110");
        Assert.assertEquals(16, base.getLength());

        Bits xor = new StringBits("10");
        Assert.assertEquals(2, xor.getLength());

        Bits x = new XORBits(base, xor);
        Assert.assertEquals(16, x.getLength());

        Bits expected = new StringBits("0011111000111100");

        int l = base.getLength();
        for (int i = 0; i < l ; i++) {
            Assert.assertEquals(expected.bit(i), x.bit(i));
        }
    }
    @Test
    
    public void xor3() {
        Bits base = new StringBits("1001010010010110");
        Assert.assertEquals(16, base.getLength());

        Bits xor = new StringBits("101");
        Assert.assertEquals(3, xor.getLength());

        Bits x = new XORBits(base, xor);
        Assert.assertEquals(16, x.getLength());

        Bits expected = new StringBits("0010001001001101");

        int l = base.getLength();
        for (int i = 0; i < l ; i++) {
            Assert.assertEquals(expected.bit(i), x.bit(i));
        }
    }
}
