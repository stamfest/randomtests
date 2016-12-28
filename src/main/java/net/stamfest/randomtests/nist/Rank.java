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
import net.stamfest.randomtests.nist.utils.IntMatrix;

/**
 * The focus of the test is the rank of disjoint sub-matrices of the entire
 * sequence. The purpose of this test is to check for linear dependence among
 * fixed length substrings of the original sequence. Note that this test also
 * appears in the DIEHARD battery of tests.
 *
 * @author Peter Stamfest
 */
public class Rank extends AbstractNistTest {
    public static class RankResult extends Result {
        private double p_32;
        private double p_31;
        private double p_30;
        private int F_32;
        private int F_31;
        private int F_30;
        private int length;
        private double chi_squared;

        public RankResult(double p_value) {
            super(p_value, null);
        }

        public double getP_32() {
            return p_32;
        }

        public double getP_31() {
            return p_31;
        }

        public double getP_30() {
            return p_30;
        }

        public int getF_32() {
            return F_32;
        }

        public int getF_31() {
            return F_31;
        }

        public int getF_30() {
            return F_30;
        }

        public int getLength() {
            return length;
        }

        public double getChi_squared() {
            return chi_squared;
        }

    }


    @Override
    public Result[] runTest(Bits b) {
        double chi_squared;
        double p_32;
        double p_31;
        double p_30;
        int F_32;
        int F_31;
        int F_30;
    
        int length = b.getLength();
        int N, i, k, r;
        double product, arg1, R;

        boolean insufficientBits = false;

        if (length < 32 * 32) {
            insufficientBits = true;
            return null;
        }

        IntMatrix matrix = new IntMatrix(32, 32);

        N = length / (32 * 32);
        r = 32;
        
        /* COMPUTE PROBABILITIES */
        product = 1;
        for (i = 0; i <= r - 1; i++) {
            //                        product *= ((1.e0- pow(2, i-32) )*(1.e0-pow(2, i-32)))/(1.e0-pow(2, i-r));
            product *= ((1.e0 - Math.pow(2.0, i - 32.0)) * (1.e0 - Math.pow(2.0, i - 32.0))) / (1.e0 - Math.pow(2.0, (double) i - r));
        }
        p_32 = Math.pow(2.0, (double) r * (32 + 32 - r) - 32 * 32) * product;

        r = 31;
        product = 1;
        for (i = 0; i <= r - 1; i++) {
            product *= ((1.e0 - Math.pow(2.0, i - 32.0)) * (1.e0 - Math.pow(2.0, i - 32.0))) / (1.e0 - Math.pow(2.0, (double) i - r));
        }
        p_31 = Math.pow(2.0, (double) r * (32 + 32 - r) - 32 * 32) * product;

        p_30 = 1 - (p_32 + p_31);

        F_32 = 0;
        F_31 = 0;
        for (k = 0; k < N; k++) {
            /* FOR EACH 32x32 MATRIX   */
            matrix.assignFromBits(b, k * 32 * 32);

            R = matrix.computeRank();
            if (R == 32) {
                F_32++;
                /* DETERMINE FREQUENCIES */
            }
            if (R == 31) {
                F_31++;
            }
        }
        F_30 = N - (F_32 + F_31);

        chi_squared = (square(F_32 - N * p_32) / (double) (N * p_32)
                + square(F_31 - N * p_31) / (double) (N * p_31)
                + square(F_30 - N * p_30) / (double) (N * p_30));

        arg1 = -chi_squared / 2.e0;
        double p_value = Math.exp(arg1);
        
        RankResult result = new RankResult(p_value);
        result.F_30 = F_30;
        result.F_31 = F_31;
        result.F_32 = F_32;
        result.p_30 = p_30;
        result.p_31 = p_31;
        result.p_32 = p_32;
        result.chi_squared = chi_squared;
        result.length = length;

        return new Result[]{ result };
    }

    double square(double x) {
        return x * x;
    }
    
    @Override
    public void report(PrintWriter out, Result[] results) {
//        if (insufficientBits) {
//            out.println("RANK TEST");
//            out.println("Error: Insuffucient # Of Bits To Define An 32x32 Matrix\n");
//            return;
//        }

        out.println("RANK TEST");
        out.println("---------------------------------------------");
        if (results == null) {
            out.println("ERROR: There is no result yet");
            out.println("- OR - Insuffucient # Of Bits To Define An 32x32 Matrix\n");

            return;
        }
        
        RankResult r = (RankResult) results[0];
        
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("---------------------------------------------");
        out.printf("(a) Probability P_%d = %f\n", 32, r.getP_32());
        out.printf("(b)             P_%d = %f\n", 31, r.getP_31());
        out.printf("(c)             P_%d = %f\n", 30, r.getP_30());
        out.printf("(d) Frequency   F_%d = %d\n", 32, r.getF_32());
        out.printf("(e)             F_%d = %d\n", 31, r.getF_31());
        out.printf("(f)             F_%d = %d\n", 30, r.getF_30());
        out.printf("(g) # of matrices    = %d\n", r.getLength() / (32 * 32));
        out.printf("(h) Chi^2            = %f\n", r.getChi_squared());
        out.printf("(i) NOTE: %d BITS WERE DISCARDED.\n", r.getLength() % (32 * 32));
        out.println("---------------------------------------------");

        if (r.getPValue() < 0 || r.getPValue() > 1) {
            out.println("WARNING:  P_VALUE IS OUT OF RANGE.");
        }

        out.printf("%s\t\tp_value = %f\n\n",
                   r.isPassed() ? "SUCCESS" : "FAILURE",
                   r.getPValue());
    }

    @Override
    public int getInputSizeRecommendation() {
        return 38912;
    }
    
}
