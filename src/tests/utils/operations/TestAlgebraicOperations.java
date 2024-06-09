package tests.utils.operations;

import org.junit.Test;
import utils.operations.AlgebraicOperations;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestAlgebraicOperations {

    @Test
    public void testRaiseExponentInModulus() {
        BigInteger value = new BigInteger("3");
        BigInteger power = new BigInteger("4");
        BigInteger modulus = new BigInteger("5");

        BigInteger result = AlgebraicOperations.raiseExponentInModulus(value, power, modulus);
        assertEquals(new BigInteger("1"), result);
    }

    @Test
    public void testModInverseWithPrimeModulus() {
        BigInteger value = new BigInteger("3");
        BigInteger modulus = new BigInteger("7");

        BigInteger result = AlgebraicOperations.modInverseWithPrimeModulus(value, modulus);
        assertEquals(new BigInteger("5"), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModInverseWithNonPrimeModulus() {
        BigInteger value = new BigInteger("3");
        BigInteger modulus = new BigInteger("6");

        AlgebraicOperations.modInverseWithPrimeModulus(value, modulus);
    }

    @Test
    public void testTestPrime() {
        BigInteger prime = new BigInteger("11");
        BigInteger nonPrime = new BigInteger("10");

        assertTrue(AlgebraicOperations.testPrime(prime));
        assertFalse(AlgebraicOperations.testPrime(nonPrime));
    }

    @Test
    public void testFindGeneratorOfPrimeModulus() {
        BigInteger primeModulus = new BigInteger("23");

        Optional<BigInteger> generator = AlgebraicOperations.findGeneratorOfPrimeModulus(primeModulus);
        assertTrue(generator.isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindGeneratorOfNonPrimeModulus() {
        BigInteger nonPrimeModulus = new BigInteger("24");

        AlgebraicOperations.findGeneratorOfPrimeModulus(nonPrimeModulus);
    }

    @Test
    public void testFindPrimeFactors() {
        BigInteger value = new BigInteger("28");

        HashSet<BigInteger> factors = AlgebraicOperations.findPrimeFactors(value);
        HashSet<BigInteger> expectedFactors = new HashSet<>();
        expectedFactors.add(new BigInteger("2"));
        expectedFactors.add(new BigInteger("7"));

        assertEquals(expectedFactors, factors);
    }

    @Test
    public void testFindRootOfUnity() {
        BigInteger order = new BigInteger("4");
        BigInteger modulus = new BigInteger("17");

        BigInteger rootOfUnity = AlgebraicOperations.findRootOfUnity(order, modulus);
        assertNotNull(rootOfUnity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindRootOfUnityWithNonPrimeModulus() {
        BigInteger order = new BigInteger("4");
        BigInteger nonPrimeModulus = new BigInteger("15");

        AlgebraicOperations.findRootOfUnity(order, nonPrimeModulus);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindRootOfUnityWithInvalidOrder() {
        BigInteger order = new BigInteger("5");
        BigInteger primeModulus = new BigInteger("17");

        AlgebraicOperations.findRootOfUnity(order, primeModulus);
    }

    @Test
    public void testPerformBigIntegerDivisionHalfDown() {
        BigInteger dividend = new BigInteger("10");
        BigInteger divisor = new BigInteger("3");

        BigInteger result = AlgebraicOperations.performBigIntegerDivisionHalfDown(dividend, divisor);
        assertEquals(new BigInteger("3"), result);
    }

    @Test
    public void testTakeRemainder() {
        BigInteger dividend = new BigInteger("10");
        BigInteger divisor = new BigInteger("3");

        BigInteger result = AlgebraicOperations.takeRemainder(dividend, divisor);
        assertEquals(new BigInteger("1"), result);
    }
}
