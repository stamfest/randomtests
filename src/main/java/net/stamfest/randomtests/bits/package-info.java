/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */

/**
 * This package contains classes to deal with bit sequences. A bit sequence is
 * just an ordered sequence of bits denoted by the integer values 0 and 1. In
 * the realm of random number generator testing, bit sequences under test are
 * often considered to be immutable, but this is not actually required or
 * guaranteed by the implementation.
 *
 * In addition to bit sequences that are just backed by some form of internal
 * storage, some additional classes are provided to represent transformed
 * sequences, that is bit sequences that are based on another sequences but
 * transformed in some way.
 */
package net.stamfest.randomtests.bits;
