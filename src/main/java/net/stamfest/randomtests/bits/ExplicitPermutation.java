/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

/**
 * An explicit Permutation backed by an integer array.
 *
 * @author Peter Stamfest
 */
public class ExplicitPermutation implements Permutation {

    protected int perm[];

    /**
     * Constructs the permutation from an int array. Element i of the array
     * should contain the mapped index for i.
     *
     * @param perm The array of permuted indices.
     */
    public ExplicitPermutation(int perm[]) {
        this.perm = perm;
    }

    @Override
    public int getMappedIndex(int index) {
        return perm[index];
    }

    @Override
    public int getLength() {
        return perm.length;
    }
}
