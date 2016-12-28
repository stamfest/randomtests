/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import net.stamfest.randomtests.bits.ArrayBits;
import net.stamfest.randomtests.bits.Bits;

/**
 * Support for reading bit sequences from an input stream. Either binary data (8
 * bits per byte) or ASCII based data can be read.
 *
 * @author Peter Stamfest
 */
public class IO {

    /**
     * Reads a bit sequence from a binary input stream. Every byte defines 8
     * bits, where bit 7 of the very first byte corresponds to bit 0 of the bit
     * sequence.
     *
     * @param is          The InputStream to read the bit stream from.
     * @param maxBitCount The number of bits to read. If this is not a multiple
     *                    of 8, the remaining bits of the very last byte read
     *                    will be lost from the input stream.
     *
     * @return The resulting bit sequence.
     * @throws IOException Passed on from InputStream operations.
     */
    public static Bits readBinary(InputStream is, int maxBitCount) throws IOException {
        int n = 0;
        int len = 8192;
        int maxByteCount = (maxBitCount + 7) / 8;

        byte buf[] = new byte[len];
        int cnt;

        while ((cnt = is.read(buf, n, Math.min(len, maxByteCount) - n)) > 0) {
            n += cnt;
            if (n >= maxByteCount) {
                break;
            }
            if (n >= buf.length) {
                // enlarge buffer
                len *= 2;

                buf = Arrays.copyOf(buf, len);
            }
        }

        return new ArrayBits(buf, (n == maxByteCount) ? maxBitCount : (n * 8));
    }

    /**
     * Write a bit sequence as a sequence of bytes, each byte representing 8 bits.
     *
     * @param os The OutputStream to write to.
     * @param bits The bit sequence to write
     * @throws IOException Passed on from InputStream operations.
     */
    public static void writeBinary(OutputStream os, Bits bits) throws IOException {
        byte b = 0;
        int n = 0;
        for (int bit : bits) {
            b = (byte) ((b << 1) | (byte) bit);
            n++;
            if (n % 8 == 0) {
                os.write(b);
                b = 0;
            }
        }
        if (n % 8 != 0) {
            b = (byte) (b << (8 - n % 8));
            os.write(b);
        }
    }

    /**
     * Read a bit sequence from an InputStream. Only the ASCII characters '0'
     * and '1' are recognised and define the sequence bit-by-bit.
     *
     * @param is          The InputStream to read ASCII '0' and '1' characters
     *                    from.
     * @param maxBitCount The maximum number of bits to read. If the input
     *                    stream does not contain enough '0' and '1' characters,
     *                    the bit sequence will be shorter than this number.
     * @return The resulting bit sequence.
     * @throws IOException Passed on from InputStream operations.
     */
    public static Bits readAscii(InputStream is, int maxBitCount) throws IOException {
        int n = 0;
        int len = 8192;

        byte buf[] = new byte[len];
        int cnt;
        int b;

        while ((b = is.read()) > 0) {
            //  while ((cnt = is.read(buf, n, Math.min(len, maxByteCount) - n)) > 0) {
            if (b != '0' && b != '1') {
                continue;
            }

            int byteIndex = n / 8;
            if (byteIndex >= buf.length) {
                // enlarge buffer
                len *= 2;

                buf = Arrays.copyOf(buf, len);
            }
            if (b == '1') {
                buf[byteIndex] |= 1 << (7 - n % 8);
            }
            n++;

            if (n >= maxBitCount) {
                break;
            }
        }

        return new ArrayBits(buf, n);
    }

    /**
     * Write a bit sequence as an ASCII sequence of '0' and '1' characters.
     *
     * @param os The OutputStream to write to.
     * @param b  The bit sequence to write.
     * @throws IOException Passed on from stream operations.
     */
    public static void writeAscii(OutputStream os, Bits b) throws IOException {
        for (int bit : b) {
            os.write((bit != 0) ? '1' : '0');
        }
    }
}
