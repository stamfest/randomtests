/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.nist.test;

import java.io.IOException;
import junit.framework.Assert;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.bits.StringBits;
import net.stamfest.randomtests.nist.DiscreteFourierTransform;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.utils.IO;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class DiscreteFourierTest {

    @Test
    public void dft() throws IOException {
        Bits b = IO.readAscii(DiscreteFourierTest.class.getResourceAsStream("/data.e"), 100000);
        DiscreteFourierTransform dft = new DiscreteFourierTransform();
        Result[] results = dft.runTest(b);
        dft.report(System.out, results);
    }
    
    /* NOTE: the data given in example 2.6.8 are WRONG. The original NIST code also gives the data used in this test... */
    @Test
    public void example_268() {
        Bits b = new StringBits("1100100100001111110110101010001000100001011010001100001000110100110001001100011001100010100010111000");
        Assert.assertEquals(100, b.getLength());
        DiscreteFourierTransform dft = new DiscreteFourierTransform();
        Result[] results = dft.runTest(b);
        dft.report(System.out, results);
        
        Assert.assertEquals(0.646355, results[0].getPValue(), 0.000001);
    }

}
