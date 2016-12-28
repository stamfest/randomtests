package net.stamfest.randomtests;

import java.util.List;
import net.stamfest.randomtests.nist.Result;

/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */

/**
 * SuiteResult objects are use to hold the results for all tests executed in a
 * single run of a test suite, that is for a single bit sequence. Essentially, a
 * SuiteResult holds a list of Result arrays, where every list element (a Result
 * array) corresponds to the test at the same index within the list of tests of
 * the test Suite.
 *
 * @author Peter Stamfest
 */
public interface SuiteResult {

    /**
     * Retrieve the list of result array for the run of the test suite this
     * object corresponds to.
     *
     * @return A list of Result arrays. Every list element corresponds to the
     *         test at the same index within the list of tests from the Suite.
     */
    public List<Result[]> getResults();

    /**
     * Returns the run number the results correspond to.
     *
     * @return The run number.
     */
    public int getRun();
}
