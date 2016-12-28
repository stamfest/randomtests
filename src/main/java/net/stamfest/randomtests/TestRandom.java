/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.nist.BlockFrequency;
import net.stamfest.randomtests.nist.Frequency;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.nist.Runs;
import net.stamfest.randomtests.utils.IO;

/**
 *
 * @author Peter Stamfest
 */
public class TestRandom {

    public static void main(String argv[]) throws IOException {
        Bits b;
        if (argv.length > 0) {
            b = IO.readBinary(new FileInputStream(argv[0]), Integer.MAX_VALUE);
        } else {
            b = IO.readBinary(System.in, Integer.MAX_VALUE);
        }

        PrintWriter pw = new PrintWriter(System.out);
        Frequency f = new Frequency();
        Result[] r = f.runTest(b);
        f.report(pw, r);

        BlockFrequency bf = new BlockFrequency(50);
        r = bf.runTest(b);
        bf.report(pw, r);

        Runs runs = new Runs();
        r = runs.runTest(b);
        runs.report(pw, r);
    }
}
