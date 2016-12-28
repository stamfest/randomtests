/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

import java.util.Arrays;

/**
 * A bit sequence implementation that allows to change its bits. This might be
 * useful for constructing bit sequences or for using them as efficient boolean
 * arrays. Note that although the bits of the sequence can be changed, its
 * length cannot.
 *
 *
 * @author Peter Stamfest
 */
public class MutableArrayBits extends ArrayBits {

    /**
     * Constructs a mutable bit sequence with a given (fixed) length. NOTE: All
     * bits are undefined. Consider to use {@link #fill(byte) } to initialise
     * them.
     *
     * @param length The length of the sequence to be constructed.
     */
    public MutableArrayBits(int length) {
        this(new byte[length / 8 + 1], length);
    }

    /**
     * Constructs a mutable bit sequence from a given byte array. Note that the
     * passed in array is used by reference, so mutations through the
     * constructed object will change the array.
     *
     * @param array  The array backing the bit sequence.
     * @param length The length of the bit sequence. It must be no larger than 8
     *               * array.length.
     */
    public MutableArrayBits(byte[] array, int length) {
        super(array, length);
    }

    /**
     * Constructs a mutable bit sequence from a given byte array with the
     * "natural" length of the bit sequence, that is 8 * array.length.
     *
     * @param array The array backing the bit sequence.
     */
    public MutableArrayBits(byte[] array) {
        this(array, array.length * 8);
    }

    /**
     * Fill all bytes in the underlying byte array with a constant value. This
     * is probably most useful to initialise the bit sequence to all-zero or
     * all-one. However, every other 8-bit periodic sequence can be created this
     * way as well.
     *
     * @param val The value to fill the array with.
     */
    public void fill(byte val) {
        Arrays.fill(array, val);
    }

    /**
     * Invert a single bit.
     *
     * @param i The index of the bit to invert ("flip").
     * @return The new value of the flipped bit (0 or 1).
     */
    public int flipBit(int i) {
        if (i >= length) {
            throw new IndexOutOfBoundsException();
        }

        int byteIndex = i / 8;
        int bitIndex = 7 - i % 8;  // 0 -> 7, 7 -> 0, 8 -> 7, 15 -> 0

        if (byteIndex >= array.length) {
            throw new IndexOutOfBoundsException();
        }

        array[byteIndex] ^= (1 << bitIndex);

        return ((array[byteIndex] & (1 << bitIndex)) != 0) ? 1 : 0;
    }

    /**
     * Set one bit to a given value.
     *
     * @param i     The index of the bit to set.
     * @param value The new value of the bit.
     * @return The old value of the bit (0 or 1).
     */
    public int setBit(int i, int value) {
        if (i >= length) {
            throw new IndexOutOfBoundsException();
        }

        int byteIndex = i / 8;
        int bitIndex = 7 - i % 8;  // 0 -> 7, 7 -> 0, 8 -> 7, 15 -> 0

        if (byteIndex >= array.length) {
            throw new IndexOutOfBoundsException();
        }
        int old = ((array[byteIndex] & (1 << bitIndex)) != 0) ? 1 : 0;
        if (value != 0) {
            array[byteIndex] |= (1 << bitIndex);
        } else {
            array[byteIndex] &= (byte) ~(1 << bitIndex);
        }
        return old;
    }

    /**
     * Assign a bit sequence from another sequence. This may or may not replace
     * the entire backing array with a new array. So this method does not
     * guarantee that further changes will be reflected in the user-known array
     * used to construct the object initially.
     *
     * @param other The bit sequence to assign this from.
     */
    public void assignFrom(Bits other) {
        if (array.length * 8 < (other.getLength() / 8 + 1)) {
            array = new byte[other.getLength() / 8 + 1];
        }
        length = other.getLength();

        if (other instanceof ArrayBits) {
            int l = length / 8 + ((length % 8 > 0) ? 1 : 0);
            ArrayBits aother = (ArrayBits) other;
            System.arraycopy(aother.array, 0, array, 0, l);
        } else {
            int l = other.getLength();

            for (int i = 0; i < l; i++) {
                setBit(i, other.bit(i));
            }
        }
    }
}
