/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import junit.framework.Assert;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.utils.IO;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class IOTest {

    byte[] bytes = new byte[1024 * 1024];
    byte[] shortBytes = new byte[] { (byte) 0x6a, (byte) 0xc5 };
    @Before
    public void setup() {
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (((i >> 10) ^ ((i & 0x300) >> 8) ^ i) & 0xff);
        }
    }

    @Test
    public void multiStreams() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);

        for (int i = 0; i < 1024; i++) {
            Bits bits = IO.readBinary(is, 1024 * 8);
            Assert.assertEquals(1024 * 8, bits.getLength());

            for (int j = 0; j < 1024; j++) {
                byte wanted = (byte) ((i ^ (j >> 8) ^ j) & 0xff);
//                System.err.printf("i=%d j=%d wanted=%d\n", i, j, wanted);
                Assert.assertEquals((wanted >> 7) & 1, bits.bit(j * 8 + 0));
                Assert.assertEquals((wanted >> 6) & 1, bits.bit(j * 8 + 1));
                Assert.assertEquals((wanted >> 5) & 1, bits.bit(j * 8 + 2));
                Assert.assertEquals((wanted >> 4) & 1, bits.bit(j * 8 + 3));
                Assert.assertEquals((wanted >> 3) & 1, bits.bit(j * 8 + 4));
                Assert.assertEquals((wanted >> 2) & 1, bits.bit(j * 8 + 5));
                Assert.assertEquals((wanted >> 1) & 1, bits.bit(j * 8 + 6));
                Assert.assertEquals((wanted >> 0) & 1, bits.bit(j * 8 + 7));
            }
        }
    }
    
    /** Tests if long binary bit sequences can be read. Check for bugfix! */
    @Test
    public void longSequence() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Bits bits = IO.readBinary(is, 500000);
        Assert.assertEquals(500000, bits.getLength());
    }
    
    @Test
    public void shortSequence1() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(shortBytes);
        Bits bits = IO.readBinary(is, 1);
        Assert.assertEquals(1, bits.getLength());
        
        Assert.assertEquals(0, bits.bit(0));
    }

    @Test
    public void shortSequence5() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(shortBytes);
        Bits bits = IO.readBinary(is, 5);
        Assert.assertEquals(5, bits.getLength());
        
        Assert.assertEquals(0, bits.bit(0));
        Assert.assertEquals(1, bits.bit(1));
        Assert.assertEquals(1, bits.bit(2));
        Assert.assertEquals(0, bits.bit(3));
        Assert.assertEquals(1, bits.bit(4));
    }

    @Test
    public void shortSequence8() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(shortBytes);
        Bits bits = IO.readBinary(is, 8);
        Assert.assertEquals(8, bits.getLength());
        
        Assert.assertEquals(0, bits.bit(0));
        Assert.assertEquals(1, bits.bit(1));
        Assert.assertEquals(1, bits.bit(2));
        Assert.assertEquals(0, bits.bit(3));
        Assert.assertEquals(1, bits.bit(4));
        Assert.assertEquals(0, bits.bit(5));
        Assert.assertEquals(1, bits.bit(6));
        Assert.assertEquals(0, bits.bit(7));
    }

    @Test
    public void shortSequence9() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(shortBytes);
        Bits bits = IO.readBinary(is, 9);
        Assert.assertEquals(9, bits.getLength());
        
        Assert.assertEquals(0, bits.bit(0));
        Assert.assertEquals(1, bits.bit(1));
        Assert.assertEquals(1, bits.bit(2));
        Assert.assertEquals(0, bits.bit(3));
        Assert.assertEquals(1, bits.bit(4));
        Assert.assertEquals(0, bits.bit(5));
        Assert.assertEquals(1, bits.bit(6));
        Assert.assertEquals(0, bits.bit(7));
        Assert.assertEquals(1, bits.bit(8));
    }
    
    @Test
    public void twoShortSequences() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(shortBytes);
        Bits bits = IO.readBinary(is, 4);
        Assert.assertEquals(4, bits.getLength());
        Assert.assertEquals(0, bits.bit(0));
        Assert.assertEquals(1, bits.bit(1));
        Assert.assertEquals(1, bits.bit(2));
        Assert.assertEquals(0, bits.bit(3));

        bits = IO.readBinary(is, 4);
        Assert.assertEquals(4, bits.getLength());
        Assert.assertEquals(1, bits.bit(0));
        Assert.assertEquals(1, bits.bit(1));
        Assert.assertEquals(0, bits.bit(2));
        Assert.assertEquals(0, bits.bit(3));
    }

    /**
     *
     * @param in the value of in
     * @param len the value of len
     * @throws IOException
     */
    public void readWriteBinary(byte[] in, int len) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(in);
        Bits bits = IO.readBinary(is, len);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IO.writeBinary(os, bits);

        byte[] obytes = os.toByteArray();

        Assert.assertEquals(in.length, obytes.length);
        for (int i = 0; i < Math.min(obytes.length - 1, 0); i++) {
            Assert.assertEquals("byte " + i,  in[i], obytes[i]);
        }
        byte mask = (byte) (0xff << (8 - (len % 8)));
        //System.err.printf("%03x %03x %03x\n", mask, in[obytes.length - 1] & mask, obytes[obytes.length-1] & mask);
        Assert.assertEquals("last byte",  in[obytes.length - 1] & mask, obytes[obytes.length-1] & mask);

    }

    @Test
    public void readWriteBinaryShort() throws IOException {
        readWriteBinary(shortBytes, shortBytes.length * 8);
    }

    @Test
    public void readWriteBinaryShort2() throws IOException {
        readWriteBinary(shortBytes, shortBytes.length * 8 - 1);
    }

    @Test
    public void readWriteBinaryShort3() throws IOException {
        readWriteBinary(shortBytes, shortBytes.length * 8 - 2);
    }

    @Test
    public void readWriteBinaryShort4() throws IOException {
        readWriteBinary(shortBytes, shortBytes.length * 8 - 3);
    }

    @Test
    public void readWriteBinaryLong() throws IOException {
        readWriteBinary(bytes, bytes.length * 8);
    }
    
    @Test
    public void readWriteAscii1() throws IOException {
        readWriteAscii("01011101011011110101010100000100010101110100011", 30);
    }

    public void readWriteAscii(String str, int len) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes());

        Bits bits = IO.readAscii(is, len);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IO.writeAscii(os, bits);

        byte[] obytes = os.toByteArray();

        Assert.assertEquals(len, obytes.length);
        for (int i = 0; i < len; i++) {
            Assert.assertEquals("byte " + i, str.charAt(i), obytes[i]);
        }
    }
 
    @Test
    public void eofBinary() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(shortBytes);
        Bits bits = IO.readBinary(is, shortBytes.length * 8);

        Assert.assertNotNull(bits);
        Assert.assertEquals(16, bits.getLength());
        
        bits = IO.readBinary(is, shortBytes.length * 8);
        Assert.assertNotNull(bits);
        Assert.assertEquals(0, bits.getLength());
    }
    
    
}
