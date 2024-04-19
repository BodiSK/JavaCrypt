package utils.operations;

import java.math.BigInteger;

/**
 * A class encapsulating functionality for performing operations with bits
 */
public class BitOperations {
    /**
     * Checks if a certain BigInteger is a power of 2
     * by checking if there is only one non zero element in the bit representation of the input value
     */
    public static boolean isPowerOfTwo(BigInteger value){
        return value.bitCount() == 1;
    }

    /**
     * Performs bit reversal operation necessary for Number Theoretical Transform
     * given an input value returns a value that is exactly bitLength long in binary and has the bits in reversed order of the original value
     * Example: for inputs value = 12 and bitLength = 8 result is 48, because
     * 12 in binary is 1100, padded with zeros to reach length 8 it is 00001100, reversed 001100000 which is 48
     *
     * @param value the value to be reversed
     * @param bitLength the length of bits the result should have
     */
    //todo provide more documentation here
    public static BigInteger bitReversal(BigInteger value, int bitLength) {
        BigInteger reversedValue = BigInteger.ZERO;
        for (int i = 0; i < bitLength; i++) {
            BigInteger bit = value.testBit(i) ? BigInteger.ONE : BigInteger.ZERO;
            reversedValue = reversedValue.shiftLeft(1).or(bit);
        }
        return reversedValue;
    }

    /**
     * Calculates logarithm base 2 of BigInteger value
     * works correctly for exact powers of 2, because
     * bit representation of a power of 2 has exactly 1 non zero element at the position of the power
     * subtracting one of it is since the starting power is 0
     */
    public static int logarithmBaseTwoOfBigInteger(BigInteger value) {
        return value.bitLength()-1;
    }
}
