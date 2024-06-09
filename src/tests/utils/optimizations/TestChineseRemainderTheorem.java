package tests.utils.optimizations;

import org.junit.Before;
import org.junit.Test;
import utils.optimizations.ChineseRemainderTheorem;
import java.math.BigInteger;
import static org.junit.Assert.*;

public class TestChineseRemainderTheorem {

    private ChineseRemainderTheorem chineseRemainderTheorem;
    private int numberOfPrimes;
    private int bitSize;
    private BigInteger polynomialDegree;

    @Before
    public void setUp() {
        numberOfPrimes = 4;
        bitSize = 9;
        polynomialDegree = BigInteger.valueOf(256);
        chineseRemainderTheorem = new ChineseRemainderTheorem(polynomialDegree, bitSize, numberOfPrimes);
    }

    @Test
    public void testPrimeGeneration() {
        BigInteger[] primes = chineseRemainderTheorem.getPrimeNumbers();

        assertNotNull(primes);
        assertEquals(numberOfPrimes, primes.length);

        for (BigInteger prime : primes) {
            assertTrue(prime.compareTo(BigInteger.valueOf(1L << bitSize)) > 0);
            assertEquals(BigInteger.ONE, prime.mod(BigInteger.TWO.multiply(polynomialDegree)));
        }
    }

    @Test
    public void testCrtTransformations() {
        BigInteger value = BigInteger.valueOf(178);
        BigInteger[] deconstructed = chineseRemainderTheorem.deconstruct(value);
        BigInteger reconstructed = chineseRemainderTheorem.reconstruct(deconstructed);

        assertNotNull(deconstructed);
        assertEquals(value, reconstructed);
    }

}
