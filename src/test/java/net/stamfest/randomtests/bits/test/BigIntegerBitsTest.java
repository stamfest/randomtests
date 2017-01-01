/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits.test;

import java.math.BigInteger;
import java.util.Iterator;
import junit.framework.Assert;
import net.stamfest.randomtests.bits.AbstractBaseBits;
import net.stamfest.randomtests.bits.ArrayBits;
import net.stamfest.randomtests.bits.BigIntegerBits;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.bits.StringBits;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class BigIntegerBitsTest {

    @Test
    public void test1() {
        BigInteger bi = new BigInteger("123456789abcdef", 16);
        BigIntegerBits bits = new BigIntegerBits(bi);

        Assert.assertEquals(4 * 15 - 3, bits.getLength());

        Assert.assertEquals("100100011010001010110011110001001101010111100110111101111",
                            StringBits.toString(bits));
    }

    @Test
    public void lengths() {
        BigInteger bi = new BigInteger("a", 16);
        BigIntegerBits bits = new BigIntegerBits(bi);

        Assert.assertEquals(4, bits.getLength());
        Assert.assertEquals("1010", StringBits.toString(bits));

        bits = new BigIntegerBits(bi, 5);
        Assert.assertEquals(5, bits.getLength());
        Assert.assertEquals("01010", StringBits.toString(bits));

        bits = new BigIntegerBits(bi, 3);
        Assert.assertEquals(3, bits.getLength());
        Assert.assertEquals("010", StringBits.toString(bits));
    }
    
    @Test
    public void docExamples() {
        Assert.assertEquals(new BigIntegerBits(new BigInteger("b8", 16)), new StringBits("10111000"));
        Assert.assertEquals(new BigIntegerBits(new BigInteger("b8", 16), 7), new StringBits("0111000"));
        Assert.assertEquals(new BigIntegerBits(new BigInteger("b8", 16), 9), new StringBits("010111000"));
        Assert.assertEquals(new BigIntegerBits(new BigInteger("38", 16)), new StringBits("111000"));
    }
    
    static class WrappedBits extends AbstractBaseBits {
        Bits source;

        public WrappedBits(Bits source) {
            this.source = source;
        }

        @Override
        public int bit(int i) {
            return source.bit(i);
        }

        @Override
        public int getLength() {
            return source.getLength();
        }
    }

    @Test
    public void roundtrip1() {
        BigInteger bi = new BigInteger("6a", 16);
        BigIntegerBits bits = new BigIntegerBits(bi);
        Assert.assertEquals(7, bits.getLength());

        BigInteger bi2 = BigIntegerBits.getBigInteger(bits);

        Assert.assertEquals(bi, bi2);
        bi2 = BigIntegerBits.getBigInteger(new WrappedBits(bits));

        Assert.assertEquals(bi, bi2);
    }

    @Test
    public void roundtrip2() {
        BigInteger bi = new BigInteger("6a7bf18ae5f10f45a8", 16);
        BigIntegerBits bits = new BigIntegerBits(bi);

        Assert.assertEquals(18 * 4 - 1, bits.getLength());

        BigInteger bi2 = BigIntegerBits.getBigInteger(bits);

        Assert.assertEquals(bi, bi2);
        bi2 = BigIntegerBits.getBigInteger(new WrappedBits(bits));

        Assert.assertEquals(bi, bi2);
    }

    @Test
    public void roundtrip3() {
        BigInteger bi = new BigInteger("17bf18ae5f10f45a8", 16);
        BigIntegerBits bits = new BigIntegerBits(bi);

        Assert.assertEquals(17 * 4 - 3, bits.getLength());

        BigInteger bi2 = BigIntegerBits.getBigInteger(bits);

        Assert.assertEquals(bi, bi2);

        bi2 = BigIntegerBits.getBigInteger(new WrappedBits(bits));

        Assert.assertEquals(bi, bi2);
    }

    @Test
    public void roundtrip4() {
        BigInteger bi = new BigInteger("17bf18ae5f10f45a8", 16);
        BigIntegerBits bits = new BigIntegerBits(bi, 20 * 4 + 3);

        Assert.assertEquals(20 * 4 + 3, bits.getLength());

        BigInteger bi2 = BigIntegerBits.getBigInteger(bits);        // direct access to internal BigInteger

        Assert.assertEquals(bi, bi2);

        bi2 = BigIntegerBits.getBigInteger(new WrappedBits(bits)); // explicit bit copying

        Assert.assertEquals(bi, bi2);
    }
    
    @Test
    public void array() {
        BigInteger bi = new BigInteger("17bf18ae5f10f45a8", 16);

        byte[] bytes = new byte[]{ (byte) 0x01, (byte) 0x7b, (byte) 0xf1,
                                   (byte) 0x8a, (byte) 0xe5, (byte) 0xf1,
                                   (byte) 0x0f, (byte) 0x45, (byte) 0xa8 };
        ArrayBits bits = new ArrayBits(bytes);

        Assert.assertEquals(9*8, bits.getLength());

        BigInteger bi2 = BigIntegerBits.getBigInteger(bits);        // direct access to internal array

        Assert.assertEquals(bi, bi2);

        bi2 = BigIntegerBits.getBigInteger(new WrappedBits(bits)); // explicit bit copying

        Assert.assertEquals(bi, bi2);
    }

}
