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
 * The focus of the test is the proportion of ones within M-bit blocks. The
 * purpose of this test is to determine whether the frequency of ones in an
 * M-bit block is approximately M/2, as would be expected under an assumption of
 * randomness. For block size M =1, this test degenerates to the
 * {@link Frequency} (Monobit) test.
 *
 * @author Peter Stamfest
 */
public class BlockFrequency extends AbstractNistTest {
    public static class BlockFrequencyResult extends Chi2Result {
        public BlockFrequencyResult(double p_value, double chi2, int degrees) {
            super(p_value, chi2, degrees, null);
        }
        private int blockLen;
        private int discarded;
        private int blockCount;

        public int getBlockCount() {
            return blockCount;
        }

        public int getBlockLen() {
            return blockLen;
        }

        public int getDiscarded() {
            return discarded;
        }
    }
    private final int blockLen;

    /**
     * Constructs a test for a given block length.
     *
     * @param blockLenM The block length to test. This is the parameter M from
     *                  the NIST publication.
     */
    public BlockFrequency(int blockLenM) {
        this.blockLen = blockLenM;
    }

    @Override
    public Result[] runTest(Bits b) {
        double chi_squared;
        int blockCount;

        int n = b.getLength();
        int discarded = n % blockLen;

        int i, j, blockSum;
        double sum, pi, v;

        blockCount = n / blockLen;
        /* # OF SUBSTRING BLOCKS      */
        sum = 0.0;

        for (i = 0; i < blockCount; i++) {
            blockSum = 0;
            int offset = i * blockLen;
            for (j = 0; j < blockLen; j++) {
                blockSum += b.bit(offset + j);
            }
            pi = (double) blockSum / (double) blockLen;
            v = pi - 0.5;
            sum += v * v;
        }
        chi_squared = 4.0 * blockLen * sum;
        //p_value = cephes_igamc(blockCount/2.0, chi_squared/2.0);
        
        BlockFrequencyResult r = new BlockFrequencyResult(Gamma.regularizedGammaQ(blockCount / 2.0, chi_squared / 2.0),
                                                          chi_squared, blockCount);

        r.discarded = discarded;
        r.blockLen = blockLen;
        r.blockCount = blockCount;
        
        return new Result[] { r };
    }


    @Override
    public void report(PrintWriter out, Result[] results) {
        out.println("BLOCK FREQUENCY TEST");
        out.println("---------------------------------------------");
        if (results == null) {
            out.println("ERROR: There is no result yet");
            return;
        }

        BlockFrequencyResult r = (BlockFrequencyResult) results[0];
        
        out.println("COMPUTATIONAL INFORMATION:");
        out.println("---------------------------------------------");
        out.printf("(a) Chi^2           = %f\n", r.getChi2());
        out.printf("(b) # of substrings = %d\n", r.getBlockCount());
        out.printf("(c) block length    = %d\n", r.getBlockLen());
        out.printf("(d) Note: %d bits were discarded.\n", r.getDiscarded());
        out.println("---------------------------------------------");

        out.printf("%s p_value = %f\n\n", r.isPassed() ? "SUCCESS" : "FAILURE", r.getPValue());
    }

    @Override
    public int getInputSizeRecommendation() {
        return 100;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + String.format("(%d)", blockLen);
    }
}
