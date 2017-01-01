/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.nist.test;

import junit.framework.Assert;
import net.stamfest.randomtests.bits.StringBits;
import net.stamfest.randomtests.nist.CumulativeSums;
import net.stamfest.randomtests.nist.Result;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class CumulativeSumsTest {

    /*
    
    (input) ε = 11001001000011111101101010100010001000010110100011 
                00001000110100110001001100011001100010100010111000 
    
    (input) n = 100 
    
    (processing) z = 1.6 (forward) || z = 1.9 (reverse) 
    
    (output)   P-value = 0.219194 (forward) || P-value = 0.114866 (reverse) 

    */
    @Test
    public void example2_13_8() {
        StringBits bits = new StringBits("1100100100001111110110101010001000100001011010001100001000110100110001001100011001100010100010111000");
        CumulativeSums cs = new CumulativeSums();
        Result[] results = cs.runTest(bits);
        cs.report(System.out, results);

        Assert.assertEquals("forward", 0.219194, results[0].getPValue(), 0.000001);
        Assert.assertEquals("forward", 0.114866, results[1].getPValue(), 0.000001);
    }
}
