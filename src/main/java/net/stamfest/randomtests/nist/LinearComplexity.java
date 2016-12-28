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
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.bits.MutableArrayBits;
import org.apache.commons.math3.special.Gamma;

/**
 * The focus of this test is the length of a linear feedback shift register
 * (LFSR). The purpose of this test is to determine whether or not the sequence
 * is complex enough to be considered random. Random sequences are characterized
 * by longer LFSRs. An LFSR that is too short implies non-randomness.
 *
 * @author NIST / ported by Peter Stamfest
 */
public class LinearComplexity extends AbstractNistTest {
    public static class LinearComplexityResult extends Chi2Result {
        public LinearComplexityResult(double p_value, double chi2, int degrees) {
            super(p_value, chi2, degrees, null);
        }
        
        private long nu[] = new long[7];
        private int blockLength, K;
        private int length;
        private int blockCount;

        public long getNu(int i) {
            return nu[i];
        }

        public int getDiscarded() {
            return length % blockLength;
        }

        public int getBlockLength() {
            return blockLength;
        }

        public int getK() {
            return K;
        }

        private int getLength() {
            return length;
        }

        public long[] getNu() {
            return nu;
        }

        public int getBlockCount() {
            return blockCount;
        }
    };
    
    private final int M;

    public LinearComplexity(int M) {
        this.M = M;
        if (M < 500 || M > 5000) {
            throw new IllegalArgumentException("M is recommended to be between 500 and 5000");
        }
    }

    @Override
    public Result[] runTest(Bits b) {
        long nu[] = new long[7];
        double chi2;
        int K = 6;
        int N;
        int length = b.getLength();
        int i, ii, j, d, L, m, N_, parity, sign;
        double T_, mean;
        double pi[] = new double[]{ 0.01047, 0.03125, 0.12500, 0.50000, 0.25000, 0.06250, 0.020833 };
        MutableArrayBits T, P, B_, C;

        K = 6;

        // blockCount = (int)Math.floor(n/M); 
        N = length / M;

        for (i = 0; i < K + 1; i++) {
            nu[i] = 0;
        }

        byte mutable[] = new byte[M / 8 + 1];
        Arrays.fill(mutable, (byte) 0);

        C = new MutableArrayBits(M);
        T = new MutableArrayBits(M);
        P = new MutableArrayBits(M);
        B_ = new MutableArrayBits(M);

        for (ii = 0; ii < N; ii++) {
            C.fill((byte) 0);
            T.fill((byte) 0);
            P.fill((byte) 0);
            B_.fill((byte) 0);
            L = 0;
            m = -1;
            d = 0;
            C.setBit(0, 1);
            B_.setBit(0, 1);

            /* DETERMINE LINEAR COMPLEXITY */
            N_ = 0;
            while (N_ < M) {
                d = (int) b.bit(ii * M + N_);
                for (i = 1; i <= L; i++) {
                    d += C.bit(i) * b.bit(ii * M + N_ - i);
                }
                d = d % 2;
                if (d == 1) {
                    P.fill((byte) 0);
                    T.assignFrom(C);

                    for (j = 0; j < M; j++) {
                        if (B_.bit(j) != 0) {
                            P.setBit(j + N_ - m, 1);
                        }
                    }
                    for (i = 0; i < M; i++) {
                        C.setBit(i, (C.bit(i) + P.bit(i)) % 2);
                    }
                    if (L <= N_ / 2) {
                        L = N_ + 1 - L;
                        m = N_;
                        B_.assignFrom(T);
                    }
                }
                N_++;
            }
            if ((parity = (M + 1) % 2) == 0) {
                sign = -1;
            } else {
                sign = 1;
            }
            mean = M / 2.0 + (9.0 + sign) / 36.0 - 1.0 / Math.pow(2, M) * (M / 3.0 + 2.0 / 9.0);
            if ((parity = M % 2) == 0) {
                sign = 1;
            } else {
                sign = -1;
            }
            T_ = sign * (L - mean) + 2.0 / 9.0;
            if (T_ <= -2.5) {
                nu[0]++;
            } else if (T_ > -2.5 && T_ <= -1.5) {
                nu[1]++;
            } else if (T_ > -1.5 && T_ <= -0.5) {
                nu[2]++;
            } else if (T_ > -0.5 && T_ <= 0.5) {
                nu[3]++;
            } else if (T_ > 0.5 && T_ <= 1.5) {
                nu[4]++;
            } else if (T_ > 1.5 && T_ <= 2.5) {
                nu[5]++;
            } else {
                nu[6]++;
            }
        }
        chi2 = 0.00;
        for (i = 0; i < K + 1; i++) {
            chi2 += Math.pow(nu[i] - N * pi[i], 2) / (N * pi[i]);
        }
        
        LinearComplexityResult r
                = new LinearComplexityResult(Gamma.regularizedGammaQ(K / 2.0, chi2 / 2.0),
                                             chi2, K);
        r.length = length;
        r.K = K;
        r.blockLength = M;
        r.blockCount = N;
              
        System.arraycopy(nu, 0, r.getNu(), 0, nu.length);
        
        return new Result[]{ r };
    }

    @Override
    public void report(PrintWriter out, Result[] results) {
        out.println("L I N E A R  C O M P L E X I T Y");
        out.println("-----------------------------------------------------");
        if (results == null) {
            out.println("ERROR: There is no result yet");
            return;
        }
        
        LinearComplexityResult result = (LinearComplexityResult) results[0];
        
        out.printf("M (substring length)     = %d\n", result.getBlockLength());
        out.printf("N (number of substrings) = %d\n", result.getBlockCount());
        out.println("-----------------------------------------------------");
        out.println("        F R E Q U E N C Y                            ");
        out.println("-----------------------------------------------------");
        out.println("  C0   C1   C2   C3   C4   C5   C6    CHI2    P-value");
        out.println("-----------------------------------------------------");
        out.printf("Note: %d bits were discarded!\n", result.getLength() % result.getBlockLength());

        int cnt= result.getK() + 1;
        for (int i = 0; i < cnt; i++) {
            out.printf("%4d ", (int) result.getNu(i));
        }

        out.printf("%9.6f%9.6f\n", result.getChi2(), result.getPValue());
    }
    
    @Override
    public int getInputSizeRecommendation() {
        return Math.max(1000000, 200 * M);
        /* N >= 200 

        N = length / M >= 200
        -> length >= 200 * M
        
        */
        //
    }

    @Override
    public String getDescription() {
        return super.getDescription() + String.format("(%d)", M);
    }
}
