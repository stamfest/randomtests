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
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * The focus of this test is the maximal excursion (from zero) of the random
 * walk defined by the cumulative sum of adjusted (-1, +1) digits in the
 * sequence. The purpose of the test is to determine whether the cumulative sum
 * of the partial sequences occurring in the tested sequence is too large or too
 * small relative to the expected behaviour of that cumulative sum for random
 * sequences. This cumulative sum may be considered as a random walk. For a
 * random sequence, the excursions of the random walk should be near zero. For
 * certain types of non-random sequences, the excursions of this random walk
 * from zero will be large.
 *
 * @author NIST / ported by Peter Stamfest
 */
public class CumulativeSums extends AbstractNistTest {
    private final static NormalDistribution nd = new NormalDistribution();

    public static class CumulativeSumsResult extends Result {
        private int partialSum;

        public CumulativeSumsResult(double p_value, int partialSum, String description) {
            super(p_value, description);
            this.partialSum = partialSum;
        }

        public int getPartialSum() {
            return partialSum;
        }
    }

    @Override
    public Result[] runTest(Bits b) {
        int n;
        int z;
        int zrev;
        n = b.getLength();
        double sqrtN = Math.sqrt(n);
        int S, sup, inf, k;
        double sum1, sum2;

        z = zrev = 0;
        S = 0;
        sup = 0;
        inf = 0;
        for (k = 0; k < n; k++) {
            if (b.bit(k) != 0) {
                S++;
            } else {
                S--;
            }
            if (S > sup) {
                sup++;
            }
            if (S < inf) {
                inf--;
            }
            z = (sup > -inf) ? sup : -inf;
            zrev = (sup - S > S - inf) ? sup - S : S - inf;
        }

        // forward
        sum1 = 0.0;
        for (k = (-n / z + 1) / 4; k <= (n / z - 1) / 4; k++) {
            sum1 += nd.cumulativeProbability(((4 * k + 1) * z) / sqrtN);
            sum1 -= nd.cumulativeProbability(((4 * k - 1) * z) / sqrtN);
        }
        sum2 = 0.0;
        for (k = (-n / z - 3) / 4; k <= (n / z - 1) / 4; k++) {
            sum2 += nd.cumulativeProbability(((4 * k + 3) * z) / sqrtN);
            sum2 -= nd.cumulativeProbability(((4 * k + 1) * z) / sqrtN);
        }

        double p_value0 = 1.0 - sum1 + sum2;

        // backwards
        sum1 = 0.0;
        for (k = (-n / zrev + 1) / 4; k <= (n / zrev - 1) / 4; k++) {
            sum1 += nd.cumulativeProbability(((4 * k + 1) * zrev) / sqrtN);
            sum1 -= nd.cumulativeProbability(((4 * k - 1) * zrev) / sqrtN);
        }
        sum2 = 0.0;
        for (k = (-n / zrev - 3) / 4; k <= (n / zrev - 1) / 4; k++) {
            sum2 += nd.cumulativeProbability(((4 * k + 3) * zrev) / sqrtN);
            sum2 -= nd.cumulativeProbability(((4 * k + 1) * zrev) / sqrtN);
        }
        double p_value1 = 1.0 - sum1 + sum2;
        return new Result[] { new CumulativeSumsResult(p_value0, z, "forward"), 
                              new CumulativeSumsResult(p_value1, zrev, "backward") };
    }

    @Override
    public void report(PrintWriter out, Result[] results) {
        out.println("CUMULATIVE SUMS (FORWARD) TEST");
        out.println("-------------------------------------------");
        if (results == null) {
            out.println("ERROR: There are no results yet");
            return;
        }
        
        CumulativeSumsResult r = (CumulativeSumsResult) results[0];
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("-------------------------------------------");
        out.printf("(a) The maximum partial sum = %d\n", r.getPartialSum());
        out.println("-------------------------------------------");
        if (r.getPValue() < 0 || r.getPValue() > 1) {
            out.println("WARNING:  P_VALUE IS OUT OF RANGE");
        }

        out.printf("%s\t\tp_value = %f\n\n", 
                   !r.isPassed() ? "FAILURE" : "SUCCESS",
                   r.getPValue());

        r = (CumulativeSumsResult) results[1];

        out.println("CUMULATIVE SUMS (REVERSE) TEST");
        out.println("-------------------------------------------");
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("-------------------------------------------");
        out.printf("(a) The maximum partial sum = %d\n", r.getPartialSum());
        out.println("-------------------------------------------");
        if (r.getPValue() < 0 || r.getPValue() > 1) {
            out.println("WARNING:  P_VALUE IS OUT OF RANGE");
        }

        out.printf("%s\t\tp_value = %f\n\n",
                   !r.isPassed() ? "FAILURE" : "SUCCESS",
                   r.getPValue());
    }
    
    @Override
    public int getInputSizeRecommendation() {
        return 100;
    }

}
