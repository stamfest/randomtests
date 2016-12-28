/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests;

/**
 * An exception transporting information about problems with the textual
 * representation as defined in the {@link TestFactory} class.
 *
 * @author Peter Stamfest
 */
public class InvalidTestSpec extends Exception {
    public InvalidTestSpec(String message) {
        super(message);
    }
}
