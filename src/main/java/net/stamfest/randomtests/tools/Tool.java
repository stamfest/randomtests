/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.tools;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;

/**
 * A tool manager class for the jar packaging
 * @author Peter Stamfest
 */
public class Tool {
    public static void main(String argv[]) {
        if (argv.length == 0) {
            System.err.println("Missing tool name! See the documentation for possible tool\n"
                    + "names. Or have a look at this (possibly incomplete) list:");
            for(String n: new String[] {"Assess", "Transform"}) {
                System.err.println(n);
            }
            
            System.exit(126);
        } 
        
        String tool = argv[0];
        try {
            String want = Tool.class.getPackage().getName() + "." + tool;
            
            Class<?> c = Class.forName(want);
            Method mainMethod = c.getMethod("main", (new String[0]).getClass());
            
            /* throw away tool name from the original command line */
            String argv2[] = new String[argv.length-1];
            for (int i = 1, j = 0; i < argv.length; i++, j++) {
                argv2[j] = argv[i];
            }
            
            mainMethod.invoke(null, new Object[] { argv2 } );
        } catch (Exception ex) {
            System.err.printf("invalid tool name: '%s'\n", ex.getMessage());
            ex.printStackTrace();
            System.exit(125);
        }
    }
}