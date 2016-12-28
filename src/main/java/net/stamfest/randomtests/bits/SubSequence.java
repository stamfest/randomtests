/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

/**
 * A bit sequence representing a sub sequence of bits from another sequence
 * based on offset, length and step-size.
 *
 * The offset defines what bit from the base sequence becomes the first bit
 * (index 0) of the new sequence. Subsequent bits are defined by using every
 * step-size-th bit from the base sequence for a total of length bit.
 *
 * @author Peter Stamfest
 */
public class SubSequence extends AbstractBaseBits {

    private Bits base;
    private int offset;
    private int length;
    private int step;

    /**
     * Constructs a subsequence object using offset, length and step-size.
     *
     * @param base   The base sequence.
     * @param offset The offset within the base sequence. The bit at the index
     *               becomes the bit at position 0 of the subsequence.
     * @param length The maximum length of the subsequence. If this is equal to
     *               or less than 0, the length becomes the maximum possible
     *               length for the given offset and step-size.
     * @param step   Defines the stepping within the base sequence for
     *               subsequent bits.
     * @throws IllegalArgumentException if any argument does not make sense.
     */
    public SubSequence(Bits base, int offset, int length, int step) throws IllegalArgumentException {
        this.base = base;
        this.offset = offset;
        this.length = length;
        this.step = step;

        if (base == null) {
            throw new IllegalArgumentException("missing base sequence");
        }
        if (step <= 0) {
            throw new IllegalArgumentException("step must be larger than 1");
        }
        if (offset >= base.getLength() || offset < 0) {
            throw new IllegalArgumentException("offset out of bounds");
        }
        if (length <= 0 || (offset + (this.length - 1) * step >= base.getLength())) {
            this.length = (base.getLength() - offset - 1) / step + 1; 
        }
    }

    /**
     * Equivalent to
     * {@link #SubSequence(Bits, int, int, int) SubSequence(base, offset, length, 1)}.
     *
     * @param base   The base sequence.
     * @param offset The offset within the base sequence. The bit at the index
     *               becomes the bit at position 0 of the subsequence.
     * @param length The maximum length of the subsequence. If this is equal to
     *               or less than 0, the length becomes the maximum possible
     *               length for the given offset and step-size.
     */
    public SubSequence(Bits base, int offset, int length) {
        this(base, offset, length, 1);
    }

    @Override
    public int bit(int i) {
        if (i > length || i < 0) {
            throw new IndexOutOfBoundsException();
        }
        return base.bit(offset + i * step);
    }

    @Override
    public int getLength() {
        return length;
    }
}
