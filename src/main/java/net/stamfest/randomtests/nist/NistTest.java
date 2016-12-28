/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * (c) 1999 by the National Institute Of Standards & Technology
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.nist;

import java.io.PrintWriter;
import net.stamfest.randomtests.Suite;
import net.stamfest.randomtests.bits.Bits;

/**
 * The highest level interface contract of any NIST test. An implementation is
 * expected to produce a list of results (@see {@link Result}). Every result
 * should at least produce a probability, that the tested bit sequence is
 * compatible with the assumption that it is purely random. This probability
 * itself should follow a uniform distribution, that is testing a large number
 * of truly random bit sequences should will produce probabilities in the range
 * [0..1] that are uniformly distributed over that range. This further allows to
 * combine many tests to assess if the process to generate the bit streams is
 * "random" or not (@see {@link Suite}).
 *
 * @author Peter Stamfest
 */
public interface NistTest {

    /**
     * Run the test for the given bits. One or more results get returned.
     *
     * @param bits The sequence of bits to test.
     * @return An array of Result objects describing the outcome(s) of the test.
     *         null may be returned to indicate that there is no result (yet) -
     *         which might mean that there were problems with running the test.
     *         Note: A test MUST NOT re-use Result objects or a Result-array it
     *         already returned for another test-run. This allows the caller to
     *         store Result objects as-is for different runs. A test MAY return
     *         such objects for multiple calls to this method as long as no new
     *         run (using runTest or an equivalent method) was done between such
     *         calls. Any implementation must produce the same number of results
     *         for every tested bit sequence (or null) in order to be compatible
     *         with test suites. Note that differently constructed objects of a
     *         class may produce different numbers of results, but any single
     *         object should never change the number of results it produces
     *         during its entire lifetime.
     */
    public Result[] runTest(Bits bits);

    /**
     * Write a report about the last run of the test to a PrintWriter.
     *
     * @param out Where to write the report to.
     * @param results The results to be reported.
     */
    public void report(PrintWriter out, Result[] results);

    /**
     * Returns a description (eg the name and parameters of the test) for human
     * presentation.
     *
     * @return The description.
     */
    public String getDescription();

    /**
     * NIST tests come with an input size recommendation - it refers to the
     * minimum number of bits to be tested by a test so that useful results can
     * be expected.
     *
     * @return The minimum recommended length of the bit sequence to the
     *         {@link #runTest(net.stamfest.randomtests.bits.Bits) runTest(Bits)}
     *         method.
     */
    public int getInputSizeRecommendation();
}
