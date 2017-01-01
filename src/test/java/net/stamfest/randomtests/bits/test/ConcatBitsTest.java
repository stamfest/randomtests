/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits.test;

import java.io.IOException;
import java.math.BigInteger;
import net.stamfest.randomtests.bits.ArrayBits;
import net.stamfest.randomtests.bits.BigIntegerBits;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.bits.ConcatBits;
import net.stamfest.randomtests.bits.StringBits;
import net.stamfest.randomtests.utils.IO;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class ConcatBitsTest {

    @Test
    public void concat() throws IOException {

        BigIntegerBits a = new BigIntegerBits(new BigInteger("b8", 16));
        BigIntegerBits b = new BigIntegerBits(new BigInteger("af01", 16));
        BigIntegerBits c = new BigIntegerBits(new BigInteger("86", 16));

        ConcatBits cb = new ConcatBits(a, b, c);

        ArrayBits ab = new ArrayBits(new byte[] {(byte) 0xb8, (byte) 0xaf, (byte) 0x01, (byte) 0x86 });
        
        Assert.assertTrue(cb.equals(ab));
        Assert.assertTrue(cb.equals(cb));
        Assert.assertTrue(ab.equals(ab));
        
        Assert.assertEquals(cb, ab);
    }

    @Test
    public void concatShort() throws IOException {

        BigIntegerBits a = new BigIntegerBits(new BigInteger("b8", 16), 7);   // 0111000
        BigIntegerBits b = new BigIntegerBits(new BigInteger("af01", 16), 14);  // 10111100000001
        BigIntegerBits c = new BigIntegerBits(new BigInteger("86", 16), 7);   // 0000110

        ConcatBits cb = new ConcatBits(a, b, c);
        // 0111 0001 0111 1000 0000 1000 0110
        Bits ab = new StringBits("0111 0001 0111 1000 0000 1000 0110");
        // ArrayBits ab = new ArrayBits(new byte[]{ (byte) 0x71, (byte) 0x78, (byte) 0x08, (byte) 0x60 }, 28);

        Assert.assertTrue(cb.equals(ab));
        Assert.assertTrue(cb.equals(cb));
        Assert.assertTrue(ab.equals(ab));

        Assert.assertEquals(cb, ab);
    }

}
