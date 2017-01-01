/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.nist.test;

import java.io.IOException;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.nist.NonOverlappingTemplateMatchings;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.utils.IO;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class NonOverlappingTemplateMatchingsTest {

    @Test
    public void test() throws IOException {
        Bits bits = IO.readAscii(NonOverlappingTemplateMatchingsTest.class.getResourceAsStream("/data.e"), 100000);
        NonOverlappingTemplateMatchings n = new NonOverlappingTemplateMatchings(9);
        Result[] results = n.runTest(bits);
        n.report(System.out, results);
        
        /* these values were obtained from the original NIST suite for data.e */
        double p_values[] = new double[] {
            0.362582, 0.284640, 0.293561, 0.876118, 0.881916, 0.101036, 0.390028, 0.303711,
            0.665796, 0.666907, 0.853051, 0.542935, 0.229011, 0.189467, 0.871692, 0.687604,
            0.221109, 0.397855, 0.308412, 0.523625, 0.626066, 0.205118, 0.874027, 0.223216,
            0.183903, 0.685834, 0.565530, 0.606847, 0.727910, 0.710080, 0.485984, 0.946850,
            0.880366, 0.965457, 0.455804, 0.161975, 0.834957, 0.706421, 0.986959, 0.432738,
            0.616595, 0.302839, 0.705469, 0.223583, 0.192914, 0.953679, 0.355950, 0.350645,
            0.108274, 0.765387, 0.328707, 0.921264, 0.456934, 0.052834, 0.097873, 0.888077,
            0.619331, 0.358708, 0.742750, 0.636513, 0.445503, 0.581641, 0.958742, 0.995141,
            0.815856, 0.813427, 0.325298, 0.645559, 0.255446, 0.219017, 0.728199, 0.187691,
            0.151682, 0.412030, 0.362582, 0.975536, 0.902346, 0.308983, 0.258669, 0.739953,
            0.411088, 0.676527, 0.612900, 0.737293, 0.447211, 0.005759, 0.273152, 0.533640,
            0.670461, 0.355320, 0.261877, 0.424258, 0.833187, 0.770848, 0.910626, 0.362698,
            0.671719, 0.729646, 0.177188, 0.519032, 0.110846, 0.252208, 0.842028, 0.103053,
            0.219097, 0.379184, 0.517481, 0.317279, 0.719708, 0.101370, 0.090705, 0.452224,
            0.850020, 0.266792, 0.248649, 0.904761, 0.233107, 0.702756, 0.159796, 0.870547,
            0.648748, 0.302839, 0.112370, 0.958322, 0.177357, 0.336428, 0.107832, 0.459866,
            0.920074, 0.740096, 0.057632, 0.941907, 0.644372, 0.605888, 0.147728, 0.036999,
            0.273866, 0.411904, 0.139917, 0.365258, 0.141055, 0.434032, 0.959988, 0.393074,
            0.638960, 0.757280, 0.863644, 0.412030, };
        
        int i = 0;
        for (Result r : results) {
            Assert.assertEquals(p_values[i++], r.getPValue(), 0.000001);
        }
    }
}
