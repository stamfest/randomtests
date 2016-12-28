/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests;

import net.stamfest.randomtests.nist.NistTest;

/**
 * This implementation is extensible for statistical tests implementing the
 * {@link NistTest} interface. To this end, it uses the standard Java
 * ServiceProvider infrastructure as pointed out in the documentation to the
 * {@link java.util.ServiceLoader} class in order to find implementations of
 * this class.
 *
 * To find test implementations, a {@link TestFactory} uses all available
 * providers to look up the test name through the lookup method of the provider.
 * The first found implementation will be uses.
 *
 * @author Peter Stamfest
 */
public interface NistTestLookupServiceProvider {

    /**
     * Lookup the test name.
     *
     * @param testName The test name to look up.
     * @return A class object referring to a class implementing the NistTest
     *         interface or null if the provider does not implement the test.
     */
    public Class<? extends NistTest> lookup(String testName);
}
