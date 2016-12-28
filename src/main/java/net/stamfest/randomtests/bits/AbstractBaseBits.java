/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.bits;

import java.util.Iterator;

/**
 * An abstract Bits base class providing basic services that most
 * implementations might like, but which cannot be expressed using default
 * interface implementations in Bits. Also, all the methods having default
 * implementations in the {@link Bits} interface receive concrete
 * implementations. This is to avoid some strange exceptions at least in Java 8
 * when using such methods through at least the nashorn JavaScript engine
 * bundles with Oracle java 8.
 *
 * @author Peter Stamfest
 */
@SuppressWarnings("EqualsAndHashcode")
public abstract class AbstractBaseBits implements Bits {
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (! (obj instanceof Bits)) return false;
        Bits bits = (Bits) obj;
        if (bits.getLength() != this.getLength()) return false;
        int l = getLength();
        for (int i = 0; i < l; i++) {
            if (bit(i) != bits.bit(i)) return false;
        }
        return true;
    }
    
    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int n = 0;
            private int len = getLength();

            @Override
            public boolean hasNext() {
                return n < len;
            }

            @Override
            public Integer next() {
                return bit(n++);
            }
        };
    }
    
    /*
      NOTE: There are (or were) default implementations available for the
      following method(s), but this caused strange exceptions when calling them
      through the nashorn javascript engine. Introducing this abstract class
      and providing explicit implementations here made them go away.
    
    java.lang.reflect.InvocationTargetException
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:497)
        at net.stamfest.randomtests.tools.Tool.main(Tool.java:40)
    Caused by: java.lang.IncompatibleClassChangeError: Interface method reference: net.stamfest.randomtests.bits.Bits.xor(Lnet/stamfest/randomtests/bits/Bits;)Lnet/stamfest/randomtests/bits/Bits;, is in an indirect superinterface of net.stamfest.randomte
        at net.stamfest.randomtests.bits.RNGBits$$NashornJavaAdapter.xor(Unknown Source)
     */
    @Override
    public Bits xor(Bits b) {
        return new XORBits(this, b);
    }
    
    @Override
    public Bits concat(Bits... bits) {
        Bits cpy[] = new Bits[bits.length + 1];
        cpy[0] = this;
        System.arraycopy(bits, 0, cpy, 1, bits.length);
        return new ConcatBits(cpy);
    }

    @Override
    public Bits reverse() {
        return new ReversedBits(this);
    }

    @Override
    public Bits subSeq(int offset, int length, int step) {
        return new SubSequence(this, offset, length, step);
    }
}
