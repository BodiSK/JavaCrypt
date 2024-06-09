package utils.optimizations;
import utils.operations.AlgebraicOperations;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class encapsulating functionality for optimising operations on big numbers using Chinese remainder theorem
 */
public class ChineseRemainderTheorem {

    private BigInteger polynomialDegree;
    private BigInteger[] primeNumbers;
    private BigInteger primesProduct;
    private List<NumberTheoreticTransform> theoreticTransformList;
    private final Random randomGenerator = new Random();
    private BigInteger[] precomputedDivisionResults;
    private BigInteger[] precomputedDivisionResultsInverse;

    public ChineseRemainderTheorem(BigInteger polynomialDegree, int bitSize, int numberOfPrimes) {
        this.polynomialDegree = polynomialDegree;
        //computePrimes(bitSize, numberOfPrimes);
        this.primeNumbers = generatePrimes(numberOfPrimes,bitSize, polynomialDegree);
        computeTransforms();
        computePrimesProduct();

        initializePrecomputedValues();
    }

    /**
     * Generates a list of prime numbers with specified bitLength
     * all of them must be congruent modulo M, where M is two times the polynomialDegree
     * uses Java.maths BigInteger class embedded method for better performance
     */
    private void computePrimes(int bitSize, int numberOfPrimes) {

        primeNumbers = new BigInteger[numberOfPrimes];
        BigInteger doublePolynomialDegree = polynomialDegree.multiply(BigInteger.TWO);

        // Start with a randomly generated seed value
        BigInteger seed = BigInteger.probablePrime(bitSize, randomGenerator).add(BigInteger.ONE);

        for (int i = 0; i < numberOfPrimes; ) {
            if (seed.mod(doublePolynomialDegree).equals(BigInteger.ONE) && AlgebraicOperations.testPrime(seed)) {
                primeNumbers[i++] = seed;
            }
            // Increment seed for the next iteration
            seed = seed.add(BigInteger.TWO);
        }
    }

    /**
     * Generates a list of Number theoretical transform objects, each with modulus a prime number from the list of primes
     */
    private void computeTransforms() {
        theoreticTransformList = new ArrayList<>();
        for (int i = 0; i < primeNumbers.length; i++) {
            NumberTheoreticTransform transform = new NumberTheoreticTransform(polynomialDegree, primeNumbers[i]);
            theoreticTransformList.add(transform);
        }
    }

    private void computePrimesProduct() {
        primesProduct = BigInteger.ONE;
        for (int i = 0; i < primeNumbers.length; i++) {
            primesProduct = primesProduct.multiply(primeNumbers[i]);
        }
    }

    private void initializePrecomputedValues() {
        precomputedDivisionResults = new BigInteger[primeNumbers.length];
        precomputedDivisionResultsInverse = new BigInteger[primeNumbers.length];
        for (int i = 0; i < primeNumbers.length; i++) {
            precomputedDivisionResults[i] = primesProduct.divide(primeNumbers[i]);
            precomputedDivisionResultsInverse[i] = AlgebraicOperations.modInverseWithPrimeModulus(precomputedDivisionResults[i], primeNumbers[i]);
        }
    }

    /**
     * Returns true if posPrime passes the Miller-Rabin primality test
     *
     * @param posPrime the candidate number to test for primality
     * @param tests    number of iterations
     * @return true if posPrime is probably prime
     */
    private boolean millerRabinTest(BigInteger posPrime, int tests) {
        if (posPrime.equals(BigInteger.TWO)) {
            return true;
        }
        if (posPrime.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return false;
        }

        int s = 0;
        BigInteger d = posPrime.subtract(BigInteger.ONE);
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            s++;
            d = d.divide(BigInteger.TWO);
        }

        for (int i = 0; i < tests; i++) {
            BigInteger a = randomBigIntegerMoreThanTwoLessThanPosPrime(posPrime);
            BigInteger x = a.modPow(d, posPrime);
            if (x.equals(BigInteger.ONE) || x.equals(posPrime.subtract(BigInteger.ONE))) {
                continue;
            }

            int r = 0;
            for (; r < s; r++) {
                x = x.modPow(BigInteger.TWO, posPrime);
                if (x.equals(BigInteger.ONE)) {
                    return false;
                }
                if (x.equals(posPrime.subtract(BigInteger.ONE))) {
                    break;
                }
            }
            if (r == s) {
                return false;
            }
        }
        return true;
    }

    private BigInteger randomBigIntegerMoreThanTwoLessThanPosPrime(BigInteger posPrime) {
        BigInteger minLimit = BigInteger.TWO;
        BigInteger maxLimit = posPrime.subtract(minLimit);
        BigInteger randomBigInt;

        do {
            randomBigInt = new BigInteger(posPrime.bitLength(), randomGenerator);
        } while (randomBigInt.compareTo(minLimit) < 0 || randomBigInt.compareTo(maxLimit) >= 0);

        return randomBigInt;
    }

    public BigInteger[] deconstruct(BigInteger value) {
        BigInteger[] result = new BigInteger[primeNumbers.length];

        for (int i = 0; i < primeNumbers.length; i++) {
            result[i] = AlgebraicOperations.takeRemainder(value, primeNumbers[i]);
        }

        return result;
    }

    public BigInteger reconstruct(BigInteger[] deconstructedValues) {
        if(deconstructedValues.length != primeNumbers.length) {
            throw new IllegalArgumentException("The number of elements to perform reconstruction with Chinese Remainder Theorem must equal" +
                    " the size of primes used for deconstruction");
        }

        BigInteger reconstructedValue = BigInteger.ZERO;

        for (int i = 0; i < deconstructedValues.length; i++) {
            BigInteger intermediateResult = AlgebraicOperations.takeRemainder(
                    deconstructedValues[i].multiply(precomputedDivisionResultsInverse[i]),
                    primeNumbers[i]);

            intermediateResult = AlgebraicOperations.takeRemainder(
                    intermediateResult.multiply(precomputedDivisionResults[i]),
                    primesProduct);

            reconstructedValue = AlgebraicOperations.takeRemainder(
                    reconstructedValue.add(intermediateResult),
                    primesProduct);
        }

        return reconstructedValue;

    }

    public BigInteger[] getPrimeNumbers() {
        return primeNumbers;
    }

    public List<NumberTheoreticTransform> getTheoreticTransformList() {
        return theoreticTransformList;
    }

    public BigInteger getPrimesProduct() {
        return primesProduct;
    }


    public BigInteger[] generatePrimes(int totPrimes, int primeBitSize, BigInteger polynomialDegree) {
        BigInteger mod = BigInteger.TWO.multiply(polynomialDegree);
        BigInteger[] primes = new BigInteger[totPrimes];
        BigInteger posPrime = BigInteger.TWO.pow(primeBitSize).add(BigInteger.ONE);

        for (int i = 0; i < totPrimes;) {
            if (millerRabinTest(posPrime, 200)) {
                primes[i] = posPrime;
                i++;
            }
            posPrime = posPrime.add(mod);
        }

        return primes;
    }

}
