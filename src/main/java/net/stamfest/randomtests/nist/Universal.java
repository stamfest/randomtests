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
 * The focus of this test is the number of bits between matching patterns (a
 * measure that is related to the length of a compressed sequence). The purpose
 * of the test is to detect whether or not the sequence can be significantly
 * compressed without loss of information. A significantly compressible sequence
 * is considered to be non-random.
 *
 * @author Peter Stamfest
 */
public class Universal extends AbstractNistTest {
    public static class UniversalResult extends Result {

        public UniversalResult(double p_value) {
            super(p_value, null);
        }

        private int L;
        private int Q;
        private int K;
        private double sigma;
        private double sum;
        private int length;
        private double phi;

        public int getL() {
            return L;
        }

        public int getQ() {
            return Q;
        }

        public int getK() {
            return K;
        }

        public double getSigma() {
            return sigma;
        }

        public double getSum() {
            return sum;
        }

        public double getPhi() {
            return phi;
        }

        public int getDiscarded() {
            return length - (Q + K) * L;
        }

        public double getVariance() {
            return variance[L];
        }

        public double getExpectedValue() {
            return expected_value[L];
        }

        private int getLength() {
            return length;
        }
    }

    private boolean outOfRange;
    private static final double variance[] = { 0, 0, 0, 0, 0, 0, 2.954, 3.125, 3.238, 3.311, 3.356, 3.384,
                                               3.401, 3.410, 3.416, 3.419, 3.421 };

    private static final double expected_value[] = { 0, 0, 0, 0, 0, 0, 5.2177052, 6.1962507, 7.1836656,
                                                     8.1764248, 9.1723243, 10.170032, 11.168765,
                                                     12.168070, 13.167693, 14.167488, 15.167379 };

    @Override
    public Result[] runTest(Bits b) {
        int length = b.getLength();

        int i, j, p;
        double arg, sqrt2, c;
        long T[];
        int decRep;

        /* * * * * * * * * ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         * THE FOLLOWING REDEFINES L, SHOULD THE CONDITION:     n >= 1010*2^L*L       *
         * NOT BE MET, FOR THE BLOCK LENGTH L.                                        *
         * * * * * * * * * * ** * * * * * * * * * * * * * * * * * * * * * * * * * * * */
        int L = 5;
        if (length >= 387840) {
            L = 6;
        }
        if (length >= 904960) {
            L = 7;
        }
        if (length >= 2068480) {
            L = 8;
        }
        if (length >= 4654080) {
            L = 9;
        }
        if (length >= 10342400) {
            L = 10;
        }
        if (length >= 22753280) {
            L = 11;
        }
        if (length >= 49643520) {
            L = 12;
        }
        if (length >= 107560960) {
            L = 13;
        }
        if (length >= 231669760) {
            L = 14;
        }
        if (length >= 496435200) {
            L = 15;
        }
        if (length >= 1059061760) {
            L = 16;
        }

        int Q = 10 * (int) (1 << L);
        int K = (int) (Math.floor(length / L) - (double) Q);
        /* BLOCKS TO TEST */

        p = (int) 1 << L; // pow(2, L);

        T = new long[p];

        outOfRange = false;
        if ((L < 6) || (L > 16) || ((double) Q < 10 * (1 << L))) {
            UniversalResult r = new UniversalResult(Double.NaN);
            r.K = K;
            r.L = L;
            r.Q = Q;
            r.length = length;
            r.phi = Double.NaN;
            r.sigma = Double.NaN;
            r.sum = Double.NaN;

            return new Result[]{ r };
        }

        /* COMPUTE THE EXPECTED:  Formula 16, in Marsaglia's Paper */
        c = 0.7 - 0.8 / (double) L + (4 + 32 / (double) L) * Math.pow(K, -3 / (double) L) / 15;
        double sigma = c * Math.sqrt(variance[L] / (double) K);
        sqrt2 = Math.sqrt(2);
        double log2 = Math.log(2);
        double sum = 0.0;

        for (i = 0; i < p; i++) {
            T[i] = 0;
        }
        for (i = 1; i <= Q; i++) {
            /* INITIALIZE TABLE */
            decRep = 0;
            for (j = 0; j < L; j++) {
                decRep += b.bit((i - 1) * L + j) * (long) Math.pow(2, L - 1 - j);
            }
            T[decRep] = (long) i;
        }
        for (i = Q + 1; i <= Q + K; i++) {
            /* PROCESS BLOCKS */
            decRep = 0;
            for (j = 0; j < L; j++) {
                decRep += b.bit((i - 1) * L + j) * (long) Math.pow(2, L - 1 - j);
            }
            sum += Math.log((double) i - T[decRep]) / log2;
            T[decRep] = i;
        }
        double phi = (double) (sum / (double) K);
        arg = Math.abs(phi - expected_value[L]) / (sqrt2 * sigma);
        
        UniversalResult r = new UniversalResult(Erf.erfc(arg));
        r.K = K;
        r.L = L;
        r.Q = Q;
        r.length = length;
        r.phi = phi;
        r.sigma = sigma;
        r.sum = sum;
        
        return new Result[] { r };
    }

    @Override
    public void report(PrintWriter out, Result[] results) {
        out.println("UNIVERSAL STATISTICAL TEST");
        out.println("---------------------------------------------");
//        if (outOfRange) {
//            out.println("ERROR:  L IS OUT OF RANGE.");
//            out.printf("-OR- :  Q IS LESS THAN %d.\n", 10 * (1 << L));
//            return;
//        }
        if (results == null) {
            out.println("ERROR: There are no results yet");
            out.println("-OR- ERROR:  L IS OUT OF RANGE.");
            out.println("-OR- :  Q IS TOO SMALL\n");
            return;
        }
        
        UniversalResult r = (UniversalResult) results[0];
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("--------------------------------------------");
        out.printf("(a) L         = %d\n", r.getL());
        out.printf("(b) Q         = %d\n", r.getQ());
        out.printf("(c) K         = %d\n", r.getK());
        out.printf("(d) sum       = %f\n", r.getSum());
        out.printf("(e) sigma     = %f\n", r.getSigma());
        out.printf("(f) variance  = %f\n", variance[r.getL()]);
        out.printf("(g) exp_value = %f\n", expected_value[r.getL()]);
        out.printf("(h) phi       = %f\n", r.getPhi());
        out.printf("(i) WARNING:  %d bits were discarded.\n", r.getLength() - (r.getQ() + r.getK()) * r.getL());
        out.println("-----------------------------------------");

        if (r.getPValue() < 0 || r.getPValue() > 1.0) {
            out.println("WARNING:  P_VALUE IS OUT OF RANGE");
        }

        out.printf("%s\t\tp_value = %f\n\n", (!r.isPassed()) ? "FAILURE" : "SUCCESS", r.getPValue());
    }

    @Override
    public int getInputSizeRecommendation() {
        return 387840; // section 2.9.7
    }
}
