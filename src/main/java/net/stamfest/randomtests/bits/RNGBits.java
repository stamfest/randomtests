/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

import java.util.Random;

/**
 * A bit sequence initialised by a @see {@link java.util.Random}-based random
 * number generator. Note that the bit sequence is only initialised once upon
 * construction. After construction, the bits from the sequence will never
 * change.
 *
 * @author Peter Stamfest
 */
public class RNGBits extends ArrayBits {

    /**
     * Constructs a bit sequence initialised from a random-number-generator.
     * Note that this constructor will use the {@link Random#nextBytes(byte[])}
     * method to obtain the minimum number of bytes needed to define the bit
     * sequence. If the requested length is not a multiple of 8, some bits from
     * the very last byte obtained will be lost.
     *
     * @param rng The random number generator to use.
     * @param length The length of the bit sequence requested.
     */
    public RNGBits(Random rng, int length) {
        super();
        byte r[] = new byte[(length + 7) / 8];
        rng.nextBytes(r);
        setArray(r, length);
    }
}
