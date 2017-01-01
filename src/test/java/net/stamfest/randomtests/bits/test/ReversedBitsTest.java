/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits.test;

import java.security.NoSuchAlgorithmException;
import java.util.Random;
import junit.framework.Assert;
import net.stamfest.randomtests.bits.RNGBits;
import net.stamfest.randomtests.bits.ReversedBits;
import net.stamfest.randomtests.bits.StringBits;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class ReversedBitsTest {
    @Test
    public void reversed1() {
        Assert.assertEquals(new ReversedBits(new StringBits("1000")), new StringBits("0001"));
    }

    @Test
    public void reversed2() {
        Assert.assertEquals(new ReversedBits(new StringBits("1000110101")), new StringBits("1010110001"));
    }

    @Test
    public void reversed3() {
        Assert.assertEquals(new ReversedBits(new StringBits("00001")), new StringBits("10000"));
    }

        @Test
    public void doubleReverse() throws NoSuchAlgorithmException {
        RNGBits rng = new RNGBits(new Random(), 812);
        Assert.assertEquals(new ReversedBits(new ReversedBits(rng)), rng);
    }

}
