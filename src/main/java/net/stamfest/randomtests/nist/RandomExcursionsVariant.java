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
 *
 * Test Purpose The focus of this test is the total number of times that a
 * particular state is visited (i.e., occurs) in a cumulative sum random walk.
 * The purpose of this test is to detect deviations from the expected number of
 * visits to various states in the random walk. This test is actually a series
 * of eighteen tests (and conclusions), one test and conclusion for each of the
 * states: -9, -8, ..., -1 and +1, +2, ..., +9.
 *
 * @author Peter Stamfest
 */
public class RandomExcursionsVariant extends AbstractNistTest {

    private static final int stateX[] = { -9, -8, -7, -6, -5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    public class RandomExcursionsVariantResult extends Result {

        private final int count;
        private int length;
        private int J;
        private int constraint;
        private int state;

        public RandomExcursionsVariantResult(int count, double p_value, int state) {
            super(p_value, "" + state);
            this.state = state;
            this.count = count;
        }

        public int getState() {
            return state;
        }

        public int getCount() {
            return count;
        }

        public int getLength() {
            return length;
        }

        public int getJ() {
            return J;
        }

        public int getConstraint() {
            return constraint;
        }
    }

    @Override
    public Result[] runTest(Bits b) {
        int length;
        int J;
        int constraint;
    
        length = b.getLength();
        int i, p, x, count, S_k[];

        S_k = new int[length];


        J = 0;
        S_k[0] = 2 * (int) b.bit(0) - 1;
        for (i = 1; i < length; i++) {
            S_k[i] = S_k[i - 1] + 2 * b.bit(i) - 1;
            if (S_k[i] == 0) {
                J++;
            }
        }
        if (S_k[length - 1] != 0) {
            J++;
        }

        constraint = (int) Math.max(0.005 * Math.pow(length, 0.5), 500);
        if (J < constraint) {

            RandomExcursionsVariantResult[] results = new RandomExcursionsVariantResult[18];
            for (p = 0; p <= 17; p++) {
                RandomExcursionsVariantResult r
                        = new RandomExcursionsVariantResult(0,
                                                            Double.NaN,
                                                            stateX[p]);
                
                r.length = length;
                r.J = J;
                r.constraint = constraint;
                
                results[p] = r;
            }

            return results;
        } else {
            RandomExcursionsVariantResult[] results = new RandomExcursionsVariantResult[18];
            for (p = 0; p <= 17; p++) {
                x = stateX[p];
                count = 0;
                for (i = 0; i < length; i++) {
                    if (S_k[i] == x) {
                        count++;
                    }
                }
                RandomExcursionsVariantResult r
                        = new RandomExcursionsVariantResult(count,
                                                            Erf.erfc(Math.abs(count - J) / (Math.sqrt(2.0 * J * (4.0 * Math.abs(x) - 2)))),
                                                            x);
                
                r.length = length;
                r.J = J;
                r.constraint = constraint;
                
                results[p] = r;
            }
            
            return results;
        }
       
    }

    @Override

    public void report(PrintWriter out, Result[] results) {
        out.println("RANDOM EXCURSIONS VARIANT TEST");
        out.println("--------------------------------------------");
        if (results == null) {
            out.println("ERROR: There are no results yet");
            out.println("- OR - WARNING:  TEST NOT APPLICABLE.  THERE ARE AN");
            out.println("INSUFFICIENT NUMBER OF CYCLES.");
            return;
        }
        
        RandomExcursionsVariantResult r = (RandomExcursionsVariantResult) results[0];
        
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("--------------------------------------------");
        out.printf("(a) Number Of Cycles (J) = %d\n", r.getJ());
        out.printf("(b) Sequence Length (n)  = %d\n", r.getLength());
        out.println("--------------------------------------------");
//        if (J < constraint) {
//            out.println("WARNING:  TEST NOT APPLICABLE.  THERE ARE AN");
//            out.println("INSUFFICIENT NUMBER OF CYCLES.");
//            out.println("---------------------------------------------");
//            for (int i = 0; i < 18; i++) {
//                out.printf("%f\n", 0.0);
//            }
//            return;
//        }
        for (int i = 0; i < 18; i++) {
            r = (RandomExcursionsVariantResult) results[i];
            if (r.getPValue() < 0.0 || r.getPValue() >= 1.0) {
                out.println("(b) WARNING: P_VALUE IS OUT OF RANGE.\n");
            }
            out.printf("%s\t\t", !r.isPassed() ? "FAILURE" : "SUCCESS");
            out.printf("(x = %2d) Total visits = %4d; p-value = %f\n", r.getState(), r.getCount(), r.getPValue());

        }
    }
    
    @Override
    public int getInputSizeRecommendation() {
        return 1000000;
    }
}
