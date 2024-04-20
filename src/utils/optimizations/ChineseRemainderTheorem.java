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

        computePrimes(bitSize, numberOfPrimes);
        computeTransforms();
        computePrimesProduct();

        initializePrecomputedValues();
    }

    /**
     * Generates a list of prime numbers with specified bitLength
     * all of them must be congruent modulo M, where M is two times the polynomialDegree
     * use Java.maths BigInteger class embedded method for better performance
     */
    private void computePrimes(int bitSize, int numberOfPrimes) {

        primeNumbers = new BigInteger[numberOfPrimes];
        BigInteger doublePolynomialDegree = polynomialDegree.multiply(BigInteger.TWO);

        // Start with a randomly generated seed value
        BigInteger seed = BigInteger.probablePrime(bitSize, randomGenerator);

        for (int i = 0; i < numberOfPrimes; ) {
            //todo extract certainty constant
            if (seed.mod(doublePolynomialDegree).equals(BigInteger.ONE) && seed.isProbablePrime(200)) {
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
        for (int i = 0; i < polynomialDegree.intValue(); i++) {
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
            precomputedDivisionResults[i] = AlgebraicOperations.performBigIntegerDivisionHalfDown(primesProduct, primeNumbers[i]);
            precomputedDivisionResultsInverse[i] = AlgebraicOperations.modInverseWithPrimeModulus(precomputedDivisionResults[i], primeNumbers[i]);
        }
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
            throw new IllegalArgumentException("The number of elements to perform reconstruction wirth Chinese Remainder Theorem must equal" +
                    "the size of primes used for deconstruction");
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
}
