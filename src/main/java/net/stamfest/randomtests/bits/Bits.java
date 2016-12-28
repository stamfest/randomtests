/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

import java.util.Iterator;

/**
 * A sequence of bits. A standard bit sequence is immutable. Implementations may
 * add facilities to make bits mutable or they may add any other required
 * functionality.
 *
 * Bit values are reported as 0 or 1 to simplify their numeric use. Reporting
 * them as boolean values would make this use rather hard.
 *
 * Note that Bits implement @see {@link Iterable} over Integer objects so one
 * can write
 *
 * <pre>
 * {@code
 * Bits bits;
 * for (Integer bit: bits) {
 *     // refer to bit
 * }
 * }
 * </pre>
 *
 *
 * @author Peter Stamfest
 */
public interface Bits extends Iterable<Integer> {

    /**
     * Return the bit at position i. Bit positions are counted from index zero
     * up to the length of the sequence minus one.
     *
     * @param i The bit index to retrieve.
     * @return The bit as an integer value. The value returned by
     *         implementations MUST be either 0 or 1. If this contract is
     *         broken, users will likely be confused and results based on
     *         failing bits will likely be wrong.
     * @throws RuntimeException Access to bits beyond the length of the sequence
     *                          may throw a runtime exception.
     */
    public int bit(int i);

    /**
     * Get the length of the sequence. Usually, bit sequences are expected to
     * never change their length.
     *
     * @return The length of the bit sequence.
     */
    public int getLength();

    /**
     * Returns an iterator to sequentially walk the bits of the sequence. Note
     * that for performance reasons the iterator caches the length of the
     * sequence upon its construction, so changes in length after creating an
     * iterator may cause runtime exceptions.
     *
     * @return The iterator
     */
    @Override
    public default Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int n = 0;
            private int len = getLength();

            @Override
            public boolean hasNext() {
                return n < len;
            }

            @Override
            public Integer next() {
                return bit(n++);
            }
        };
    }

    /**
     * Return new XORBits(this, b). {@link XORBits}.
     *
     * @param b The sequence to XOR this sequence with. If it is shorter than
     *          this sequence, it gets repeated as often as required to match
     *          the length.
     * @return The XORed sequence (a new object).
     */
    public default Bits xor(Bits b) {
        return new XORBits(this, b);
    }

    /**
     * Returns a new bit sequence which consists of this sequence concatenated
     * with one or more other bit sequences.
     *
     * @param bits The sequence(s) to concatenate.
     * @return A newly constructed bit sequence representing the concatenation
     *         of this sequence and the ones passed.
     */
    public default Bits concat(Bits... bits) {
        Bits cpy[] = new Bits[bits.length + 1];
        cpy[0] = this;
        System.arraycopy(bits, 0, cpy, 1, bits.length);
        return new ConcatBits(cpy);
    }

    /**
     * Returns a new bit sequence with the bits in the reverse order than this
     * one.
     *
     * @return The reversed bit sequence.
     */
    public default Bits reverse() {
        return new ReversedBits(this);
    }

    /**
     * Returns a subsequence of the bits from this bit sequence.
     *
     * @param offset The offset within the base sequence. The bit at the index
     *               becomes the bit at position 0 of the subsequence.
     * @param length The maximum length of the subsequence. If this is equal to
     *               or less than 0, the length becomes the maximum possible
     *               length for the given offset and step-size.
     * @param step   Defines the stepping within the base sequence for
     *               subsequent bits.
     * @return The subsequence.
     */
    public default Bits subSeq(int offset, int length, int step) {
        return new SubSequence(this, offset, length, step);
    }
}
