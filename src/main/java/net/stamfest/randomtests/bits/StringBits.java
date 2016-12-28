/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import net.stamfest.randomtests.utils.IO;

/**
 * A bit sequence initialized from a String. Every '0' or '1' character in the
 * string defines one bit of the sequence, with the bit with index 0 originating
 * from the first such character of the String. All other characters are
 * ignored.
 *
 * @author Peter Stamfest
 */
public class StringBits extends ArrayBits {

    /**
     * Constructs a bit sequence based on a String.
     *
     * @param bits The String to define the bit sequence from in ASCII format,
     *             that is only '0' and '1' characters are recognised. All other
     *             characters will be silently ignored.
     */
    public StringBits(String bits) {
        super();

        byte array[] = new byte[bits.length() / 8 + 1];
        int i = 0, j = 0;
        for (j = 0; j < bits.length(); j++) {
            char c = bits.charAt(j);
            if (c != '0' && c != '1') {
                continue;
            }
            if (c == '1') {
                array[i / 8] |= 1 << (7 - (i % 8));
            }
            i++;
        }
        setArray(array, i);
    }
    
    public static String toString(Bits bits) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        try {
            IO.writeAscii(bos, bits);
            bos.flush();
            bos.close();
                    
            return baos.toString();
        } catch (IOException ex) {
        }
        return "";
    }
}
