/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

/**
 * A bit sequence reflecting the permuted bits of another bit sequence.
 *
 * @author Peter Stamfest
 */
public class PermutedBits extends AbstractBaseBits {

    private Bits base;
    private Permutation perm;

    /**
     * Constructs a permuted bit sequence. This requires both a bit sequence and
     * a permutation. The permutation must be of the same length of the bit
     * sequence or shorter. In the latter case, the bit sequence will only have
     * the length of the permutation.
     *
     * @param base The base bit sequence.
     * @param perm The permutation to permute the bit sequence with.
     */
    public PermutedBits(Bits base, Permutation perm) {
        if (perm.getLength() > base.getLength()) {
            throw new IllegalArgumentException("The permutation may not be longer than the bit sequence to be permuted.");
        }

        this.base = base;
        this.perm = perm;
    }

    @Override
    public int bit(int i) {
        return base.bit(perm.getMappedIndex(i));
    }

    @Override
    public int getLength() {
        return Math.min(base.getLength(), perm.getLength());
    }
}
