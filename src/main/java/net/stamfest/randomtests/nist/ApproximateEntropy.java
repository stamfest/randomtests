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
import org.apache.commons.math3.special.Gamma;

/**
 * As with the Serial test, the focus of this test is the frequency of all
 * possible overlapping m-bit patterns across the entire sequence. The purpose
 * of the test is to compare the frequency of overlapping blocks of two
 * consecutive/adjacent lengths ( m and m+1 ) against the expected result for a
 * random sequence.
 *
 * @author NIST / ported by Peter Stamfest
 */
public class ApproximateEntropy extends AbstractNistTest {
    public static class ApproximateEntropyResult extends Chi2Result {
        private int seqLength;
        private double ApEn[] = new double[2];
        private double apen;

        public int getSeqLength() {
            return seqLength;
        }

        public double[] getApEn() {
            return ApEn;
        }

        public double getApen() {
            return apen;
        }
        
        public ApproximateEntropyResult(double p_value, double chi2, int degrees) {
            super(p_value, chi2, degrees, null);
        }
    }
    
    private final static double ln2 = Math.log(2.0);
    private final int m;
    private final int recommendation;
    
    /**
     *
     * @param m The length of each block – in this case, the first block length
     *          used in the test. m+1 is the second block length used. Choose m
     *          such that m is smaller than Math.floor(log_2(n))-5 where n is the
     *          length of the sequence.
     */
    public ApproximateEntropy(int m) {
        this.m = m;
                         
        this.recommendation = (int) Math.exp((m + 5.0) * ln2);
    }

    @Override
    public Result[] runTest(Bits b) {
        int seqLength;
        double chi_squared;
        double ApEn[] = new double[2], apen;
       
        int i, j, k, r, blockSize, powLen, index;
        double sum, numOfBlocks;
        int P[];

        seqLength = b.getLength();
        r = 0;

        for (blockSize = m; blockSize <= m + 1; blockSize++) {
            if (blockSize == 0) {
                ApEn[0] = 0.00;
                r++;
            } else {
                numOfBlocks = (double) seqLength;
                powLen = (int) Math.pow(2, blockSize + 1) - 1;
                P = new int[powLen];

                for (i = 1; i < powLen - 1; i++) {
                    P[i] = 0;
                }
                for (i = 0; i < numOfBlocks; i++) {
                    /* COMPUTE FREQUENCY */
                    k = 1;
                    for (j = 0; j < blockSize; j++) {
                        k <<= 1;
                        if ((int) b.bit((i + j) % seqLength) == 1) {
                            k++;
                        }
                    }
                    P[k - 1]++;
                }
                /* DISPLAY FREQUENCY */
                sum = 0.0;
                index = (int) Math.pow(2, blockSize) - 1;
                for (i = 0; i < (int) Math.pow(2, blockSize); i++) {
                    if (P[index] > 0) {
                        sum += P[index] * Math.log(P[index] / numOfBlocks);
                    }
                    index++;
                }
                sum /= numOfBlocks;
                ApEn[r] = sum;
                r++;
            }
        }
        apen = ApEn[0] - ApEn[1];

        chi_squared = 2.0 * seqLength * (Math.log(2) - apen);
        double p_value = Gamma.regularizedGammaQ(Math.pow(2, m - 1), chi_squared / 2.0);
        
        ApproximateEntropyResult result = new ApproximateEntropyResult(p_value, chi_squared, 1 << m);
        result.seqLength = seqLength;
        result.apen = apen;
        result.ApEn[0] = ApEn[0];
        result.ApEn[1] = ApEn[1];
        
        return new Result[] { result };
    }

    @Override
    public void report(PrintWriter out, Result[] results) {
        out.println("APPROXIMATE ENTROPY TEST");
        out.println("--------------------------------------------");
        if (results == null) {
            out.println("ERROR: There is no result yet");
            return;
        }
        
        ApproximateEntropyResult r = (ApproximateEntropyResult) results[0];
        
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("--------------------------------------------");
        out.printf("(a) m (block length)    = %d\n", m);
        out.printf("(b) n (sequence length) = %d\n", r.getSeqLength());
        out.printf("(c) Chi^2               = %f\n", r.getChi2());
        out.printf("(d) Phi(m)              = %f\n", r.getApEn()[0]);
        out.printf("(e) Phi(m+1)            = %f\n", r.getApEn()[1]);
        out.printf("(f) ApEn                = %f\n", r.getApen());
        out.printf("(g) Log(2)              = %f\n", Math.log(2.0));
        out.println("--------------------------------------------");

        int recommendedBlocksize = (int) (Math.log(r.getSeqLength()) / Math.log(2) - 5);
        if (m > recommendedBlocksize) {
            out.printf("Note: The blockSize = %d exceeds recommended value of %d\n", m,
                       Math.max(1, recommendedBlocksize));
            out.println("Results are inaccurate!");
            out.println("--------------------------------------------");
        }

        out.printf("%s\t\tp_value = %f\n\n",
                   !r.isPassed() ? "FAILURE" : "SUCCESS",
                   r.getPValue());
    }
    
    @Override
    public int getInputSizeRecommendation() {
        // m < Math.floor(log(n)/log(2)) - 5
        /*
        m + 5 < Math.floor()
        m + 5 <   log(n)/log(2)
        (m + 5) * log(2) < log(n)
                
        n > exp((m + 5) * log(2))
                        */
        return recommendation;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + String.format("(%d)", m);
    }
}
