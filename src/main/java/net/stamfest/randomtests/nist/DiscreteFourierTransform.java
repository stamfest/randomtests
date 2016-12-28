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
import org.jtransforms.fft.DoubleFFT_1D;

/**
 * The focus of this test is the peak heights in the Discrete Fourier Transform
 * of the sequence. The purpose of this test is to detect periodic features
 * (i.e., repetitive patterns that are near each other) in the tested sequence
 * that would indicate a deviation from the assumption of randomness. The
 * intention is to detect whether the number of peaks exceeding the 95 %
 * threshold is significantly different than 5 %.
 *
 * @author Peter Stamfest
 */
public class DiscreteFourierTransform extends AbstractNistTest {
    public static class DiscreteFourierTransformResult extends Result {
        private double percentile;
        private double N_l;
        private double N_o;
        private double d;

        public DiscreteFourierTransformResult(double p_value) {
            super(p_value, null);
        }

        public double getPercentile() {
            return percentile;
        }

        public double getN_l() {
            return N_l;
        }

        public double getN_o() {
            return N_o;
        }

        public double getD() {
            return d;
        }
        
    }
    
    @Override
    public Result[] runTest(Bits b) {
        int n = b.getLength();
        double percentile;
        double N_l;
        double N_o;
        double d;

        double upperBound;
        double X[] = new double[n];
        double m[] = new double[n / 2 + 1];

        int i, count, ifac[] = new int[15];

        for (i = 0; i < n; i++) {
            X[i] = 2 * (int) b.bit(i) - 1;  // turn 0 and 1 into -1 and +1
        }

        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.realForward(X);
//
//        for (int ii = 0; ii < n; ii++) {
//            System.out.printf("X[%d]   %g\n", ii, X[ii]);
//        }

        m[0] = Math.sqrt(X[0] * X[0]);
        /* COMPUTE MAGNITUDE */
        m[n / 2] = Math.sqrt(X[1] * X[1]);

        for (i = 0; i < n / 2 - 1; i++) {
            m[i + 1] = Math.hypot(X[2 * i + 2], X[2 * i + 3]);
        }

        count = 0;
        /* CONFIDENCE INTERVAL */
        upperBound = Math.sqrt(2.995732274 * n);
        for (i = 0; i < n / 2; i++) {
            if (m[i] < upperBound) {
                count++;
            }
        }
        percentile = (double) count / (n / 2) * 100;
        N_l = (double) count;
        /* number of peaks less than h = sqrt(3*n) */
        N_o = (double) 0.95 * n / 2.0;
        d = (N_l - N_o) / Math.sqrt(n / 4.0 * 0.95 * 0.05);
        double p_value = Erf.erfc(Math.abs(d) / Math.sqrt(2.0));
        
        DiscreteFourierTransformResult r = new DiscreteFourierTransformResult(p_value);
        r.N_l = N_l;
        r.N_o = N_o;
        r.d = d;
        r.percentile = percentile;
        
        return new Result[] { r };
    }

    @Override
    public void report(PrintWriter out, Result[] results) {
        out.println("FFT TEST");
        out.println("-------------------------------------------");
        if (results == null) {
            out.println("ERROR: There is no result yet");
            return;
        }
        DiscreteFourierTransformResult r = (DiscreteFourierTransformResult) results[0];
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("-------------------------------------------");
        out.printf("(a) Percentile = %f\n", r.getPercentile());
        out.printf("(b) N_l        = %f\n", r.getN_l());
        out.printf("(c) N_o        = %f\n", r.getN_o());
        out.printf("(d) d          = %f\n", r.getD());
        out.println("-------------------------------------------");

        out.printf("%s\t\tp_value = %f\n\n",
                   r.isPassed() ? "SUCCESS" : "FAILURE",
                   r.getPValue());
    }

    @Override
    public int getInputSizeRecommendation() {
        return 1000;
    }

}
