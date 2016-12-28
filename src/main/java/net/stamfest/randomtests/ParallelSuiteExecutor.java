/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests;

import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.nist.NistTest;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.utils.Histogram;

/**
 * A test suite using a thread pool to execute multiple tests at once. The use
 * of such a thread pool allows to use all available processing cores of most
 * modern computers.
 *
 * @author Peter Stamfest
 */
public class ParallelSuiteExecutor extends Suite {

    /**
     * Creates the NIST "standard test suite" (just like the
     * {@link Suite#getStandardTestSuite} factory method), but executing
     * multiple tests at once.
     *
     * @param settings The settings object.
     * @return The standard NIST test suite using a {@link ParallelSuiteExecutor}
     *         for execution.
     */
    public static ParallelSuiteExecutor getStandardParallelTestSuite(Properties settings) {
        return new ParallelSuiteExecutor(getStandardTestList());
    }

    private int threads = Runtime.getRuntime().availableProcessors();
    private ThreadPoolExecutor executor = null;

    /**
     * Create a test suite that is capable of running tests for multiple bit
     * sequences in parallel.
     *
     * @param tests The list of tests the suite implements.
     */
    public ParallelSuiteExecutor(List<NistTest> tests) {
        super(tests);
    }

    /**
     * Start the internal executor used to run tests. Calling this usually is
     * not necessary, because the executor is started implicitly.
     *
     */
    synchronized public void start() {
        if (executor != null) {
            return;
        }
        final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10);
        RejectedExecutionHandler rejectedHandler = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                while (true) {
                    try {
                        queue.put(r);
                        return;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ParallelSuiteExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        executor = new ThreadPoolExecutor(threads, threads,
                                          1, TimeUnit.DAYS,
                                          queue,
                                          rejectedHandler
        );
        executor.allowCoreThreadTimeOut(true);
    }

    /**
     * A class representing the result of the call to the
     * {@link ParallelSuiteExecutor#runSuite} method. As this might call tests
     * asynchronously, this class is as of now based on
     * {@link java.util.concurrent.Future} internally.
     */
    public class FutureSuiteResult implements SuiteResult {
        private Future<SuiteResult> future;
        
        public FutureSuiteResult(Future<SuiteResult> future) {
            this.future = future;
        }
        
        @Override
        public List<Result[]> getResults() {
            try {
                return future.get().getResults();
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        public int getRun() {
            try {
                return future.get().getRun();
            } catch (Exception ex) {
                return -1;
            }
        }
        
    }
    
    @Override
    synchronized public SuiteResult runSuite(Bits bits) {
        if (executor == null) {
            start();
        }
        
        Future<SuiteResult> f = executor.submit(new Callable<SuiteResult>() {
            @Override
            public SuiteResult call() throws Exception {
                SuiteResult sr = ParallelSuiteExecutor.super.runSuite(bits);
                return sr;
            }
        });

        int proccount = Runtime.getRuntime().availableProcessors();
        if (proccount != threads) {
            threads = proccount;
            executor.setCorePoolSize(proccount);
            executor.setMaximumPoolSize(proccount);
        }
        return new FutureSuiteResult(f);
    }

    /**
     * Wait for all currently scheduled test runs to be finished.
     */
    public void await() {
        ThreadPoolExecutor e = null;
        synchronized (this) {
            if (executor == null) {
                return;
            }
            e = executor;
            executor = null;
        }

        e.shutdown();
        try {
            e.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException ex1) {
            Logger.getLogger(ParallelSuiteExecutor.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }

    @Override
    public void report(PrintWriter out) {
        await();
        super.report(out); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Histogram> getHistogramsForTest(NistTest test) {
        await();
        return super.getHistogramsForTest(test);
    }

    @Override
    public void reset() {
        await();
        super.reset();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
    }
}
