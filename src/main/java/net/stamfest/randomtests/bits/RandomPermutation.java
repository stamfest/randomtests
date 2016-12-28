/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

import java.util.Random;

/**
 * A permutation initialized from a @see {@link java.util.Random} based random
 * number generator. The permutation will be initialized only once upon
 * construction and will be fixed after wards.
 *
 * @author Peter Stamfest
 */
public class RandomPermutation extends ExplicitPermutation {

    /**
     * Construct a random permutation initialized from a random number
     * generator. For long permutations, the algorithmic complexity of
     * initialization is O(n^2) where n is the length.
     *
     * @param rng    The random number generator to use.
     * @param length The length of the permutation.
     */
    public RandomPermutation(Random rng, int length) {
        super(new int[length]);

        boolean used[] = new boolean[length];
        for (int i = 0; i < length; i++) {
            used[i] = false;
        }
        int l = 0;
        for (int i = 0; i < length; i++) {
            l += rng.nextInt(length);
            l = l % length;

            while (used[l]) {
                l++;
                if (l >= length) {
                    l = 0;
                }
            }
            used[l] = true;
            perm[i] = l;
        }
    }
}
