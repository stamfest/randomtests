package net.stamfest.randomtests;

/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
import net.stamfest.randomtests.nist.NistTest;

/**
 * The service provider for the standard NIST tests.
 *
 * @author Peter Stamfest
 */
public class StandardNistTestLookupServiceProvider implements NistTestLookupServiceProvider {

    private static final String defaultPackageName = NistTest.class.getPackage().getName();

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends NistTest> lookup(String baseClassName) {
        String className = defaultPackageName + "." + baseClassName;
        try {
            Class c = Class.forName(className);
            if (c == null || !NistTest.class.isAssignableFrom(c)) {
                return null;
            }
             
            return (Class<? extends NistTest>) c;
        } catch (ClassNotFoundException ex) {
            return null;
        } catch (ClassCastException ex) {
            return null;
        }
    }
}
