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
 * The focus of this test is the total number of runs in the sequence, where a
 * run is an uninterrupted sequence of identical bits. A run of length k
 * consists of exactly k identical bits and is bounded before and after with a
 * bit of the opposite value. The purpose of the runs test is to determine
 * whether the number of runs of ones and zeros of various lengths is as
 * expected for a random sequence. In particular, this test determines whether
 * the oscillation between such zeros and ones is too fast or too slow.
 *
 * @author Peter Stamfest
 */
public class Runs extends AbstractNistTest {

    public static class RunsResult extends Result {

        private double pi;
        private int V;
        private double erfc_arg;

        public RunsResult(double p_value) {
            super(p_value, null);
        }

        public double getPi() {
            return pi;
        }

        public int getV() {
            return V;
        }

        public double getErfc_arg() {
            return erfc_arg;
        }
    }
//   private boolean estimatorCriteriaMet;

    @Override
    public Result[] runTest(Bits b) {
        double pi;
        int V;
        double erfc_arg;

        int length = b.getLength();
        int S, k;

        S = 0;
        for (k = 0; k < length; k++) {
            if (b.bit(k) != 0) {
                S++;
            }
        }
        pi = (double) S / (double) length;

        if (Math.abs(pi - 0.5) > (2.0 / Math.sqrt(length))) {
//            estimatorCriteriaMet = false;
            return null;
        } else {
//            estimatorCriteriaMet = true;
            V = 1;
            for (k = 1; k < length; k++) {
                if (b.bit(k) != b.bit(k - 1)) {
                    V++;
                }
            }

            erfc_arg = Math.abs((double) V - 2.0 * length * pi * (1 - pi)) / (2.0 * pi * (1 - pi) * Math.sqrt(2 * length));

            RunsResult r = new RunsResult(Erf.erfc(erfc_arg));
            r.V = V;
            r.erfc_arg = erfc_arg;
            r.pi = pi;

            return new Result[]{ r };
        }
    }

    @Override
    public void report(PrintWriter out, Result[] results) {
//        if (!estimatorCriteriaMet) {
//            out.println("RUNS TEST");
//            out.println("------------------------------------------");
//            out.printf("PI ESTIMATOR CRITERIA NOT MET! PI = %f\n", pi);
//        } else {
        out.println("RUNS TEST");
        out.println("------------------------------------------");
        if (results == null) {
            out.println("ERROR: There is no result yet");
            out.println("- OR - PI ESTIMATOR CRITERIA NOT MET!");
            return;
        }

        RunsResult r = (RunsResult) results[0];
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("------------------------------------------");
        out.printf("(a) Pi                        = %f\n", r.getPi());
        out.printf("(b) V_n_obs (Total # of runs) = %d\n", r.getV());
        out.println("(c) V_n_obs - 2 n pi (1-pi)");
        out.printf("    -----------------------   = %f\n", r.getErfc_arg());
        out.println("      2 sqrt(2n) pi (1-pi)");
        out.println("------------------------------------------");
        if (r.getPValue() < 0 || r.getPValue() > 1) {
            out.println("WARNING:  P_VALUE IS OUT OF RANGE.\n");
        }

        out.printf("%s p_value = %f\n\n",
                   !r.isPassed() ? "FAILURE" : "SUCCESS",
                   r.getPValue());
    }
//    }

    @Override
    public int getInputSizeRecommendation() {
        return 100;
    }
}
