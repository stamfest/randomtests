/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * (c) 1999 by the National Institute Of Standards & Technology
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.nist.NistTest;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.utils.Histogram;

/**
 * A "test suite" class holding a list of Test implementations. All tests can be
 * applied to a single bit sequence at once. A test suite also accumulates
 * results from testing multiple bit sequences. As pointed out in the
 * documentation to the {@link NistTest} class, the idea is, that every single
 * test produces a probability that the given bit sequence is compatible with
 * the assumption that it is random.
 *
 * A test suite uses this property to fill histograms from tests. Every set of
 * histograms can be retrieved from the test suite to investigate the combined
 * results from all test runs.
 *
 *
 * @author Peter Stamfest
 */
public class Suite {

    /* SIGNIFICANCE LEVEL */
    public static double ALPHA = 0.01;

    List<NistTest> tests = new ArrayList<>();
    Map<NistTest, List<Histogram>> histomap = new HashMap<>();

    AtomicInteger runCounter = new AtomicInteger(0);

    /**
     * Creates the NIST "standard test suite". This factory method constructs a
     * test suite containing all the tests from the NIST publication.
     *
     * In order to parametrise the tests, this method takes a Properties object.
     * It can contain the following properties (they are shown together with
     * their default values):
     *
     * <pre>
     * blockFrequencyBlockLength = 128;
     * nonOverlappingTemplateBlockLength = 9;
     * overlappingTemplateBlockLength = 9;
     * approximateEntropyBlockLength = 10;
     * serialBlockLength = 16;
     * inearComplexitySequenceLength = 500;
     * </pre>
     *
     * @param settings The settings object.
     * @return The standard NIST test suite.
     */
    public static Suite getStandardTestSuite(Properties settings) {
        return new Suite(getStandardTestList());
    }

    /**
     * Get a list of standard tests.
     *
     * @return The list of standard test-
     */
    static public List<NistTest> getStandardTestList() {
        List<NistTest> tests = new ArrayList<>();
        TestFactory testFactory = TestFactory.getInstance();

        for (String testSpec : getStandardTestSpecs()) {
            try {
                TestFactory.TestSpec spec = testFactory.parseTestSpec(testSpec);
                tests.add(spec.getInstance());
            } catch (Exception ex) {
                Logger.getLogger(Suite.class.getName()).log(Level.SEVERE,
                                                            "internal implementation error",
                                                            ex);
            }
        }

        return tests;
    }

    static public List<String> getStandardTestSpecs() throws NumberFormatException {
        List<String> tests = new ArrayList<>();
        tests.add("Frequency");
        tests.add("BlockFrequency(128)");
        tests.add("CumulativeSums");
        tests.add("Runs");
        tests.add("LongestRunOfOnes");
        tests.add("Rank");
        tests.add("DiscreteFourierTransform");
        tests.add("NonOverlappingTemplateMatchings(9)");
        tests.add("OverlappingTemplateMatching(9)");
        tests.add("Universal");
        tests.add("ApproximateEntropy(10)");
        tests.add("RandomExcursions");
        tests.add("RandomExcursionsVariant");
        tests.add("Serial(16)");
        tests.add("LinearComplexity(500)");
        return tests;
    }

    /**
     * Creates a test suite from a list of NistTest objects.
     *
     * @param tests The list of tests to create the test suite from.
     */
    public Suite(List<NistTest> tests) {
        this.tests = new ArrayList<>();
        this.tests.addAll(tests);

        resetHistograms();
    }

    private void resetHistograms() {
        for (NistTest test : tests) {
            histomap.put(test, new ArrayList<>());
        }
    }

    /**
     * Returns the smallest input size recommendation compatible with all tests
     * within the suite. This is a convenience method to find the smallest
     * useful length of bit sequences that should produce meaningful results for
     * all tests.
     *
     * @return The test suite input size recommendation.
     */
    public int getInputSizeRecommendation() {
        int rec = 0;
        for (NistTest test : tests) {
            rec = Math.max(rec, test.getInputSizeRecommendation());
        }
        return rec;
    }

    /**
     * Run all tests for the passed bit sequence. The results from multiple test
     * runs get combined using internal histograms. This method returns all the
     * results for all tests executed through the returned SuiteResult object.
     * Every call to this method returns a new SuiteResult object. Every call
     * also increments an internal counter. The value of this counter can also
     * be retrieved from the SuiteResult object returned.
     *
     * @param b The bit sequence to test using all tests from the suite.
     * @return A SuiteResult object holding all results for all tests performed.
     */
    public SuiteResult runSuite(Bits b) {
        int runCnt = runCounter.getAndIncrement();
        ArrayList<Result[]> allresults = new ArrayList<>(tests.size());
        for (NistTest test : tests) {
            List<Histogram> histos = histomap.get(test);
            Result[] results = test.runTest(b);
            allresults.add(results);
            if (results == null) {
                // something was wrong with this test...
                continue;
            }
            if (histos.size() < results.length) {
                synchronized (histos) {
                    for (int i = histos.size(); i < results.length; i++) {
                        Histogram h = new Histogram(10, 0.0, 1.0);
                        h.setDescription(results[i].getDescription());
                        h.setPassValue(ALPHA);
                        histos.add(h);
                    }
                }
            }
            for (int i = 0; i < results.length; i++) {
                Result result = results[i];
                Histogram histo = histos.get(i);

                histo.update(result.getPValue());
            }
        }
        return new SuiteResult() {
            @Override
            public int getRun() {
                return runCnt;
            }

            @Override
            public List<Result[]> getResults() {
                return allresults;
            }
        };
    }

    /**
     * Retrieve a list of Histogram objects corresponding to the result lists
     * obtained from a runCounter of the passed in test.
     *
     * @param test The test object to get the list of Histogram objects for.
     *             This must be one of the tests corresponding for this test
     *             suite instance.
     * @return A list of Histogram objects corresponding to the passed test or
     *         null if the test is not part of the suite or non of the test runs
     *         have created any results or no tests have been runCounter so far.
     */
    public List<Histogram> getHistogramsForTest(NistTest test) {
        return histomap.get(test);
    }

    /**
     * Get the list of tests that are part of this test suite.
     *
     * @return The list of tests.
     */
    public List<NistTest> getTests() {
        return tests;
    }

    /**
     * Generate a standard cumulative report for all executed tests.
     *
     * @param out A PrintWriter to generate the report to.
     */
    public void report(PrintWriter out) {

        String fmt = "%3d ";
        reportHeader(out, fmt);

        for (NistTest test : tests) {
            List<Histogram> histos = histomap.get(test);
            for (Histogram histo : histos) {
                out.printf("%s %s", histo.toString(fmt), test.getDescription());
                String hdescr = histo.getDescription();
                if (hdescr != null) {
                    out.printf("[%s]", hdescr);
                }
                out.println();
            }
        }
        out.flush();
    }

    /**
     * Reset all statistics.
     */
    public void reset() {
        resetHistograms();
    }

    private static final String HEADERLINE2_1 = " P-VALUE PROPORTION ";
    private static final String HEADERLINE2_2 = "STATISTICAL TEST";

    /**
     * A mostly internal helper method to format a header block for printing
     * histograms. Let's not talk about it...
     *
     * @param fmt      The format that will be used for the corresponding call
     *                 to {@link #toString(String)}.
     * @param binCount For how many bins should the header be prepared.
     * @return The formatted header.
     */
    private void reportHeader(PrintWriter out, String fmt) {
        String example = String.format(fmt, 0);

        int extraSpace = HEADERLINE2_2.length();
        int binCount = 0;
        
        for (NistTest test : tests) {
            List<Histogram> histos = histomap.get(test);
            String tdescr = test.getDescription();
            for (Histogram histo : histos) {
                String hdescr = histo.getDescription();
                extraSpace = Math.max(extraSpace,
                                      ((hdescr != null) ? hdescr.length() : 0)
                                      + ((tdescr != null) ? tdescr.length() : 0));
                binCount = Math.max(binCount, histo.getBinCount());
            }
        }

        if (binCount == 0) {
            // this means: there are no results to report.... skip header
            return;
        }
        int total = example.length() * binCount // The "C#" parts + spacing
                + HEADERLINE2_1.length()        // P-VALUE PROPORTION part
                + extraSpace                    // the longest test/histo description
                + 2;                            // square brackets around histo description
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < total; i++) {
            sb.append('-');
        }
        String headerLine1 = sb.toString();

        out.println(headerLine1);
        out.println("RESULTS FOR THE UNIFORMITY OF P-VALUES AND THE PROPORTION OF PASSING SEQUENCES");

        //        out.println("------------------------------------------------------------------------------");
        //        out.println(" C1  C2  C3  C4  C5  C6  C7  C8  C9 C10  P-VALUE PROPORTION STATISTICAL TEST");
        //        out.println("------------------------------------------------------------------------------");
        //
        out.println(headerLine1);

        sb = new StringBuilder();
        for (int i = 1; i <= binCount; i++) {
            String s = String.format("C%d ", i);
            sb.append("                   ".substring(0, example.length() - s.length()));
            sb.append(s);
        }
        out.printf("%s%s%s\n", sb.toString(), HEADERLINE2_1, HEADERLINE2_2);
        out.println(headerLine1);
    }

}
