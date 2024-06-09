package tests.utils.operations;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;
import utils.operations.BitOperations;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class TestBitOperations {

    @Test
    public void testIsPowerOfTwo() {
        assertTrue(BitOperations.isPowerOfTwo(new BigInteger("4")));
        assertFalse(BitOperations.isPowerOfTwo(new BigInteger("5")));
    }

    @Test
    public void testLogarithmBaseTwoOfBigInteger() {
        assertEquals(2, BitOperations.logarithmBaseTwoOfBigInteger(new BigInteger("4")));
        assertEquals(3, BitOperations.logarithmBaseTwoOfBigInteger(new BigInteger("8")));
    }

    @Test
    public void testBitReversal() {
        BigInteger value = new BigInteger("12"); // 1100 in binary
        int bitLength = 8;
        BigInteger result = BitOperations.bitReversal(value, bitLength);
        assertEquals(new BigInteger("48"), result); // 1100 reversed to 00110000 which is 48
    }

    @Test
    public void testVectorBitReversal() {
        BigInteger[] values = {
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("4")
        };

        BigInteger[] result = BitOperations.vectorBitReversal(values);

        BigInteger[] expected = {
                new BigInteger("1"),
                new BigInteger("3"),
                new BigInteger("2"),
                new BigInteger("4")
        };

        assertArrayEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVectorBitReversalInvalidLength() {
        BigInteger[] values = {
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3")
        };

        BitOperations.vectorBitReversal(values);
    }

    @Test
    public void testVectorBitReversalComplex() {
        Complex[] values = {
                new Complex(1, 0),
                new Complex(2, 0),
                new Complex(3, 0),
                new Complex(4, 0)
        };

        Complex[] result = BitOperations.vectorBitReversalComplex(values);

        Complex[] expected = {
                new Complex(1, 0),
                new Complex(3, 0),
                new Complex(2, 0),
                new Complex(4, 0)
        };

        assertArrayEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVectorBitReversalComplexInvalidLength() {
        Complex[] values = {
                new Complex(1, 0),
                new Complex(2, 0),
                new Complex(3, 0)
        };

        BitOperations.vectorBitReversalComplex(values);
    }

    @Test
    public void testBaseDecompose() {
        BigInteger value = new BigInteger("255");
        int base = 16;

        BigInteger[] result = BitOperations.baseDecompose(value, base);

        BigInteger[] expected = {
                new BigInteger("15"),
                new BigInteger("15"),
                new BigInteger("0"),
        };

        assertArrayEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBaseDecomposeInvalidBase() {
        BitOperations.baseDecompose(new BigInteger("255"), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBaseDecomposeNegativeValue() {
        BitOperations.baseDecompose(new BigInteger("-255"), 16);
    }
}
