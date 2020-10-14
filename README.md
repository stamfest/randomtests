Random tests
============

This is a project to re-implement the random number test suite from 
the NIST standard publication to be found at

  http://csrc.nist.gov/groups/ST/toolkit/random_number.html

More specifically, the tests are based on the document (linked from this 
page) at

  http://csrc.nist.gov/groups/ST/toolkit/rng/documents/SP800-22rev1a.pdf

In addition to the tests from this suite, the software contains some infrastructure 
to deal with bit sequences and to transform them.

To build this project from sources you need maven (yes, I know: It IS horrible)

Build a jar file to be executed using `java -jar ...` by running

    mvn -DskipTests clean compile package

This will build a jar file containing all the dependencies required to run the 
software and which can be used directly using java by calling it like

    java -jar target/randomtests-ARTIFACT_VERSION-jar-with-dependencies.jar TOOL OPTIONS

where ARTIFACT_VERSION stands for the version as specified in the pom.xml file.
TOOL is the tool to run and OPTIONS are the command line options required
for the TOOL. All tools should accept the `--help` option.

You may also use one of the scripts from this directory to run the suite. The 
linux/unix shell script `rndtest` should work to run the tools even after changes 
to the source code. Alternatively, use 

    mvn exec:java -Dexec.args="TOOL OPTIONS"

Tools
=====

Currently there are two tools: Assess and Transform. Both support the `--help`
option to give a short usage message. So using

    rndtest Assess --help 

gives a list of supported options.

Assess
------

The Assess tool is the interface to the core reimplementation of the original NIST 
tests and is inspired by the original assess tool from the NIST distribution.

Assess allows to run any sequence of supported tests by passing a "testfile" naming the
tests to be run. There is a standard list of tests which can be shown by passing
`--show-tests` with no other options present.

Note: if multiple sequences are tested using this tool, they are processed in parallel on 
multi-processor systems.

### Examples

Run the standard NIST test suite on the standard JDK random number generator using 100 
bit sequences with 1 millon bits each, showing basic progress information.

    Assess --rng NativePRNGNonBlocking@SUN --seqlength 1000000 --seqcount 100 --progress

This might take considerable time, depending on the CPUs available. It will produce 
a report similar to the report from the orignal NIST tool. The results for identical
input should be identical to the ones reported by the NIST tool.

The extended example

    Assess --rng NativePRNGNonBlocking@SUN --seqlength 1000000 --seqcount 100 --out tested.bits --report-dir reports --progress

also produces an output file (`tested.bits`) holding all the tested 
bits. This might be interesting to be able to (re)test the exact same sequence with 
other settings. It also creates an output directory (`reports`), where it puts more detailed information 
about all the executed tests.


Data Format
===========

The tools operate on bit sequences. There currently are two external storage formats 
understood natively by the tools: binary and ascii.

Binary
------

The binary format is just that: A sequence of bytes where every bit refers to one 
bit of the sequence. The overall sequence of bits is defined as the concatenation 
of all bits from all bytes making up the sequence with the highest order bit of 
the first byte referring to the first bit of the sequences (index 0). The lowest 
order bit of the first byte is the eighth bit or the sequence (index 7). 

This means that the byte sequence 0x96 0x14 corresponds to the bit sequence

1001011000010100

Where the left-most bit corresponds to index 0 and the right-most one is at index 15.

ASCII
-----

The ASCII format is just a string representation of ASCII '0' and '1' characters 
(character codes 48 (hex 30) and 49 (hex 31)) corresponding to the individual bits 
of the sequence. If such a format is read, all characters that are not either '0'
or '1' get silently ignored. This means that the string "10 011x010b110N! 0" 
corresponds to the bit sequence

100110101100

Test definition file
====================

The tests the `Assess` tool applies to its input data can be configured through 
a  "testfile". This file contains one test definiton per line. Every line is 
the  class name of a NistTest sub class to instantiate (including the 
package name in standard java notation. The package name may be omitted if the 
class is within the package net.stamfest.randomtests.nist.)

Optionally, the class name may be followed by a list of arguments within
parentheses. The argument list will be passed to the matchings constructor.

The actually used list of tests can be requested from the Assess to by using the 
`--show-tests` option.

The standard list of tests is 

```
Frequency
BlockFrequency(128)
CumulativeSums
Runs
LongestRunOfOnes
Rank
DiscreteFourierTransform
NonOverlappingTemplateMatchings(9)
OverlappingTemplateMatching(9)
Universal
ApproximateEntropy(10)
RandomExcursions
RandomExcursionsVariant
Serial(16)
LinearComplexity(500)
```

Source code documentation
=========================

During the maven build process javadoc documentation gets created in the `target/apidocs` 
directory.

Licensing
---------

For licensing information see the file license.txt

Author
------

This software was written by Peter Stamfest (peter -AT- stamfest.at) and is
based in parts on software by NIST.
