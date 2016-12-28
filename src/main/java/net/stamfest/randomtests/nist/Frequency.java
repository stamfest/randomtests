/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * (c) 1999 by the National Institute Of Standards & Technology
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.nist;

import java.io.PrintWriter;
import net.stamfest.randomtests.bits.Bits;
import org.apache.commons.math3.special.Erf;

/**
 * The focus of the test is the proportion of zeroes and ones for the entire
 * sequence. The purpose of this test is to determine whether the number of ones
 * and zeros in a sequence are approximately the same as would be expected for a
 * truly random sequence. The test assesses the closeness of the fraction of
 * ones to ½ , that is, the number of ones and zeroes in a sequence should be
 * about the same. All subsequent tests depend on the passing of this test.
 *
 * @author NIST / ported by Peter Stamfest
 */
public class Frequency extends AbstractNistTest {
    public static class FrequencyResult extends Result {
        private int sum;
        private int len;
        
        public FrequencyResult(double p_value, int len, int sum) {
            super(p_value, null);
            this.len = len;
            this.sum = sum;
        }

        public int getSum() {
            return sum;
        }

        public int getLen() {
            return len;
        }
        
    }
    static final double sqrt2 = 1.41421356237309504880;

    @Override
    public Result[] runTest(Bits b) {
        int len = b.getLength();
        int sum = 0;

        for (int i = 0; i < len; i++) {
            sum += 2 * b.bit(i) - 1;
            /* bits become -1 and +1 instead of 0 and 1 
            this means the expected mean value of a purely random sequence of -1,+1 is zero */
        }

        double s_obs = Math.abs(sum) / Math.sqrt(len);
        double f = s_obs / sqrt2;
        // double p_value = Math.erfc(f);
        double p_value = Erf.erfc(f);
        
        FrequencyResult r = new FrequencyResult(p_value, len, sum);
        return new Result[] { r };
    }
    
    @Override
    public void report(PrintWriter out, Result[] results) {
        out.println("FREQUENCY TEST");
        out.println("---------------------------------------------");

        if (results == null) {
            out.println("ERROR: There is no result yet");
            return;
        }

        FrequencyResult r = (FrequencyResult) results[0];
        
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("---------------------------------------------");
        out.printf("(a) The nth partial sum = %d\n", r.getSum());
        out.printf("(b) S_n/n               = %f\n", (double) r.getSum() / r.getLen());
        out.println("--------------------------------------------");

        out.printf("%s\t\tp_value = %f\n\n", 
                   r.isPassed() ? "SUCCESS" : "FAILURE", 
                   r.getPValue());
    }

    @Override
    public int getInputSizeRecommendation() {
        return 100;
    }

}
