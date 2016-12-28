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
 * The focus of this test is the frequency of all possible overlapping blockLength -bit
 patterns across the entire sequence. The purpose of this test is to determine
 whether the number of occurrences of the 2 blockLength blockLength -bit overlapping patterns is
 approximately the same as would be expected for a random sequence. Random
 sequences have uniformity; that is, every blockLength -bit pattern has the same chance
 of appearing as every other blockLength -bit pattern. Note that for blockLength = 1, the Serial
 test is equivalent to the Frequency test.
 *
 * @author NIST / ported by Peter Stamfest
 */
public class Serial extends AbstractNistTest {

    public static class SerialResult extends Result {
        private double psim0, psim1, psim2, del1, del2;
        private int length;
        private int blockLength;

        /**
         *
         * @param p_value the value of p_value
         * @param description the value of description
         */
        public SerialResult(double p_value, String description) {
            super(p_value, description);
        }

        public double getPsim0() {
            return psim0;
        }

        public double getPsim1() {
            return psim1;
        }

        public double getPsim2() {
            return psim2;
        }

        public double getDel1() {
            return del1;
        }

        public double getDel2() {
            return del2;
        }

        public int getLength() {
            return length;
        }

        public int getBlockLength() {
            return blockLength;
        }
        
    }
    private final int m;
    private final int recommendation;
    private final double ln2 = Math.log(2.0);

    public Serial(int m) {
        this.m = m;
        this.recommendation = (int) Math.exp((m + 2.0) * ln2);

    }
    

    @Override
    public Result[] runTest(Bits b) {
        double psim0, psim1, psim2, del1, del2;
        int length = b.getLength();

        psim0 = psi2(m, b);
        psim1 = psi2(m - 1, b);
        psim2 = psi2(m - 2, b);
        del1 = psim0 - psim1;
        del2 = psim0 - 2.0 * psim1 + psim2;
        double p_value1 = Gamma.regularizedGammaQ(Math.pow(2, m - 1) / 2, del1 / 2.0);
        double p_value2 = Gamma.regularizedGammaQ(Math.pow(2, m - 2) / 2, del2 / 2.0);

        SerialResult r1 = new SerialResult(p_value1, "1");
        r1.del1 = del1;
        r1.del2 = del2;
        r1.length = length;
        r1.psim0 = psim0;
        r1.psim1 = psim1;
        r1.psim2 = psim2;
        r1.blockLength = m;
        
        SerialResult r2 = new SerialResult(p_value2, "2");
        r2.del1 = del1;
        r2.del2 = del2;
        r2.length = length;
        r2.psim0 = psim0;
        r2.psim1 = psim1;
        r2.psim2 = psim2;
        r2.blockLength = m;
        
        return new Result[] { r1, r2 };
    }

    private double psi2(int m, Bits b) {
        int length = b.getLength();
        double p_value1, p_value2, psim0, psim1, psim2, del1, del2;

        int i, j, k, powLen;
        double sum, numOfBlocks;
        int P[];

        if ((m == 0) || (m == -1)) {
            return 0.0;
        }
        numOfBlocks = length;
        powLen = (int) Math.pow(2, m + 1) - 1;

        P = new int[powLen];
        for (i = 1; i < powLen - 1; i++) {
            P[i] = 0;
            /* INITIALIZE NODES */
        }
        for (i = 0; i < numOfBlocks; i++) {
            /* COMPUTE FREQUENCY */
            k = 1;
            for (j = 0; j < m; j++) {
                if (b.bit((i + j) % length) == 0) {
                    k *= 2;
                } else if (b.bit((i + j) % length) == 1) {
                    k = 2 * k + 1;
                }
            }
            P[k - 1]++;
        }
        sum = 0.0;
        for (i = (int) Math.pow(2, m) - 1; i < (int) Math.pow(2, m + 1) - 1; i++) {
            sum += Math.pow(P[i], 2);
        }
        sum = (sum * Math.pow(2, m) / (double) length) - (double) length;

        return sum;

    }

    @Override
    public void report(PrintWriter out, Result[] results) {
        out.println("SERIAL TEST");
        out.println("---------------------------------------------");
        if (results == null) {
            out.println("ERROR: There are no results yet");
            return;
        }

        SerialResult r1 = (SerialResult) results[0];
        SerialResult r2 = (SerialResult) results[1];
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("---------------------------------------------");
        out.printf("(a) Block length    (m) = %d\n", r1.getBlockLength());
        out.printf("(b) Sequence length (n) = %d\n", r1.getLength());
        out.printf("(c) Psi_m               = %f\n", r1.getPsim0());
        out.printf("(d) Psi_m-1             = %f\n", r1.getPsim1());
        out.printf("(e) Psi_m-2             = %f\n", r1.getPsim2());
        out.printf("(f) Del_1               = %f\n", r1.getDel1());
        out.printf("(g) Del_2               = %f\n", r1.getDel2());
        out.println("---------------------------------------------");
        out.printf("%s\t\tp_value1 = %f\n", ! r1.isPassed() ? "FAILURE" : "SUCCESS", r1.getPValue());
        out.printf("%s\t\tp_value2 = %f\n", ! r2.isPassed() ? "FAILURE" : "SUCCESS", r2.getPValue());
    }
    
    @Override
    public int getInputSizeRecommendation() {
        return recommendation;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + String.format("(%d)", m);
    }
}
