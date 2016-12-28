/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.nist;

import java.io.PrintWriter;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.bits.MutableArrayBits;
import org.apache.commons.math3.special.Gamma;

/**
 * The focus of the Overlapping Template Matching test is the number of
 * occurrences of pre-specified target strings. Both this test and the
 * Non-overlapping Template Matching test of Section 2.7 use an m -bit window to
 * search for a specific m -bit pattern. As with the test in Section 2.7, if the
 * pattern is not found, the window slides one bit position. The difference
 * between this test and the test in Section 2.7 is that when the pattern is
 * found, the window slides only one bit before resuming the search.
 *
 * NOTE: The implementation ported from the original NIST code only implements a
 * single pattern for this test, namely the all-one-bit pattern.
 *
 * @author Peter Stamfest
 */
public class OverlappingTemplateMatching extends AbstractNistTest {

    public static class OverlappingTemplateMatchingResult extends Chi2Result {
        private int length;
        private int templateLength;
        private int[] nu;
        private double[] pi;
        private int M;
        private int N;
        private double lambda;
        private double eta;

        public OverlappingTemplateMatchingResult(double p_value, double chi2, int degrees) {
            super(p_value, chi2, degrees, null);
        }

        public int getLength() {
            return length;
        }

        public int[] getNu() {
            return nu;
        }

        public double[] getPi() {
            return pi;
        }

        public int getM() {
            return M;
        }

        public int getN() {
            return N;
        }

        public double getLambda() {
            return lambda;
        }

        public double getEta() {
            return eta;
        }

        public int getTemplateLength() {
            return templateLength;
        }

    }


    private final int m;

    /**
     * Construct a new all-one-bit template test using an m bit long template.
     *
     * @param m The template length. NIST recommends m = 9 or 10. Note that
     *          {@link #getInputSizeRecommendation()} returns a value only
     *          compatible with these two values for m.
     */
    public OverlappingTemplateMatching(int m) {
        this.m = m;
    }
    
    @Override
    public Result[] runTest(Bits bits) {
        int n = bits.getLength();
        int[] nu;
        double[] pi;
        int M;
        int N;
        double lambda;
        double eta;
        double chi2;
              
        int i, k, match;
        double W_obs, sum;
        int  j, K = 5;
        nu = new int[] { 0, 0, 0, 0, 0, 0 };
        pi = new double[] { 0.143783, 0.139430, 0.137319, 0.124314, 0.106209, 0.348945 };
        
        MutableArrayBits sequence = new MutableArrayBits(m);

        M = 1032;
        N = n / M;

        sequence.fill((byte) 0xff);

        lambda = (double) (M - m + 1) / Math.pow(2, m);
        eta = lambda / 2.0;
        sum = 0.0;
        for (i = 0; i < K; i++) {
            /* Compute Probabilities */
            pi[i] = Pr(i, eta);
            sum += pi[i];
        }
        pi[K] = 1 - sum;

        for (i = 0; i < N; i++) {
            W_obs = 0;
            for (j = 0; j < M - m + 1; j++) {
                match = 1;
                for (k = 0; k < m; k++) {
                    if (sequence.bit(k) != bits.bit(i * M + j + k)) {
                        match = 0;
                    }
                }
                if (match == 1) {
                    W_obs++;
                }
            }
            if (W_obs <= 4) {
                nu[(int) W_obs]++;
            } else {
                nu[K]++;
            }
        }
        sum = 0;
        chi2 = 0.0;
        /* Compute Chi Square */
        for (i = 0; i < K + 1; i++) {
            chi2 += Math.pow((double) nu[i] - (double) N * pi[i], 2) / ((double) N * pi[i]);
            sum += nu[i];
        }
        
        OverlappingTemplateMatchingResult r
                = new OverlappingTemplateMatchingResult(Gamma.regularizedGammaQ(K / 2.0, chi2 / 2.0),
                                                        chi2,
                                                        K);
        
        r.M = M;
        r.templateLength = m;
        r.length = n;
        r.N = N;
        r.eta = eta;
        r.lambda = lambda;
        r.nu = nu;
        r.pi = pi;
        
        return new Result[]{ r };
    }
    
    @Override
    public void report(PrintWriter out, Result[] results) {

        out.println("OVERLAPPING TEMPLATE OF ALL ONES TEST");
        out.println("-----------------------------------------------");
        if (results == null) {
            out.println("ERROR: There is no result yet");
            return;
        }
        
        OverlappingTemplateMatchingResult result = (OverlappingTemplateMatchingResult) results[0];
        
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("-----------------------------------------------");
        out.printf("(a) n (sequence_length)      = %d\n", result.getLength());
        out.printf("(b) m (block length of 1s)   = %d\n", result.getTemplateLength());
        out.printf("(c) M (length of substring)  = %d\n", result.getM());
        out.printf("(d) N (number of substrings) = %d\n", result.getN());
        out.printf("(e) lambda [(M-m+1)/2^m]     = %f\n", result.getLambda());
        out.printf("(f) eta                      = %f\n", result.getEta());
        out.println("-----------------------------------------------");
        out.println("F R E Q U E N C Y");
        out.println("0     1     2     3     4   >=5   Chi^2   P-value  Assignment");
        out.println("-----------------------------------------------");

        int [] nu = result.getNu();
        out.printf("%5d %5d %5d %5d %5d %5d  %f ",
                nu[0], nu[1], nu[2], nu[3], nu[4], nu[5], result.getChi2());

        if (results[0].getPValue() < 0 || results[0].getPValue() > 1) {
            out.println("WARNING:  P_VALUE IS OUT OF RANGE.\n");
        }

        out.printf("%f %s\n\n", results[0].getPValue(), ! results[0].isPassed() ? "FAILURE" : "SUCCESS");
    }

    double Pr(int u, double eta) {
        int l;
        double sum, p;

        if (u == 0) {
            p = Math.exp(-eta);
        } else {
            sum = 0.0;
            for (l = 1; l <= u; l++) {
                sum += Math.exp(-eta - u * Math.log(2) + l * Math.log(eta) - Gamma.logGamma(l + 1) + Gamma.logGamma(u) - Gamma.logGamma(l) - Gamma.logGamma(u - l + 1));
            }
            p = sum;
        }
        return p;
    }

    @Override
    public int getInputSizeRecommendation() {
        /*
        
        The values of K, M and N have been chosen such that each sequence to be 
        tested consists of a minimum of 106 bits (i.e., n ≥ 106). Various values 
        of m may be selected, but for the time being, NIST recommends
        m = 9 or m = 10. If other values are desired, please choose these values as follows:
        
        •	n ≥ MN.
        •	N should be chosen so that N • (min πi) > 5.
        •	λ = (M-m+1)/2m ≈ 2
        •	m should be chosen so that m ≈ log2 M
        •	Choose K so that K ≈ 2λ. Note that the πi values would need 
                to be recalculated for values of K other than 5.

         */
        return 106;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + String.format("(%d)", m);
    }
}
