/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests;

import java.lang.reflect.Constructor;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.stamfest.randomtests.nist.NistTest;

/**
 * A helper class to instantiate random number tests from textual
 * representations. This is useful for human input (like command line
 * arguments).
 *
 * A text representation is simply the class name of the {@link NistTest}
 * class to instantiate (including the package name in standard java notation.
 * The package name may be omitted if the class is within the package
 * net.stamfest.randomtests.nist).
 *
 * Optionally, the class name may be followed by a list of arguments within
 * parentheses. The argument list will be passed to the matchings constructor.
 *
 * @author Peter Stamfest
 */
public class TestFactory {

    private static volatile TestFactory theInstance = null;

    public static synchronized TestFactory getInstance() {
        if (theInstance == null) {
            theInstance = new TestFactory();
        }
        return theInstance;
    }

    private ServiceLoader<NistTestLookupServiceProvider> nistTestLoader;

    private TestFactory() {
        nistTestLoader = ServiceLoader.load(NistTestLookupServiceProvider.class);
    }

    /**
     * Holds a test specification. This is the parsed form of the textual
     * representation.
     */
    public static class TestSpec {

        private Class<NistTest> testClass;
        private String parameterList[];
        private Object[] parameters;
        private String definition = null;

        public NistTest getInstance() throws Exception {
            Class classes[] = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                classes[i] = parameters[i].getClass();
                if (classes[i].equals(Integer.class)) {
                    classes[i] = Integer.TYPE;
                }
                if (classes[i].equals(Double.class)) {
                    classes[i] = Double.TYPE;
                }
            }
            Constructor<NistTest> constr = testClass.getConstructor(classes);
            return constr.newInstance(parameters);
        }

        /**
         * Get the actual class corresponding to the specification.
         *
         * @return The {@link Class} object.
         */
        public Class<NistTest> getTestClass() {
            return testClass;
        }
        
        /**
         * Get the definition string (if any) that was used for this TestSpec.
         * @return The definition String.
         */
        public String getDefinition() {
            return definition;
        }
    }

    private static Pattern testSpecPattern = Pattern.compile("(?:(?<package>[a-zA-Z0-9._]+)\\.)?(?<class>[a-zA-Z0-9]+)(\\((?<parameters>([0-9\\\\.]+)(,([0-9\\\\.]+))*)\\))?");

    /**
     * Parse the textual representation into a TestSpec object.
     *
     * @param textSpec The textual representation to parse.
     * @return The parsed TestSpec
     * @throws InvalidTestSpec Thrown if the textSpec cannot be parsed.
     */
    @SuppressWarnings("unchecked")
    public TestSpec parseTestSpec(String textSpec) throws InvalidTestSpec {
        Matcher m = testSpecPattern.matcher(textSpec);
        if (!m.matches()) {
            throw new InvalidTestSpec(String.format("invalid test syntax '%s'", textSpec));
        }

        try {
            Class<?> c = null;
            String packageName = m.group("package");
            String baseClassName = m.group("class");
            
            // try all service providers if no explicit package was given
            if (packageName == null || packageName.isEmpty()) {
                for (NistTestLookupServiceProvider ntl : nistTestLoader) {
                    c = ntl.lookup(baseClassName);
                    if (c != null) {
                        break;
                    }
                }
            } else {
                String className = packageName + "." + baseClassName;
                c = Class.forName(className);
            }

            // check if named class really is a NistTest
            if (c != null && NistTest.class.isAssignableFrom(c)) {
                TestSpec spec = new TestSpec();
                spec.definition = textSpec;
                spec.testClass = (Class<NistTest>) c;
                String parametersStr = m.group("parameters");

                spec.parameterList = parametersStr != null ? parametersStr.split(",") : new String[0];
                spec.parameters = new Object[spec.parameterList.length];

                for (int i = 0; i < spec.parameterList.length; i++) {
                    String param = spec.parameterList[i];

                    try {
                        spec.parameters[i] = Integer.parseInt(param);
                        continue;
                    } catch (Exception e) {
                    }
                    try {
                        spec.parameters[i] = Double.parseDouble(param);
                    } catch (Exception e) {
                        spec.parameters[i] = param;
                    }
                }
                return spec;
            } else {
                throw new InvalidTestSpec(String.format("'%s' is not a NistTest",
                                                        textSpec));
            }
        } catch (ClassNotFoundException ex) {
            throw new InvalidTestSpec(String.format("'%s' is not a proper test class name",
                                                    textSpec));
        }
    }
}
