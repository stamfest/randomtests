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
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.nist.Universal;
import net.stamfest.randomtests.utils.IO;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class UniversalTest {
    @Test
    public void x() throws IOException {
        Bits bits = IO.readAscii(UniversalTest.class.getResourceAsStream("/data.e"), 500000);
        Universal u = new Universal();
        Result[] results = u.runTest(bits);
        u.report(System.out, results);
        
        
        /*
        
        This result was obtained from the original NIST code:
        
                        UNIVERSAL STATISTICAL TEST 
                -------------------------------------------- 
                COMPUTATIONAL INFORMATION: 
                -------------------------------------------- 
                (a) L         = 6 
                (b) Q         = 640 
                (c) K         = 82693 
                (d) sum       = 431541.979721 
                (e) sigma     = 0.003400 
                (f) variance  = 2.954000 
                (g) exp_value = 5.217705 
                (h) phi       = 5.218604 
                (i) WARNING:  2 bits were discarded. 
                ----------------------------------------- 
SUCCESS         p_value = 0.791608 
        
        */
        
        Assert.assertEquals(0.791608, results[0].getPValue(), 0.000001);
    }
    
    @Test
    public void bbs() throws IOException {
        Bits bits = IO.readBinary(UniversalTest.class.getResourceAsStream("/BBS.dat"), 500000);
        
        Assert.assertEquals(500000, bits.getLength());
        
        Universal u = new Universal();
        
 /*       
                        UNIVERSAL STATISTICAL TEST
                --------------------------------------------
                COMPUTATIONAL INFORMATION:
                --------------------------------------------
                (a) L         = 6
                (b) Q         = 640
                (c) K         = 82693
                (d) sum       = 431720.871543
                (e) sigma     = 0.003400
                (f) variance  = 2.954000
                (g) exp_value = 5.217705
                (h) phi       = 5.220767
                (i) WARNING:  2 bits were discarded.
                -----------------------------------------
SUCCESS         p_value = 0.367837

    */   
        Result[] results = u.runTest(bits);
        Universal.UniversalResult r = (Universal.UniversalResult) results[0];
        
        u.report(System.out, results);
        
        Assert.assertEquals(6, r.getL());
        Assert.assertEquals(640, r.getQ());
        Assert.assertEquals(82693, r.getK());
        Assert.assertEquals(0.367837, r.getPValue(), 0.000001);
        Assert.assertEquals(5.217705, r.getExpectedValue(), 0.000001);
    }
}
