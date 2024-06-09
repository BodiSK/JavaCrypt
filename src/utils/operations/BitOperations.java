package utils.operations;

import org.apache.commons.math3.complex.Complex;

import java.math.BigInteger;

import static utils.Constants.INVALID_VECTOR_LENGTH_EXCEPTION;

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
     * Calculates logarithm base 2 of BigInteger value
     * works correctly for exact powers of 2, because
     * bit representation of a power of 2 has exactly 1 non zero element at the position of the power
     * subtracting one of it is since the starting power is 0
     */
    public static int logarithmBaseTwoOfBigInteger(BigInteger value) {
        return value.bitLength()-1;
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
    public static BigInteger bitReversal(BigInteger value, int bitLength) {
        BigInteger reversedValue = BigInteger.ZERO;
        for (int i = 0; i < bitLength; i++) {
            BigInteger bit = value.testBit(i) ? BigInteger.ONE : BigInteger.ZERO;
            reversedValue = reversedValue.shiftLeft(1).or(bit);
        }
        return reversedValue;
    }


    /**
     * Performs reversal of the values in a list using bitReversal
     * the new order is based on the position that the bitReversal of the current index gives
     * Example: having [1,2,3,4] as an input list with length the width of reversal is 2
     *                  0 in binary is 00, reversed is 00 which is 0 so the value on index 0 stays the same
     *                  1 in binary is 01, reversed is 10 which is 2 so the value on index 1 is takes the position of the value of index 2
     *                  2 in binary is 10, reversed is 01 which is 1 so the value on index 2 is takes the position of the value of index 1
     *                  3 in binary is 11, reversed is 11 which is 3 so the value on index 3 stays the same
     *        the result array is [1, 3, 2, 4]
     * @param values  the list of values that have to be reversed
     * @throws IllegalArgumentException if the length of the list is not an exact power of 2
     */
    public static BigInteger[] vectorBitReversal(BigInteger[] values) {
        int length = values.length;

        BigInteger [] result = new BigInteger[length];

        if(!isPowerOfTwo(BigInteger.valueOf(length))) {
            throw new IllegalArgumentException(String.format(INVALID_VECTOR_LENGTH_EXCEPTION
                    , length));
        }

        for (int i = 0; i < length; i++) {
            int newIndex = bitReversal(BigInteger.valueOf(i), logarithmBaseTwoOfBigInteger(BigInteger.valueOf(length))).intValue();
            result[i] = values[newIndex];
        }

        return result;
    }

    public static Complex[] vectorBitReversalComplex(Complex[] values) {
        int length = values.length;

        Complex [] result = new Complex[length];

        if(!isPowerOfTwo(BigInteger.valueOf(length))) {
            throw new IllegalArgumentException(String.format(INVALID_VECTOR_LENGTH_EXCEPTION
                    , length));
        }

        for (int i = 0; i < length; i++) {
            int newIndex = bitReversal(BigInteger.valueOf(i), logarithmBaseTwoOfBigInteger(BigInteger.valueOf(length))).intValue();
            result[i] = values[newIndex];
        }

        return result;
    }


    public static  BigInteger[] baseDecompose(BigInteger value, int base) {
        if (base <= 1) {
            throw new IllegalArgumentException("Base must be greater than 1");
        }

        if (value.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("Value must be non-negative");
        }

        // Calculate the number of digits required
        int numDigits = 1 + (value.bitLength() / (int) Math.ceil(Math.log(base) / Math.log(2)));

        BigInteger[] result = new BigInteger[numDigits];
        BigInteger divisor = BigInteger.valueOf(base);

        // Perform base decomposition
        for (int i = 0; i < numDigits; i++) {
            BigInteger[] quotientAndRemainder = value.divideAndRemainder(divisor);
            result[i] = quotientAndRemainder[1];
            value = quotientAndRemainder[0];
        }

        return result;
    }

}
