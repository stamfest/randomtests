/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * (c) 1999 by the National Institute Of Standards & Technology
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.nist;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 *
 * @author Peter Stamfest
 */
public abstract class AbstractNistTest implements NistTest {
    /**
     * Convenience method to use for eg. System.out.
     *
     * @param out     A print stream to write to. The stream gets wrapped with
     *                an internal PrintWriter and the request gets passed on to
     *                {@link NistTest#report(java.io.PrintWriter, Result[])}
     * @param results The results to report. This should be one of the array
     *                previously returned by the runTest method.
     */
    public final void report(PrintStream out, Result[] results) {
        PrintWriter pw = new PrintWriter(out);
        report(pw, results);
        pw.flush();
    }

    @Override
    public String getDescription() {
        return getClass().getSimpleName();
    }
}
