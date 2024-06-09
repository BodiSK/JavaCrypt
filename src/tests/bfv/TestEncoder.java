package tests.bfv;

import org.junit.Before;
import org.junit.Test;
import scheme.bfv.BatchEncoder;
import scheme.bfv.Parameters;
import utils.operations.SamplingOperations;
import utils.structures.Plaintext;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.Assert.*;

public class TestEncoder {

    private BatchEncoder encoder;
    private int polynomialDegree;
    private BigInteger plainTextMod;
    private BigInteger cipherTextMod;

    @Before
    public void setUp() {
        polynomialDegree = 8;
        plainTextMod = BigInteger.valueOf(17);
        cipherTextMod = new BigInteger("3fffffff000001", 16);

        Parameters params = new Parameters(polynomialDegree, plainTextMod, cipherTextMod);
        encoder = new BatchEncoder(params);
    }

    @Test
    public void testEncodeDecode() {
        BigInteger[] toTest = SamplingOperations.normalSampling(BigInteger.ZERO, plainTextMod, polynomialDegree);

        // Test encode
        Plaintext encodedPlaintext = encoder.encode(toTest);
        BigInteger[] encoded = encodedPlaintext.getPolynomial().getCoefficients();

        assertNotNull(encoded);
        assertEquals(polynomialDegree, encoded.length);

        System.out.println("Original: " + Arrays.toString(toTest));
        System.out.println("Encoded: " + Arrays.toString(encoded));

        // Test decode
        BigInteger[] decoded = encoder.decode(encodedPlaintext);

        assertNotNull(decoded);
        assertEquals(polynomialDegree, decoded.length);

        for (int i = 0; i < polynomialDegree; i++) {
            assertEquals(toTest[i].mod(plainTextMod), decoded[i]);
        }

        System.out.println("Decoded: " + Arrays.toString(decoded));
    }
}
