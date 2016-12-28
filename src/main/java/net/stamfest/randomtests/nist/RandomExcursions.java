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
 *
 * The focus of this test is the number of cycles having exactly K visits in a
 * cumulative sum random walk. The cumulative sum random walk is derived from
 * partial sums after the (0,1) sequence is transferred to the appropriate (-1,
 * +1) sequence. A cycle of a random walk consists of a sequence of steps of
 * unit length taken at random that begin at and return to the origin. The
 * purpose of this test is to determine if the number of visits to a particular
 * state within a cycle deviates from what one would expect for a random
 * sequence. This test is actually a series of eight tests (and conclusions),
 * one test and conclusion for each of the states: -4, -3, -2, -1 and +1, +2,
 * +3, +4.
 *
 * @author Peter Stamfest
 */
public class RandomExcursions extends AbstractNistTest {
    private final int stateX[] = { -4, -3, -2, -1, 1, 2, 3, 4 };
    private final double pi[][] = {
        { 0.0000000000, 0.00000000000, 0.00000000000, 0.00000000000, 0.00000000000, 0.0000000000 },
        { 0.5000000000, 0.25000000000, 0.12500000000, 0.06250000000, 0.03125000000, 0.0312500000 },
        { 0.7500000000, 0.06250000000, 0.04687500000, 0.03515625000, 0.02636718750, 0.0791015625 },
        { 0.8333333333, 0.02777777778, 0.02314814815, 0.01929012346, 0.01607510288, 0.0803755143 },
        { 0.8750000000, 0.01562500000, 0.01367187500, 0.01196289063, 0.01046752930, 0.0732727051 } };

    public class RandomExcursionsResult extends Chi2Result {
        private int x;
        private int length;
        private int J;
        private double constraint;

        public RandomExcursionsResult(double p_value, double chi2, int degrees, int x) {
            super(p_value, chi2, degrees, "" + x);
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public int getLength() {
            return length;
        }

        public int getJ() {
            return J;
        }

        public double getConstraint() {
            return constraint;
        }
    }

    @Override
    public Result[] runTest(Bits epsilon) {
        int n = epsilon.getLength();
        int J;
        double constraint;

        int b, i, j, k, x;
        int cycleStart, cycleStop, cycle[], S_k[];
        int counter[] = { 0, 0, 0, 0, 0, 0, 0, 0 };
        double sum, nu[][] = new double[6][8];

        S_k = new int[n];
        cycle = new int[Math.max(1000, n / 100)];

        J = 0;
        /* DETERMINE CYCLES */
        S_k[0] = 2 * (int) epsilon.bit(0) - 1;
        for (i = 1; i < n; i++) {
            S_k[i] = S_k[i - 1] + 2 * epsilon.bit(i) - 1;
            if (S_k[i] == 0) {
                J++;
                if (J > Math.max(1000, n / 100)) {
                    throw new IndexOutOfBoundsException("ERROR IN randomExcursions: EXCEEDING THE MAX NUMBER OF CYCLES EXPECTED");
                }
                cycle[J] = i;
            }
        }
        if (S_k[n - 1] != 0) {
            J++;
        }
        cycle[J] = n;

        constraint = Math.max(0.005 * Math.pow(n, 0.5), 500);
        if (J < constraint) {
            /* The NIST publications section 3.14 indicates that tests should 
            be rejected if this constraint is not met. So we do this... */
            
            RandomExcursionsResult[] results = new RandomExcursionsResult[8];

            for (i = 0; i < 8; i++) {
                x = stateX[i];

                RandomExcursionsResult r
                        = new RandomExcursionsResult(Double.NaN, Double.NaN, 5, x);

                r.J = J;
                r.constraint = constraint;
                r.length = n;

                results[i] = r;
            }

            return results;
        }
        cycleStart = 0;
        cycleStop = cycle[1];
        for (k = 0; k < 6; k++) {
            for (i = 0; i < 8; i++) {
                nu[k][i] = 0.;
            }
        }
        for (j = 1; j <= J; j++) {
            /* FOR EACH CYCLE */
            for (i = 0; i < 8; i++) {
                counter[i] = 0;
            }
            for (i = cycleStart; i < cycleStop; i++) {
                if ((S_k[i] >= 1 && S_k[i] <= 4) || (S_k[i] >= -4 && S_k[i] <= -1)) {
                    if (S_k[i] < 0) {
                        b = 4;
                    } else {
                        b = 3;
                    }
                    counter[S_k[i] + b]++;
                }
            }
            cycleStart = cycle[j] + 1;
            if (j < J) {
                cycleStop = cycle[j + 1];
            }

            for (i = 0; i < 8; i++) {
                if ((counter[i] >= 0) && (counter[i] <= 4)) {
                    nu[counter[i]][i]++;
                } else if (counter[i] >= 5) {
                    nu[5][i]++;
                }
            }
        }
        RandomExcursionsResult[] results = new RandomExcursionsResult[8];

        for (i = 0; i < 8; i++) {
            x = stateX[i];
            sum = 0.;
            for (k = 0; k < 6; k++) {
                sum += Math.pow(nu[k][i] - J * pi[(int) Math.abs(x)][k], 2) / (J * pi[(int) Math.abs(x)][k]);
            }

            RandomExcursionsResult r
                    = new RandomExcursionsResult(Gamma.regularizedGammaQ(2.5, sum / 2.0),
                                                 sum, 5, x);
            
            r.J = J;
            r.constraint = constraint;
            r.length = n;
           
            results[i] = r;
        }
        return results; 
    }

    @Override
    public void report(PrintWriter out, Result[] results) {
        out.println("RANDOM EXCURSIONS TEST");
        out.println("--------------------------------------------");
        if (results == null) {
            out.println("ERROR: There are no results yet");
            out.println("---------------------------------------------");
            out.println("OR WARNING:  TEST NOT APPLICABLE.  THERE ARE AN");
            out.println("INSUFFICIENT NUMBER OF CYCLES.");
            out.println("---------------------------------------------");
            return;
        }
        RandomExcursionsResult r = (RandomExcursionsResult) results[0];
        
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("--------------------------------------------");
        out.printf("(a) Number Of Cycles (J) = %04d\n", r.getJ());
        out.printf("(b) Sequence Length (n)  = %d\n", r.getLength());
        out.printf("(c) Rejection Constraint = %f\n", r.getConstraint());
        out.println("--------------------------------------------");

        for (int i = 0; i < 8; i++) {
            r = (RandomExcursionsResult) results[i];
            if (r.getPValue() < 0.0 || r.getPValue() > 1.0) {
                out.println("WARNING:  P_VALUE IS OUT OF RANGE.");
            }

            out.printf("%s\t\tx = %2d chi^2 = %9.6f p_value = %f\n",
                       !r.isPassed() ? "FAILURE" : "SUCCESS", r.getX(), r.getChi2(), r.getPValue());
        }
    } 
    
    @Override
    public int getInputSizeRecommendation() {
        return 1000000;
    }
}
