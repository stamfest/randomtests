/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.nist.test;

import java.io.IOException;
import junit.framework.Assert;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.nist.OverlappingTemplateMatching;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.utils.IO;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class OverlappingTemplateMatchingTest {

    @Test
    public void example2_8_8() throws IOException {
        Bits bits = IO.readAscii(OverlappingTemplateMatchingTest.class.getResourceAsStream("/data.e"), 1000000);
        OverlappingTemplateMatching o = new OverlappingTemplateMatching(9);
        Result[] results = o.runTest(bits);
        o.report(System.out, results);

        Assert.assertEquals(0.110434, results[0].getPValue(), 0.000001);
    }
}
