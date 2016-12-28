/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.transform;

import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.bits.ConcatBits;
import net.stamfest.randomtests.bits.ReversedBits;
import net.stamfest.randomtests.bits.SubSequence;
import net.stamfest.randomtests.bits.XORBits;

/**
 * @author Peter Stamfest
 */
public class TransformOps {

    public Bits xor(Bits a, Bits b) {
        if (a == null || b == null) {
            return null;
        }
        return new XORBits(a, b);
    }

    public Bits subSeq(Bits base, int offset, int length, int step) {
        if (base == null) {
            return null;
        }
        return new SubSequence(base, offset, length, step);
    }
    
    public Bits concat(Bits... bits) {
        return new ConcatBits(bits);
    }
    
    public Bits reverse(Bits bits) {
        return new ReversedBits(bits);
    }
}
