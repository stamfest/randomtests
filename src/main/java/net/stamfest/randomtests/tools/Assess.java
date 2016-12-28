/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.tools;

import net.stamfest.randomtests.InvalidTestSpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.stamfest.randomtests.ParallelSuiteExecutor;
import net.stamfest.randomtests.Suite;
import net.stamfest.randomtests.SuiteResult;
import net.stamfest.randomtests.TestFactory;
import net.stamfest.randomtests.TestFactory.TestSpec;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.bits.RNGBits;
import net.stamfest.randomtests.nist.NistTest;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.utils.IO;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

/**
 * This class implements a user-level command to run test suits against bit
 * sequences to assess the randomness of those sequences. It is roughly based on
 * the assess tool from the NIST source distribution.
 *
 * @author Peter Stamfest
 */
public class Assess {
    enum Format { ascii, binary };
    
    @Option(name = "--in", 
            usage = "input file (default is standard input)")
    private File in = null;

    @Option(name = "--rng",
            metaVar = "RNGCLASS|ALGO@[PROVIDER]",
            usage = "obtain input from a random number generator "
                    + "implementation. Either the direct implementation class "
                    + "of the generator can be given or it will be obtained "
                    + "through the provider based SecureRandom factory methods "
                    + "using the algorithm name and the (optional) provider")
    private String rngSpec = null;

    @Option(name = "--list-rng",
            usage = "list available random number generators")
    boolean listRng = false;

    @Option(name = "-r",
            aliases = { "--report-dir" },
            metaVar = "DIR",
            usage = "write per-test reports into files within DIR")
    private File outputDir = null;

    @Option(name = "-l",
            aliases = { "--seqlength" },
            usage = "bit sequence length (required in case tests are really executed)")
    int length = -1;

    @Option(name = "-n",
            aliases = { "--seqcount" },
            usage = "number of bit sequences (default is to read as many "
                    + "sequences as possible if using --in or 1 if using --rng)")
    int sequences = -1;

    @Option(name = "--inform",
            usage = "input file format (default is binary)")
    private Format inform = Format.binary;

    @Option(name = "-c",
            aliases = { "--continuous" },
            usage = "continuous testing")
    boolean continuous = false;

    @Option(name = "--list-standard", 
            usage = "list standard test suite definition and exit")
    boolean listStandard = false;

    @Option(name = "--minimum-length", 
            usage = "print recommended input length for the suite and exit")
    boolean printMinimumLength = false;
    
    @Option(name = "-t",
            aliases = { "--test" },
            metaVar = "TESTSPEC",
            usage = "add test instance (may be given multiple times)")
    private List<String> testnames = new ArrayList<>();

    @Option(name = "-T",
            aliases = { "--testfile" },
            usage = "add test instances from file (one by line)")
    private File testfile = null;

    @Option(name = "-q",
            aliases = { "--query" },
            usage = "query test constructors for all named tests")
    boolean queryConstructors = false;

    @Option(name = "--progress",
            usage = "show progress")
    boolean progress = false;

    @Option(name = "--show-tests",
            usage = "dump final list of test specs to standard output")
    boolean dumpTestSpecs = false;

    @Option(name = "--out",
            usage = "write bit sequences to the given file. This file could "
                    + "later be used to rerun the tests with exactly the same "
                    + "input data.")
    private File writeBits = null;
    
    @Option(name = "--outform",
            usage = "format to write bit sequences (default is binary)")
    private Format outform = Format.binary;

    @Option(name = "--help", 
            help = true,
            aliases = { "-h", "-?" },
            usage = "show this help message")
    boolean showHelp = false;

    // receives other command line parameters than options, but we don't need no stinkin' arguments
    //    @Argument
    //    private List<String> arguments = new ArrayList();

    public static void main(String argv[]) {
        new Assess().doMain(argv);
    }

    private int submittedCount = 0;
    private AtomicInteger doneCount = new AtomicInteger(0);

    private static interface BitSource {
        Bits getBits() throws IOException;
        boolean hasMore();
        void reset();
    }
    
    private void doMain(String[] argv) {
        ParserProperties parserProperties = ParserProperties.defaults();
        parserProperties.withShowDefaults(false);
        
        CmdLineParser parser = new CmdLineParser(this, parserProperties);
        try {
            parser.parseArgument(argv);

            if (showHelp) {
                usage(parser);
                System.exit(0);
            }

            if (listRng) {
                doListRng();
                System.exit(0);
            }

            if (listStandard) {
                doListStandard();
                System.exit(0);
            }
            
            List<NistTest> tests = null;

            if (testfile != null) {
                try (BufferedReader br = new BufferedReader(new FileReader(testfile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        testnames.add(line);
                    }
                }
            }
            
            if (testfile == null && testnames.isEmpty()) {
                testnames.addAll(Suite.getStandardTestSpecs());
            }

            if (queryConstructors) {
                doQueryConstructors();
                System.exit(0);
            }
            if (dumpTestSpecs) {
                doDumpTestSpecs();
                System.exit(0);
            }

            
            if (! testnames.isEmpty()) {
                tests = new ArrayList<>();

                for (TestSpec spec : testSpecs()) {
                    try {
                        tests.add(spec.getInstance());
                    } catch (Exception ex) {
                        Throwable throwable = ex.getCause();
                        System.err.printf("Cannot instantiate test %s: %s\n",
                                          spec.getTestClass().getName(),
                                          throwable == null ? ex.getMessage() : throwable.getMessage());
                        System.exit(2);
                    }
                }
            }

            if (length < 0) {
                fatalExit("missing (or invalid) bit sequence length", 9);
            }
            
            ParallelSuiteExecutor s = null;
            
            if (tests != null) {
                s = new ParallelSuiteExecutor(tests);

                int minLen = s.getInputSizeRecommendation();
                if (printMinimumLength) {
                    System.out.printf("%d\n", minLen);
                    System.exit(0);
                }
                if (length < minLen) {
                    System.err.printf("WARNING: chosen length (%d) is less than minimum recommended length (%d)\n",
                                      length, minLen);
                }

                if (outputDir != null) {
                    setupOutputDir();
                }
            }            
            BitSource bitSource = createBitSource();
            if (bitSource == null) {
                fatalExit("missing bit sequence source", 4);
            }
            
            runMainLoop(bitSource, s);

            System.exit(0);
        } catch (CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            usage(parser);

            System.exit(1);
        } catch (IOException ex) {
            fatalExit(String.format("I/O error: %s\n", ex.getMessage()), 3);
        } catch (InvalidTestSpec ex) {
            fatalExit(String.format("Test spec error: %s\n", ex.getMessage()), 3);
        }
    }

    private BitSource createBitSource() throws FileNotFoundException {
        Pattern p = Pattern.compile("^(?<algo>[^@]+)@(?<provider>.*)?$");
        
        BitSource bitSource = null;
        if (in == null && rngSpec != null) {
            try {
                Random rng = null;
                Matcher m = p.matcher(rngSpec);
                if (m.matches()) {
                    String algo = m.group("algo");
                    String provider = m.group("provider");
                    
                    if (provider != null && !provider.isEmpty()) {
                        rng = SecureRandom.getInstance(algo, provider);
                    } else {
                        rng = SecureRandom.getInstance(algo);
                    }
                } else {
                    Class<?> rngClass = Class.forName(rngSpec);
                    if (!Random.class.isAssignableFrom(rngClass)) {
                        fatalExit(String.format("Not a random number generator: '%s'", rngSpec), 3);
                    }
                    rng = (Random) rngClass.newInstance();
                }
                
                Random frng = rng;      // effectively final
                bitSource = new BitSource() {
                    int cnt = 0;
                    @Override
                    public Bits getBits() {
                        cnt++;
                        return new RNGBits(frng, length);
                    }
                    @Override
                    public boolean hasMore() {
                        return cnt < sequences;
                    }

                    @Override
                    public void reset() {
                        cnt = 0;
                    }
                };
            } catch (Exception ex) {
                fatalExit(String.format("Problem with class '%s': %s", rngSpec, ex.getMessage()), 3);
            }
        } else if (in != null && rngSpec == null) {
            InputStream is = new BufferedInputStream((in == null) ? System.in : new FileInputStream(in));
            bitSource = new BitSource() {
                int cnt = 0;
                private boolean eof = false;
                @Override
                public Bits getBits() throws IOException {
                    cnt++;
                    Bits b = inform == Format.ascii ? IO.readAscii(is, length) : IO.readBinary(is, length);
                    
                    if (b == null || b.getLength() != length) {
                        eof = true;
                    }
                    
                    return b;
                }

                @Override
                public boolean hasMore() {
                    return !eof && ((sequences == -1) || (cnt < sequences));
                }

                @Override
                public void reset() {
                    cnt = 0;
                }
            };
        } else {
            fatalExit("only one bit source allowed", 4);
        }
        return bitSource;
    }

    private void doListRng() {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            
            for (Provider.Service service : services) {
                if (service.getType().equals("SecureRandom")) {
                    System.out.printf("%s@%s\n", service.getAlgorithm(), provider.getName());
                }
            }
        }
    }

    private void runMainLoop(BitSource bitSource, ParallelSuiteExecutor s) throws FileNotFoundException, IOException {
        boolean done = false;
        do {
            // a constant "stop" element to mark the final element of the resultQueue
            SuiteResult stop = new EmptySuiteResult();
            SuiteResult dummy = new EmptySuiteResult();
            
            Thread resultCollector = null;
            BlockingDeque<SuiteResult> resultQueue = null;
            
            resultQueue = new LinkedBlockingDeque<>();
            
            resultCollector = new ResultCollectorThread(s, resultQueue, stop);
            resultCollector.setDaemon(false);
            resultCollector.start();
            
            OutputStream write = null;
            try {
                if (writeBits != null) {
                    /* use a BufferedOutputStream to speed up i/o. benchmarks 
                     * show a factor 10 speedup wrt the case without the buffered 
                     * stream */
                    write = new BufferedOutputStream(new FileOutputStream(writeBits));
                }
                
                bitSource.reset();
                while (bitSource.hasMore()) {
                    Bits bits = bitSource.getBits();

                    if (bits == null || bits.getLength() != length) {
                        done = true;
                        break;
                    }

                    if (write != null) {
                        if (outform == Format.binary) {
                            IO.writeBinary(write, bits);
                        } else {
                            IO.writeAscii(write, bits);
                        }
                    }
                    submittedCount++;
                    if (s != null) {
                        SuiteResult suiteResult = s.runSuite(bits);
                        resultQueue.offer(suiteResult);
                    } else {
                        // keep progress indicator correct...
                        resultQueue.offer(dummy);
                    }
                    progress();
                }
                resultQueue.offer(stop);
            } finally {
                if (write != null) {
                    write.flush();
                    write.close();
                }
            }
            // need to wait for final results...
            if (s != null) {
                s.await();
            }
            progress();
            
            try {
                resultCollector.join();
            } catch (InterruptedException ex) {
                System.err.println("WARNING: could not wait for collector thread..." + ex.getMessage());
            }
            
            if (progress && console != null) {
                console.printf("\n");
            }
            
            if (s != null) {
                s.report(new PrintWriter(System.out));

                if (outputDir != null) {
                    try (PrintWriter pw
                            = new PrintWriter(new File(outputDir,
                                                       "finalAnalysisReport.txt"))) {
                        s.report(pw);
                    }
                }
                s.reset();
            }
        } while (!done && continuous);
    }

    /**
     * Prints an error message to stderr and exits the VM.
     *
     * @param msg The message to report no stderr
     * @param exitCode The VM exit code.
     */
    private void fatalExit(String msg, int exitCode) {
        System.err.printf("%s: %s\n", getClass().getName(), msg);
        System.exit(exitCode);
    }

    private void usage(CmdLineParser parser) {
        String name = getClass().getName().replaceFirst(".*\\.", "");
        
        System.err.println(name + " [options...] arguments...");
        parser.printUsage(System.err);
        System.err.println();
        
        System.err.println("Examples:");
        try (InputStream is = getClass().getResourceAsStream("Assess-examples.txt")) {
            if (is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = br.readLine()) != null) {
                    System.err.printf(line, name);
                    System.err.println();
                }
            }
        } catch (IOException ex) {
        }
    }

    private void doQueryConstructors() throws InvalidTestSpec {
        for (TestSpec spec : testSpecs()) {
            Class<NistTest> c = spec.getTestClass();
            for (Constructor constr : c.getConstructors()) {
                StringBuilder sb = new StringBuilder();
                sb.append(c.getName())
                        .append("(");
                Parameter[] parameters = constr.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    if (i > 0) {
                        sb.append(",");
                    }
                    Class type = parameter.getType();
                    if (type.isPrimitive()) {
                        sb.append(type.getName());
                    } else {
                        sb.append(type.getName());
                    }

                    if (parameter.isNamePresent()) {
                        sb.append(" ").append(parameter.getName());
                    }
                }
                sb.append(")");
                System.err.println(sb.toString());
            }
        }
    }

    private List<TestSpec> testSpecs() throws InvalidTestSpec {
        TestFactory testFactory = TestFactory.getInstance();
        List<TestSpec> testspecs = new ArrayList<>();

        
        for (String testname : testnames) {
            testspecs.add(testFactory.parseTestSpec(testname));
        }
        return testspecs;
    }

    private void doListStandard() {
        for(String s: Suite.getStandardTestSpecs()) {
            System.out.println(s);
        }
    }

    private void setupOutputDir() {
        if (outputDir.exists() && !outputDir.isDirectory()) {
            fatalExit(String.format("output directory name '%s' exists and is not a directory\n",
                                    outputDir.getPath()), 8);
        }

        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                fatalExit(String.format("Could not create output directory '%s'\n",
                                        outputDir.getPath()), 8);
            }
        }
    }

    private void doDumpTestSpecs() throws InvalidTestSpec {
        for (TestSpec testSpec : testSpecs()) {
            System.out.println(testSpec.getDefinition());
        }
    }

    private class ResultCollectorThread extends Thread {
        private final BlockingDeque<SuiteResult> resultQueue;
        private final SuiteResult stopObject;
        private final Suite suite;
        private PrintWriter[] outputs;

        /**
         *
         * @param suite the value of suite
         * @param resultQueue the value of resultQueue
         * @param stopObject the value of stopObject
         */
        public ResultCollectorThread(Suite suite, BlockingDeque<SuiteResult> resultQueue, SuiteResult stopObject) {
            this.resultQueue = resultQueue;
            this.stopObject = stopObject;
            this.suite = suite;
            
        }
       
        @Override
        public void run() {
            SuiteResult sr = null;
            while (true) {
                try {
                    sr = resultQueue.take();
                    if (sr == stopObject) {
                        break;
                    }
                    handleRunResult(sr);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Assess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (outputs != null) {
                for (PrintWriter output : outputs) {
                    output.flush();
                    output.close();
                }
            }
        }

        void handleRunResult(SuiteResult sr) {
            List<Result[]> results = sr.getResults();
            int run = sr.getRun();

            doneCount.incrementAndGet();
            progress();
            
            if (outputDir != null && suite != null) {
                List<NistTest> tests = suite.getTests();
                if (results == null || tests == null || (results.size() != tests.size())) {
                    // argh! 
                    return;
                }

                int l = tests.size();

                if (outputs == null) {
                    outputs = new PrintWriter[l];
                    
                    for (int i = 0; i < l; i++) {
                        NistTest test = tests.get(i);
                        String desc = test.getDescription();
                        desc = desc.replaceAll("[^a-zA-Z0-9._/-]", "_");
                        try {
                            outputs[i] = new PrintWriter(new File(outputDir, desc + ".txt"));
                        } catch (FileNotFoundException ex) {
                            fatalExit(String.format("Cannot create print writer for test '%s'\n", desc), 8);
                        }
                    }
                }

                for (int i = 0 ; i < l ; i++) {
                    NistTest test = tests.get(i);
                    Result[] resultArray = results.get(i);
                    
                    outputs[i].printf("============================== run %d ==============================\n", run);
                    test.report(outputs[i], resultArray);
                    outputs[i].flush();
                }
            }
        }
    }
    
    private Console console = System.console();
    private synchronized void progress() {
        if (!progress || console == null) {
            return;
        }
        console.printf("\rsubmitted=%d, done=%d", submittedCount, doneCount.get());
        console.flush();
    }

    private static class EmptySuiteResult implements SuiteResult {
        @Override
        public List<Result[]> getResults() {
            return null;
        }

        @Override
        public int getRun() {
            return -1;
        }
    }
}
