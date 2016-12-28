/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

/**
 * A Bits implementation efficiently keeping data stored in a byte array. Every
 * bit occupies just one "real" bit. Bits are stored in a way that they can be
 * easily read from left to right when printed in binary or hex:
 *
 * The byte at position 0 in the underlying byte array hold bits 0 to 7 of the
 * sequence, where bit 0 is mapped to the highest bit and bit 7 is mapped to the
 * lowest bit. The second byte holds bits 8 - 15 and so on.
 *
 * This means that
 *
 * <pre>
 * {@code
 * ArrayBits bits = new ArrayBits(new byte[] { (byte) 0x49, (byte) 0xa3 }, 15);
 * for (Integer bit: bits) {
 *     System.out.printf("%d ", bit);
 * }
 * }
 * </pre>
 *
 * will print "0 1 0 0 1 0 0 1 1 0 1 0 0 0 1"
 *
 * @author Peter Stamfest
 */
public class ArrayBits extends AbstractBaseBits {

    protected byte[] array = null;
    protected int length = 0;

    protected ArrayBits() {
    }

    /**
     * Constructs a bit sequence with a length of bytes.length * 8.
     *
     * @param bytes The bytes making up the bit sequence.
     */
    public ArrayBits(byte[] bytes) {
        array = bytes;
        length = array.length * 8;
    }

    /**
     * Constructs a bit sequence with an explicitly given length.
     *
     * @param bytes The bytes making up the bit sequence.
     * @param len   The length of the bit sequence. This must be less than 8 *
     *              bytes.length.
     */
    public ArrayBits(byte[] bytes, int len) {
        this(bytes);
        if (len > length) {
            throw new IndexOutOfBoundsException();
        }
        length = len;
    }
    
    /**
     * Turn any Bits object into a byte array based bit sequence. This copies
     * all the bits into a newly allocated byte array, so any changes to the
     * input bit sequence afterwards are not reflected in this object.
     *
     * @param bits The input bit sequence.
     */
    public ArrayBits(Bits bits) {
        array = new byte[(bits.getLength() + 7) / 8];
        length = bits.getLength();

        byte b = 0;
        int n = 0;
        int j = 0;
        
        for (int bit : bits) {
            b = (byte) ((b << 1) | (byte) bit);
            n++;
            if (n % 8 == 0) {
                array[j++] = b;
                b = 0;
            }
        }
        if (n % 8 != 0) {
            b = (byte) (b << (8 - n % 8));
            array[j++] = b;
        }
    }

    @Override
    public int bit(int i) {
        if (i >= length) {
            throw new IndexOutOfBoundsException();
        }

        int byteIndex = i / 8;
        int bitIndex = 7 - i % 8;  // 0 -> 7, 7 -> 0, 8 -> 7, 15 -> 0

        if (byteIndex >= array.length) {
            throw new IndexOutOfBoundsException();
        }
        return ((array[byteIndex] & (1 << bitIndex)) != 0) ? 1 : 0;
    }

    @Override
    public int getLength() {
        return length;
    }

    protected final void setArray(byte array[], int len) {
        this.array = array;
        this.length = len;
    }

    @Override
    public String toString() {
        return "ArrayBits[len=" + length + "]";
    }
}
