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
 * Test for the Longest Run of Ones in a Block
 *
 * The focus of the test is the longest run of ones within M-bit blocks. The
 * purpose of this test is to determine whether the length of the longest run of
 * ones within the tested sequence is consistent with the length of the longest
 * run of ones that would be expected in a random sequence. Note that an
 * irregularity in the expected length of the longest run of ones implies that
 * there is also an irregularity in the expected length of the longest run of
 * zeroes. Therefore, only a test for ones is necessary. See Section 4.4.
 *
 * M  The length of each block. The test code has been pre-set to accommodate three values for 
 * M: M = 8, M = 128 and M = 10^4 in accordance with the following values of sequence 
 * length, n: 
 *
 * Minimum n  | M
 * 128        | 8
 * 6272       | 128
 * 750,000    | 10^4
 *
 * N The number of blocks; selected in accordance with the value of M.
 */

public class LongestRunOfOnes extends AbstractNistTest {
    public static class LongestRunOfOnesResult extends Chi2Result {
        private int length;
        private int[] nu;
        private int K;
        private int blockLen;
        private int numberOfBlocks;

        public LongestRunOfOnesResult(double p_value, double chi2, int degrees) {
            super(p_value, chi2, degrees, null);
        }

        public int getLength() {
            return length;
        }

        public int[] getNu() {
            return nu;
        }

        public int getK() {
            return K;
        }

        public int getBlockLen() {
            return blockLen;
        }

        public int getNumberOfBlocks() {
            return numberOfBlocks;
        }
        
        
    }
    
    @Override
    public Result[] runTest(Bits b) {
        int blockLen;
        int numberOfBlocks;
        double chi2;
        int K;
//        boolean sequenceTooShort;

        int length = b.getLength();
        double pi[] = new double[7];
        int run, v_n_obs, i, j, V[] = new int[7];

        int [] nu = new int[]{ 0, 0, 0, 0, 0, 0, 0 };

//        sequenceTooShort = false;
        if (length < 128) {
//            sequenceTooShort = true;
            return null;
        }
        if (length < 6272) {
            K = 3;
            blockLen = 8;
            V[0] = 1;
            V[1] = 2;
            V[2] = 3;
            V[3] = 4;
            pi[0] = 0.21484375;
            pi[1] = 0.3671875;
            pi[2] = 0.23046875;
            pi[3] = 0.1875;
        } else if (length < 750000) {
            K = 5;
            blockLen = 128;
            V[0] = 4;
            V[1] = 5;
            V[2] = 6;
            V[3] = 7;
            V[4] = 8;
            V[5] = 9;
            pi[0] = 0.1174035788;
            pi[1] = 0.242955959;
            pi[2] = 0.249363483;
            pi[3] = 0.17517706;
            pi[4] = 0.102701071;
            pi[5] = 0.112398847;
        } else {
            K = 6;
            blockLen = 10000;
            V[0] = 10;
            V[1] = 11;
            V[2] = 12;
            V[3] = 13;
            V[4] = 14;
            V[5] = 15;
            V[6] = 16;
            pi[0] = 0.0882;
            pi[1] = 0.2092;
            pi[2] = 0.2483;
            pi[3] = 0.1933;
            pi[4] = 0.1208;
            pi[5] = 0.0675;
            pi[6] = 0.0727;
        }

        numberOfBlocks = length / blockLen;
        for (i = 0; i < numberOfBlocks; i++) {
            v_n_obs = 0;
            run = 0;
            for (j = 0; j < blockLen; j++) {
                if (b.bit(i * blockLen + j) == 1) {
                    run++;
                    if (run > v_n_obs) {
                        v_n_obs = run;
                    }
                } else {
                    run = 0;
                }
            }
            if (v_n_obs < V[0]) {
                nu[0]++;
            }
            for (j = 0; j <= K; j++) {
                if (v_n_obs == V[j]) {
                    nu[j]++;
                }
            }
            if (v_n_obs > V[K]) {
                nu[K]++;
            }
        }

        chi2 = 0.0;
        for (i = 0; i <= K; i++) {
            chi2 += ((nu[i] - numberOfBlocks * pi[i]) * (nu[i] - numberOfBlocks * pi[i])) / (numberOfBlocks * pi[i]);
        }
        
        LongestRunOfOnesResult r
                = new LongestRunOfOnesResult(Gamma.regularizedGammaQ((double) (K / 2.0), chi2 / 2.0),
                                             chi2,
                                             K);
        r.K = K;
        r.blockLen = blockLen;
        r.length = length;
        r.nu = nu;
        r.numberOfBlocks = numberOfBlocks;
        
        return new Result[]{ r };
    }
 
    @Override
    public void report(PrintWriter out, Result[] results) {
        out.println("LONGEST RUNS OF ONES TEST");
        out.println("---------------------------------------------");
//        if (sequenceTooShort) {
//            out.printf("   n=%d sequence is too short\n", length);
//            return;
//        }
        
        if (results == null) {
            out.println("ERROR: There is no result yet");
            return;
        }
        LongestRunOfOnesResult result = (LongestRunOfOnesResult) results[0];
        
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("---------------------------------------------");
        out.printf("(a) N (# of substrings)  = %d\n", result.getNumberOfBlocks());
        out.printf("(b) M (Substring Length) = %d\n", result.getBlockLen());
        out.printf("(c) Chi^2                = %f\n", result.getChi2());
        out.println("---------------------------------------------");
        out.println("      F R E Q U E N C Y");
        out.println("---------------------------------------------");

        int[] nu = result.getNu();

        switch (result.getK()) {
            case 3:
                out.println("    <=1      2      3    >=4 P-value  Assignment");
                out.printf(" %6d %6d %6d %6d \n", nu[0], nu[1], nu[2], nu[3]);
                break;
            case 5:
                out.println("    <=4      5      6      7      8    >=9 P-value  Assignment");
                out.printf(" %6d %6d %6d %6d %6d %6d\n", nu[0], nu[1], nu[2],
                           nu[3], nu[4], nu[5]);
                break;
            default:
                out.println("   <=10     11     12     13     14     15   >=16 P-value  Assignment");
                out.printf(" %6d %6d %6d %6d %6d %6d %6d\n", nu[0], nu[1], nu[2],
                           nu[3], nu[4], nu[5], nu[6]);
                break;
        }
        
        if (result.getPValue() < 0.0 || result.getPValue() > 1.0) {
            out.println("WARNING:  P_VALUE IS OUT OF RANGE.");
        }

        out.printf("%s\t\tp_value = %f\n\n", 
                   result.isPassed() ? "SUCCESS" : "FAILURE", 
                   result.getPValue());
    }

    @Override
    public int getInputSizeRecommendation() {
        return 128;
    }
}
