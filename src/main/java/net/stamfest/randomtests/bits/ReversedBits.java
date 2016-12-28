/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

/**
 * A reversed bit sequence.
 *
 * <pre>
 * {@code
 * new ReversedBits(new StringBits("1000")); // 0001
 * }
 * </pre>
 *
 * @author Peter Stamfest
 */
public final class ReversedBits extends AbstractBaseBits {

    private final Bits base;

    /**
     * Construct a new bit sequence the reverses a base sequence.
     *
     * @param base The base sequence to reverse.
     */
    public ReversedBits(Bits base) {
        this.base = base;
    }

    @Override
    public int bit(int i) {
        return base.bit(base.getLength() - i - 1);
    }

    @Override
    public int getLength() {
        return base.getLength();
    }
}
