/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.utils.IO;

/**
 *
 * @author Peter Stamfest
 */
public class BIO {
    public enum Format { ascii, binary };

    Format format = Format.binary;

    private InputStream is = null;
    private OutputStream os = null;
    
    /**
     *
     * @param is the value of is
     * @param format the value of format
     */
    public BIO(InputStream is, BIO.Format format) {
        this.is = is;
        this.format = format;
    }
    
    public BIO(OutputStream os, BIO.Format format) {
        this.os = os;
        this.format = format;
    }
    
    public BIO(File f) throws FileNotFoundException {
        this.is = new FileInputStream(f);
    }

    
    public  Bits read(int length) throws IOException {
        if (is == null) {
            throw new IOException("not an input BIO");
        }
        Bits r = format == Format.binary ? IO.readBinary(is, length) : IO.readAscii(is, length);
        return r.getLength() == 0 ? null : r;
    }

    public void write(Bits b) throws IOException {
        if (os == null) {
            throw new IOException("not an output BIO");
        }
        if (format == Format.binary) {
            IO.writeBinary(os, b);
        } else {
            IO.writeAscii(os, b);
        }
        os.flush();
    }

    @Override
    protected void finalize() throws Throwable {
        flush();
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
    }

    
    public void flush() throws IOException {
        if (os != null) os.flush();
    }

}
