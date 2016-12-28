/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

/**
 * Transforms a base bit sequence as dictated by a second sequence using an
 * exclusive-or operation. If the second sequence is shorter than the base
 * sequence it effectively gets repeated as often as required in order to match
 * the base sequence length.
 *
 * Bits are taken from the base sequence and XOR-ed with the second sequence to
 * produce the resulting bits.
 * 
 * Example:
 * 
 * Given the base sequence 100101101 and the second sequence 10 the resulting
 * sequence will be 001111000.
 *
 * @author Peter Stamfest
 */
public final class XORBits extends AbstractBaseBits {
    private Bits base;
    private Bits xor;
    private int xorLen;

    /**
     * Create the XORed bit sequence from a base and a (possibly repeated)
     * second bit sequence.
     *
     * @param base The base bit sequence.
     * @param xor  The second bit sequence.
     */
    public XORBits(Bits base, Bits xor) {
        this.base = base;
        this.xor = xor;
        this.xorLen = xor.getLength();
    }
    
    @Override
    public int bit(int i) {
        int x = xor.bit(i % xorLen);
        int b = base.bit(i);
        return x == 0 ? b : 1 - b;
    }

    @Override
    public int getLength() {
        return base.getLength();
    }
}
