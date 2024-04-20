package utils.operations;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Optional;


/**
 * A class encapsulating functionality for basic number theory functions
 */
public class AlgebraicOperations {

    // Certainty value to check if a number is prime
    // build in function computes with probability 1 -1/2^CERTAINTY thus providing high accuracy of the result
    private static final int CERTAINTY = 200;
    private static final MathContext CALCULATIONS_ACCURACY = new MathContext(100);
    private static final RoundingMode ROUNDING_MODE = RoundingMode.FLOOR;

    /**
     * Performs calculations of value^power mod modulus without risking overflow
     */
    public static BigInteger raiseExponentInModulus(BigInteger value, BigInteger power, BigInteger modulus) {
        return value.modPow(power, modulus);
    }


    /**
     * Finds inverse of a value with respect of a prime modulus
     * or the solution x of the relation of congruence value * x = 1 mod modulus
     *
     * @param value   the value to find the inverse of
     * @param modulus modulus with respect to which operations are performed
     * @throws IllegalArgumentException if modulus is not prime
     */
    public static BigInteger modInverseWithPrimeModulus(BigInteger value, BigInteger modulus) {

        if (!testPrime(modulus)) {
            throw new IllegalArgumentException(String.format("Incorrect value %d for modulus provided. Modulus should be prime number",
                    modulus.intValue()));
        }
        return value.modInverse(modulus);
    }


    //TODO compare methods accuracy with miller-rabin or solovay-strassen  must have different variants for space of comparison
    public static boolean testPrime(BigInteger value) {
        return value.isProbablePrime(CERTAINTY);
    }


    /**
     * Finds a generator or a primitive root of q with respect to q where q is a prime modulus
     * a generator is a number g in the range [1, q-1]
     * that raised to the powers in range [1, q-1] gives distinct numbers with respect to modulus q
     * the multiplicative order of a generator (the smallest positive exponent, which gives g^k = 1 mod q)
     * equals Euler's totient function, which for prime q equals q-1
     * Source: https://www.geeksforgeeks.org/primitive-root-of-a-prime-number-n-modulo-n/
     *         https://en.wikipedia.org/wiki/Primitive_root_modulo_n for theoretical explanation
     *         todo: make a readme file with the source links for better readability
     *
     * @param modulus modulus with respect to which operations are performed
     * @throws IllegalArgumentException if modulus is not prime
     * @return the generator of the prime number or an empty Optional if no such exists
     */
    public static Optional<BigInteger> findGeneratorOfPrimeModulus(BigInteger modulus) {
        if (!testPrime(modulus)) {
            throw new IllegalArgumentException(String.format("Incorrect value %d for modulus provided. Modulus should be prime number", modulus.intValue()));
        }

        BigInteger eulerTotientFunction = modulus.subtract(BigInteger.ONE);

        HashSet<BigInteger> primeFactors = findPrimeFactors(eulerTotientFunction);

        for (BigInteger i = BigInteger.TWO; i.compareTo(eulerTotientFunction) <=0 ; i = i.add(BigInteger.ONE)) {
            boolean hasSmallerOrder = false;

            for(BigInteger factor : primeFactors) {
                // Check if i^((eulerTotien)/factor) mod n
                // is 1, if yes then a smaller multiplicative order is found and the number cannot be generator

                if(raiseExponentInModulus(i, eulerTotientFunction.divide(factor), modulus).compareTo(BigInteger.ONE) == 0) {
                    hasSmallerOrder = true;
                    break;
                }
            }

            if(!hasSmallerOrder) {
                return Optional.of(i);
            }
        }

        return Optional.empty();
    }

    /**
     * Finds all prime factors of a given number with time complexity O^(sqrt(n))
     *
     * @param value number to factorize
     * @return  factors set of values representing the number's factors
     * TODO: check if using Pollard rho's algorithm is more efficient
     */
    public static HashSet<BigInteger> findPrimeFactors( BigInteger value) {
        HashSet<BigInteger> factors = new HashSet<>();
        // Print the number of 2s that divide n
        while (value.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0) {
            factors.add(BigInteger.TWO);
            value = value.divide(BigInteger.TWO);
        }

        // n must be odd at this point. So we can skip
        // one element (Note i = i +2)
        for (BigInteger i = new BigInteger("3"); i.compareTo(value.sqrt()) <=0; i = i.add(BigInteger.TWO)) {
            // While i divides n, print i and divide n
            while (value.mod(i).equals(BigInteger.ZERO)) {
                factors.add(i);
                value = value.divide(i);
            }
        }

        // This condition is to handle the case when
        // n is a prime number greater than 2
        if (value.compareTo(BigInteger.TWO) > 0) {
            factors.add(value);
        }

        return factors;
    }

    /**
     * Finds a primitive root of given order n mod q where q is a prime modulus
     * A primitive root x has the property that x^n mod q = 1
     * and in addition, n is the smallest integer of k=1, ..., n for which r^k=1
     *
     * @param order the order n of the root of unity
     * @param modulus modulus q with respect to which operations are performed
     * @throws IllegalArgumentException      if modulus is not prime,
     *                                       or if modulus - 1 is not divisible of oder,
     * @throws UnsupportedOperationException if there is no generator of modulus q
     * @return the primitive nth root if such exists
     */
    public static BigInteger findRootOfUnity(BigInteger order, BigInteger modulus) {
        if (!testPrime(modulus)) {
            throw new IllegalArgumentException(String.format("Incorrect value %d for modulus provided." +
                    " Modulus should be prime number", modulus.intValue()));
        }

        if(!modulus.subtract(BigInteger.ONE).mod(order).equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException(String.format("Incorrect value %d for order provided." +
                    " Order should divide modulus - 1  = %d", order.intValue(), modulus.subtract(BigInteger.ONE).intValue()));
        }

        Optional<BigInteger> generator = findGeneratorOfPrimeModulus(modulus);

        if(generator.isEmpty()) {
            throw new UnsupportedOperationException(String.format("No primitive root of unity mod m = %d", modulus.intValue()));
        }

        BigInteger modulusMinusOne = modulus.subtract(BigInteger.ONE);
        BigInteger power = performBigIntegerDivisionHalfDown(modulusMinusOne, order);

        BigInteger result = raiseExponentInModulus(generator.get(),power, modulus);

        if (result.equals(BigInteger.ONE)) {
            return findRootOfUnity(order, modulus);
        }

        return result;
    }

    public static BigInteger performBigIntegerDivisionHalfDown(BigInteger dividend, BigInteger divisor) {
        BigDecimal dividendToDecimal = new BigDecimal(dividend);
        BigDecimal divisorToDecimal = new BigDecimal(divisor);
        BigDecimal result = dividendToDecimal.divide(divisorToDecimal, ROUNDING_MODE);
        return new BigInteger(String.valueOf(result.intValue()));
    }

    public static BigInteger takeRemainder(BigInteger dividend, BigInteger divisor) {
        return dividend.mod(divisor);
    }

    //TODO find a more optimal way of halving down the division of two BigIntegers
//    public static BigInteger performBigIntegerDivisionHalfDownWithoutPrecisionLoss(BigInteger dividend, BigInteger divisor) {
//        BigInteger[] result = dividend.divideAndRemainder(divisor);
//        BigInteger quotient = result[0];
//
//        // Check if the remainder requires rounding
////        if (result[1].multiply(BigInteger.valueOf(2)).compareTo(divisor) >= 0) {
////            quotient = quotient.subtract(BigInteger.ONE);
////        }
//
//        return quotient;
//    }

    //TODO go through code and ensure there is consistency in naming of variables and in comments the formulae used
    // are the same as the mathematical expressions in the theoretical part
}
