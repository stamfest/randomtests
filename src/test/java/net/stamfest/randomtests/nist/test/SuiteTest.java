/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.nist.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import net.stamfest.randomtests.ParallelSuiteExecutor;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.Suite;
import net.stamfest.randomtests.nist.NistTest;
import net.stamfest.randomtests.utils.Histogram;
import net.stamfest.randomtests.utils.IO;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class SuiteTest {
    @Test
    public void BBS() throws IOException {
        InputStream is = SuiteTest.class.getResourceAsStream("/BBS.dat");

        Suite s = Suite.getStandardTestSuite(null);

        for (int i = 0; i < 100; i++) {
            Bits bits = IO.readBinary(is, 500000);
            s.runSuite(bits);
        }
        
        PrintWriter out = new PrintWriter(System.out);
        s.report(out);
        out.flush();

    }
    
    
    /**
     * This test checks if the original nist code and this java implementation
     * yield the same results even for the full run of the test suite over 100
     * bit sequences with 500000 bits each from the data/BBS.dat file from the
     * original nist source code. * @throws IOException
     */
    @Test
    public void parallelBBS() throws IOException {
        InputStream is = SuiteTest.class.getResourceAsStream("/BBS.dat");

        ParallelSuiteExecutor s = ParallelSuiteExecutor.getStandardParallelTestSuite(null);

        for (int i = 0; i < 100; i++) {
            Bits bits = IO.readBinary(is, 500000);
            s.runSuite(bits);
        }
        
        // need to wait for final results...
        s.await();
        
        InputStream resultsInputStream = SuiteTest.class.getResourceAsStream("/BBS-test-results");
        
        Iterator<List<String>> iterator = parseFile(resultsInputStream);
        
        int n = 0;
        List<NistTest> tests = s.getTests();
        for (NistTest test : tests) {
            List<Histogram> histos = s.getHistogramsForTest(test);
            Assert.assertNotNull(histos);
            for (Histogram histo : histos) {
                Assert.assertTrue(iterator.hasNext());
                if (iterator.hasNext()) {
                    n++;
                    List<String> r = iterator.next();
                    
                    int l = histo.getBinCount();

                    for (int i = 0 ; i < l ; i++) {
                        Assert.assertEquals(String.format("%d/%d", n, i), Integer.parseInt(r.get(i)), histo.getBin(i));
                    }
                    Assert.assertEquals(String.format("p-Value(%d)=%s", n, r.get(10)), 
                                        Double.parseDouble(r.get(10)), 
                                        histo.uniformity(), 
                                        0.000001);
                    Assert.assertEquals(String.format("pass(%d)=%s", n, r.get(11)), 
                                        Integer.parseInt(r.get(11)), 
                                        histo.getPassCount());
                    Assert.assertEquals(String.format("total(%d)=%s", n, r.get(12)), 
                                        Integer.parseInt(r.get(12)), 
                                        histo.getTotal());
                }
            }
        }
        Assert.assertFalse("result iterator should have no more results available",
                           iterator.hasNext());

        PrintWriter out = new PrintWriter(System.out);
        s.report(out);
        out.flush();
    }

    @Test
    public void dataE() throws IOException {
        InputStream is = SuiteTest.class.getResourceAsStream("/data.e");

        Suite s = Suite.getStandardTestSuite(null);

        for (int i = 0; i < 100; i++) {
            Bits bits = IO.readAscii(is, 10000);
            s.runSuite(bits);
        }
        
        PrintWriter out = new PrintWriter(System.out);
        s.report(out);
        out.flush();

    }
    
   //  @Test
    public void xx() throws IOException {
        InputStream resultsInputStream = SuiteTest.class.getResourceAsStream("/BBS-test-results");
        Iterator<List<String>> iterator = parseFile(resultsInputStream);
        for ( ; iterator.hasNext();) {
            List<String> n = iterator.next();
            for (String str : n) {
                
            }
        }
    }
    
    public Iterator<List<String>> parseFile(InputStream is) throws IOException {
        // InputStream is = SuiteTest.class.getResourceAsStream("/BBS-test-results");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        /*
          7  13  10   9   9  12   7   9  12  12  0.897763     99/100     NonOverlappingTemplate
        */
        Pattern p = 
                Pattern.compile("^\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+([0-9.]+)\\s+(?:\\*\\s*)?(\\d+)/(\\d+)\\s*(?:\\*\\s*)?\\S+$");        
    
        return new Iterator<List<String>>() {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            List<String> next = null;
            @Override
            public boolean hasNext() {
                if (next != null) {
                    return true;
                }
                if (br == null) {
                    return false;
                }
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        Matcher m = p.matcher(line);
                        if (m.matches()) {
                            next  = new ArrayList<>();
                            
                            int cnt = m.groupCount();
                            for (int i = 1 ; i <= cnt ; i++) {
                                next.add(m.group(i));
                            }
                            return true;
                        }
                    }
                } catch (IOException ex) {
                }
                try {
                    br.close();
                } catch (IOException ex) {
                }
                br = null;
                return false;
            }

            @Override
            public List<String> next() {
                List<String> n = next;
                next = null;
                return n;
            }
        };
    
    }
}
