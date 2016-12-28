/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.transform.BIO;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

/**
 *
 * @author Peter Stamfest
 */
public class Transform {

    @Option(name = "--inform", usage = "input format (default is binary)")
    BIO.Format inform = BIO.Format.binary;

    @Option(name = "--outform", usage = "output format (default is binary)")
    BIO.Format outform = BIO.Format.binary;

    @Option(name = "--script", usage = "transformation script file")
    File scriptFile = null;

    @Option(name = "--in", usage = "input file (stdin if not given)")
    File inFile = null;

    @Option(name = "--out", usage = "output file (stdout if not given)")
    File outFile = null;

    @Option(name = "--script-engine", usage = "what script engine to use (better not play around with this)")
    String scriptEngine = "nashorn";

    @Option(name = "--arg",
            usage = "script argument. May be specified an arbitrary "
            + "number of times. If --script is given, 'normal' "
            + "command line arguments become script arguments")
    List<String> scriptArgs = new ArrayList<>();

    @Option(name = "--help",
            help = true,
            aliases = { "-h", "-?" },
            usage = "show this help message")
    boolean showHelp = false;

    @Option(name = "--stats",
            usage = "show some final stats")
    boolean stats = false;

    // receives other command line parameters than options, but we don't need no stinkin' arguments
    @Argument
    private List<String> arguments = new ArrayList<>();

    public static void main(String argv[]) {
        new Transform().doMain(argv);
    }

    private void usage(CmdLineParser parser) {
        String name = getClass().getName().replaceFirst(".*\\.", "");

        System.err.println(name + " [options...] arguments...");
        parser.printUsage(System.err);
        System.err.println();
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

            ScriptEngineManager sem = new ScriptEngineManager();

            ScriptEngine engine = sem.getEngineByName(scriptEngine);

            ScriptContext context = engine.getContext();
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            
            BIO input
                    = (inFile == null)
                            ? new BIO(System.in, inform)
                            : new BIO(new FileInputStream(inFile), inform);

            bindings.put("RNGBits",
                         engine.eval("Java.extend(Java.type('net.stamfest.randomtests.bits.RNGBits'), {});"));
            bindings.put("StringBits",
                         engine.eval("Java.extend(Java.type('net.stamfest.randomtests.bits.StringBits'), {});"));
            bindings.put("BigIntegerBits",
                         engine.eval("Java.extend(Java.type('net.stamfest.randomtests.bits.BigIntegerBits'), {});"));
            bindings.put("input", input);
            // bindings.put("op", new TransformOps());

            BIO output
                    = (outFile == null)
                            ? new BIO(System.out, outform)
                            : new BIO(new FileOutputStream(outFile), outform);

            if (scriptFile == null) {
                if (arguments.isEmpty()) {
                    System.err.println("Missing script or script file.\n"
                            + "Either specify a script using thessh  --script option or on the commandline");
                    System.exit(2);
                }
                StringBuilder sb = new StringBuilder();
                for (String argument : arguments) {
                    sb.append(argument).append(" ");
                }

                bindings.put("argv", scriptArgs.toArray(argv));

                engine.eval(sb.toString(), bindings);

            } else {
                try (FileReader reader = new FileReader(scriptFile)) {
                    bindings.put("argv", arguments.toArray(argv));
                    engine.eval(reader, bindings);
                }
            }

            Invocable inv = (Invocable) engine;

            int steps = 0;
            long totalBitCount = 0;

            do {
                steps++;
                Object ret = inv.invokeFunction("step");
                if (ret == null) {
                    break;
                }
                if (!(ret instanceof Bits)) {
                    System.err.println("step function must return a Bits object");
                    output.flush();
                    System.exit(1);
                }

                Bits obits = (Bits) ret;
                output.write(obits);
                totalBitCount += obits.getLength();
            } while (true);

            output.flush();

            if (stats) {
                System.err.println("steps   =" + steps);
                System.err.println("bitcount=" + totalBitCount);
            }
            System.exit(0);
        } catch (CmdLineException ex) {
            Logger.getLogger(Transform.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Transform.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
