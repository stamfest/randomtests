/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * (c) 1999 by the National Institute Of Standards & Technology
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.nist;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.nist.utils.AperiodicTemplate;
import org.apache.commons.math3.special.Gamma;

/**
 * The focus of this test is the number of occurrences of pre-specified target
 * strings. The purpose of this test is to detect generators that produce too
 * many occurrences of a given non-periodic (aperiodic) pattern. For this test
 * and for the Overlapping Template Matching test of Section 2.8, an m -bit
 * window is used to search for a specific m-bit pattern. If the pattern is not
 * found, the window slides one bit position. If the pattern is found, the
 * window is reset to the bit after the found pattern, and the search resumes.
 *
 * @author Peter Stamfest
 */
public class NonOverlappingTemplateMatchings extends AbstractNistTest {
    static final int N = 8;

    static class NonOverlappingTemplateMatchingsResult extends Chi2Result {
        private int index;
        private int[] Wj;
        private String seq;
        private double lambda;
        private int M;
        private int length;
        private int m;

        /**
         *
         * @param p_value the value of p_value
         * @param chi2 the value of chi2
         * @param degrees the value of degrees
         * @param template the value of template
         */
        public NonOverlappingTemplateMatchingsResult(double p_value, double chi2, int degrees, String template) {
            super(p_value, chi2, degrees, template);
            seq = template;
        }


        void report(PrintWriter out) {
            out.printf("%s ", seq);
            for (int i = 0; i < N; i++) {
                out.printf("%4d ", Wj[i]);
            }
            out.printf("%9.6f %f %s    %5d\n", 
                       getChi2(), getPValue(), 
                       isPassed() ? "SUCCESS" : "FAILURE", 
                       index + 1);
            if (getPValue() < 0 || getPValue() > 1) {
                out.printf("WARNING:  P_VALUE IS OUT OF RANGE.\n");
            }
        }

        public int getIndex() {
            return index;
        }

        public double getLambda() {
            return lambda;
        }

        public int getM() {
            return M;
        }

        public int getm() {
            return m;
        }

        public int getLength() {
            return length;
        }

        public int[] getWj() {
            return Wj;
        }

        public String getSeq() {
            return seq;
        }
    }
        
    private final int m;
    static final int MAXNUMOFTEMPLATES = 148;

    
    /**
     * Construct a new Test object for the given template length.
     *
     * @param templateLength The template length to run the test for.
     */
    
    public NonOverlappingTemplateMatchings(int templateLength) {
        this.m = templateLength;
    }

    @Override
    public Result[] runTest(Bits b) {
        NonOverlappingTemplateMatchingsResult results[] = null;
        double lambda;
        int M;
        int length = b.getLength();
//        final int numOfTemplates[] = new int[]{ 0, 0, 2, 4, 6, 12, 20, 40, 74, 148, 284, 568, 1116,
//                                                2232, 4424, 8848, 17622, 35244, 70340, 140680, 281076, 562152 };
        /*----------------------------------------------------------------------------
        NOTE:  Should additional templates lengths beyond 21 be desired, they must 
        first be constructed, saved into files and then the corresponding 
        number of nonperiodic templates for that file be stored in the m-th 
        position in the numOfTemplates variable.
        ----------------------------------------------------------------------------*/
        int W_obs;
        int[] Wj;
        double sum, chi2, pi[] = new double[6], varWj;
        int i, j, jj, k, match, SKIP, K = 5;

        M = length / N;

        Wj = new int[N];

        lambda = (M - m + 1) / Math.pow(2, m);
        varWj = M * (1.0 / Math.pow(2.0, m) - (2.0 * m - 1.0) / Math.pow(2.0, 2.0 * m));

        AperiodicTemplate at = new AperiodicTemplate(m);

        if ((lambda < 0) || (lambda == 0)) {
            throw new IllegalArgumentException("lambda not positive");
        }

        int numOfTemplates = at.getCount();
        if (numOfTemplates < MAXNUMOFTEMPLATES) {
            SKIP = 1;
        } else {
            SKIP = (int) (numOfTemplates / MAXNUMOFTEMPLATES);
        }
        numOfTemplates = (int) numOfTemplates / SKIP;

        sum = 0.0;
        for (i = 0; i < 2; i++) {
            /* Compute Probabilities */
            pi[i] = Math.exp(-lambda + i * Math.log(lambda) - Gamma.logGamma(i + 1));
            sum += pi[i];
        }
        pi[0] = sum;
        for (i = 2; i <= K; i++) {
            /* Compute Probabilities */
            pi[i - 1] = Math.exp(-lambda + i * Math.log(lambda) - Gamma.logGamma(i + 1));
            sum += pi[i - 1];
        }
        pi[K] = 1 - sum;

        Iterator<Long> I = at.iterator();

        int numberOfTests = Math.min(MAXNUMOFTEMPLATES, numOfTemplates);
        results = new NonOverlappingTemplateMatchingsResult[numberOfTests];
        for (jj = 0; jj < numberOfTests; jj++) {
            sum = 0;

            Long seq = I.next();

            for (i = 0; i < N; i++) {
                W_obs = 0;
                for (j = 0; j < M - m + 1; j++) {
                    match = 1;
                    for (k = 0; k < m; k++) {
//                        if ((int) squence[k] != (int) b.bit(i * M + j + k)) {
                        if ((int) ((seq >> (m - k - 1)) & 0x1) != (int) b.bit(i * M + j + k)) {
                            match = 0;
                            break;
                        }
                    }
                    if (match == 1) {
                        W_obs++;
                    }
                }
                Wj[i] = W_obs;
            }
            sum = 0;
            chi2 = 0.0;
            /* Compute Chi Square */
            for (i = 0; i < N; i++) {
                chi2 += Math.pow(((double) Wj[i] - lambda) / Math.pow(varWj, 0.5), 2);
            }

            results[jj] = new NonOverlappingTemplateMatchingsResult(Gamma.regularizedGammaQ(N / 2.0, chi2 / 2.0),
                                                                    chi2, N, at.bitstring(seq, null));
            results[jj].index = jj;
            results[jj].Wj = Arrays.copyOf(Wj, Wj.length);
            results[jj].M = M;
            results[jj].m = m;
            results[jj].lambda = lambda;
            results[jj].length = length;

            if (SKIP > 1) {
                for (k = 1; k < SKIP && I.hasNext(); k++) {
                    I.next();
                }
            }
        }
        return results;
    }

    @Override
    public void report(PrintWriter out, Result[] results) {
        out.printf("NONPERIODIC TEMPLATES TEST\n");
        out.printf("-------------------------------------------------------------------------------------\n");
        if (results == null) {
            out.println("ERROR: There are no result yet");
            return;
        }
        
        NonOverlappingTemplateMatchingsResult result = (NonOverlappingTemplateMatchingsResult) results[0];
        
        out.printf("\t\t  COMPUTATIONAL INFORMATION\n");
        out.printf("-------------------------------------------------------------------------------------\n");
        out.printf("\tLAMBDA = %f\tM = %d\tN = %d\tm = %d\tn = %d\n", 
                   result.getLambda(), result.getM(), N, result.getm(), result.getLength());
        out.printf("-------------------------------------------------------------------------------------\n");
        out.printf("\t\tF R E Q U E N C Y\n");
        out.printf("Template   W_1  W_2  W_3  W_4  W_5  W_6  W_7  W_8    Chi^2   P_value Assignment Index\n");
        out.printf("-------------------------------------------------------------------------------------\n");
        for (Result r: results) {
            ((NonOverlappingTemplateMatchingsResult) r).report(out);
        }
    }

    @Override
    public int getInputSizeRecommendation() {
        /*
         * The test code has been written to provide templates for m = 2,
         * 3,...,10. It is recommended that m = 9 or m = 10 be specified to
         * obtain meaningful results. Although N = 8 has been specified in the
         * test code, the code may be altered to other sizes. However, N should
         * be chosen such that N ≤ 100 to be assured that the P-values are
         * valid. Additionally, be sure that M > 0.01 • n and N = Math.floor(n/M).         *
         */
        
        return N;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + String.format("(%d)", m);
    }
    
    
  
}
