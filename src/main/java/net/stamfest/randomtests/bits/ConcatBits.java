/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

/**
 * Concatenates multiple bit sequences into a new sequence.
 *
 * @author Peter Stamfest
 */
public final class ConcatBits extends AbstractBaseBits {

    private Bits[] bits;
    private int offsets[];
    private int length;

    /**
     * Construct a new bit sequence from any number of input bit sequences. No
     * data is copied.
     *
     * @param bits An arbitrary number of bit sequences to concatenate.
     */
    public ConcatBits(Bits... bits) {
        this.bits = bits;
        offsets = new int[bits.length];
        int last = 0;
        for (int i = 0; i < bits.length; i++) {
            last += bits[i].getLength();
            offsets[i] = last;
        }
        length = last;
    }
//
//    public ConcatBits(Bits first, Bits... inbits) {
//        
//        bits = new Bits[1 + inbits.length];
//        bits[0] = first;
//        System.arraycopy(inbits, 0, bits, 1, inbits.length);
//        
//        offsets = new int[bits.length];
//        int last = 0;
//        for (int i = 0; i < bits.length; i++) {
//            last += bits[i].getLength();
//            offsets[i] = last;
//        }
//        length = last;
//    }
//   
    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int bit(int i) {
        for (int j = 0; j < bits.length; j++) {
            if (i < offsets[j]) {
                return bits[j].bit(i - ((j > 0) ? offsets[j - 1] : 0));
            }
        }
        throw new IndexOutOfBoundsException();
    }
}
