/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

/**
 * Represents a permutation of the sequence of natural numbers (including 0).
 * The @getMappedIndex method must return the permuted index for every natural
 * number starting at 0 up to the length of the permutation. A proper
 * permutation yields a different mapped index for every input number, that is
 * from i != j follows getMappedIndex(i) != getMappedIndex(j). Also, a proper
 * permutation only returns mapped indices from the range 0 up to the length of
 * the permutation minus 1.
 *
 * It is assumed that the mapped index never changes for any given input index.
 *
 * @author Peter Stamfest
 */
public interface Permutation {

    /**
     * Return the mapped index for the given index of a permutation.
     * 
     * @param index The index to return the mapped index for.
     * @return The mapped index.
     */
    int getMappedIndex(int index);

    /**
     * Returns the length of the permutation. A permutation may yield wrong
     * results for getMappedIndex with an index greater than or equal to its
     * length.
     * 
     * @return The length of the permutation.
     */
    int getLength();
}
