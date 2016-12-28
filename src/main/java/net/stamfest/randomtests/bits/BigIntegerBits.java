/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

import java.math.BigInteger;

/**
 * A bit sequence backed by a {@link java.math.BigInteger} object. An explicit
 * length may be specified upon construction, to be able to compensate for
 * leading zero bits. Note that the bit index 0 of the bit sequence corresponds
 * to the most significant 1-bit of the underlying BigInteger (in the case that
 * the bit sequence has the "natural" length of the BigInteger, that is the
 * value returned by {@link BigInteger#bitLength()}). If the length of the bit
 * sequence is larger than the natural length, the missing bits are inserted in
 * the beginning of the sequence. If the length of the bit sequence is smaller
 * that the natural length, leading bits are cut off.
 *
 * Examples:
 * 
 * <pre>
 * {@code
 * new BigIntegerBits(new BigInteger("b8", 16)); // 10111000
 * new BigIntegerBits(new BigInteger("b8", 16), 7); // 0111000
 * new BigIntegerBits(new BigInteger("b8", 16), 9); // 010111000
 * new BigIntegerBits(new BigInteger("38", 16)); // 111000
 * }
 * </pre>
 * 
 * @author Peter Stamfest
 */
public class BigIntegerBits extends AbstractBaseBits {

    private final BigInteger bi;
    private final int length;
    private final int bilength;

    /**
     * Construct a bit sequence from a BigInteger. The length of the sequence is
     * set from {@link BigInteger#bitLength()}.
     *
     * @param bi The BigInteger. The result is undefined for negative integers.
     */
    public BigIntegerBits(BigInteger bi) {
        this(bi, bi.bitLength());
    }

    /**
     * Construct a bit sequence from a BigInteger. The length of the sequence is
     * set explicitly. If the given length is larger than
     * {@link BigInteger#bitLength()} then the sequence is filled with the
     * necessary number of 0 bits in the front. If it is smaller, than some bits
     * are cut off of the beginning of the sequence.
     *
     * @param bi     The BigInteger. The result is undefined for negative
     *               integers.
     * @param length The length of the new bit sequence. Bits are inserted or
     *               cut off at the beginning of the "natural" bit sequence.
     */
    public BigIntegerBits(BigInteger bi, int length) {
        this.bi = bi;
        this.length = length;
        this.bilength = bi.bitLength();
    }

    /**
     * Turn an ordinary long value into a bit sequence.
     *
     * @param l      The long value to turn into a bit sequence.
     * @param length The requested length of the new sequence.
     */
    public BigIntegerBits(long l, int length) {
        this(BigInteger.valueOf(l), length);
    }
    
    @Override
    public int bit(int i) {
        if (i < (length - bilength)) {
            return 0;
        }
        return bi.testBit(length - i - 1) ? 1 : 0;
    }

    @Override
    public int getLength() {
        return length;
    }

    /**
     * A utility function to obtain a BigInteger from any bit sequence. The
     * returned number will always be positive. The first bit of the bit
     * sequence is used as the MSB of the integer number.
     *
     * @param source The bit sequence to convert into a BigInteger
     * @return The BigInteger derived from the sequence.
     */
    public static BigInteger getBigInteger(Bits source) {
        if (source instanceof BigIntegerBits) {
            BigIntegerBits other = (BigIntegerBits) source;
            if (other.bi.signum() > 0) {
                return other.bi;
            }
        } else if (source instanceof ArrayBits) {
            ArrayBits other = (ArrayBits) source;
            if (other.getLength() % 8 == 0) {
                return new BigInteger(1, other.array);
            }
        }
        
        /*
        Turn the bit sequence into a byte array. We have to make sure
        that the LSB of the BigInteger is bit source.bit(source.getLength()-1).
        For this, we have to shift the entire sequence "right". n is just this 
        number of bits we have to shift.
         */
        int l = source.getLength();
        byte[] array = new byte[(l + 7) / 8];

        int n = l % 8;
        if (n != 0) {
            n = 8 - n;
        }

        for (Integer b : source) {
            if (b == 1) {
                int bitNr = 7 - n % 8;
                int byteNr = n / 8;
                array[byteNr] |= 1 << bitNr;
            }
            n++;
        }
        return new BigInteger(1, array);
    }
}
