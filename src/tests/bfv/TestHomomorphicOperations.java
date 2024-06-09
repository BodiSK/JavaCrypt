package tests.bfv;

import org.junit.Before;
import org.junit.Test;
import scheme.bfv.*;
import utils.structures.Ciphertext;
import utils.structures.Plaintext;
import utils.structures.PublicKey;
import utils.structures.SecretKey;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class TestHomomorphicOperations {

    private int polynomialDegree;
    private BigInteger plaintextModulus;
    private BigInteger ciphertextModulus;
    private Parameters parameters;
    private KeyGenerator generator;
    private PublicKey pk;
    private SecretKey sk;
    private RelinearizationKeys rk;
    private BatchEncoder encoder;
    private Encryptor encryptor;
    private Decryptor decryptor;
    private Evaluator evaluator;

    @Before
    public void setUp() {
        polynomialDegree = 8;
        plaintextModulus = new BigInteger("881");
        ciphertextModulus = new BigInteger("799999999999999");

        parameters = new Parameters(polynomialDegree, plaintextModulus, ciphertextModulus);

        generator = new KeyGenerator(parameters);

        pk = generator.getPublicKey();
        sk = generator.getSecretKey();
        rk = generator.getRelinearizationKeys();

        encoder = new BatchEncoder(parameters);
        encryptor = new Encryptor(parameters, pk);
        decryptor = new Decryptor(parameters, sk);
        evaluator = new Evaluator(parameters);
    }

    @Test
    public void testMultiplication() {
        BigInteger[] message1 = {
                BigInteger.ZERO, BigInteger.valueOf(5), BigInteger.valueOf(8), BigInteger.valueOf(2),
                BigInteger.valueOf(5), BigInteger.valueOf(16), BigInteger.valueOf(4), BigInteger.valueOf(5)
        };

        BigInteger[] message2 = {
                BigInteger.ONE, BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.valueOf(4),
                BigInteger.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(7), BigInteger.valueOf(8)
        };

        Plaintext plaintextFirst = encoder.encode(message1);
        Plaintext plaintextSecond = encoder.encode(message2);

        Ciphertext ciphertextFirst = encryptor.encrypt(plaintextFirst);
        Ciphertext ciphertextSecond = encryptor.encrypt(plaintextSecond);

        Ciphertext result = evaluator.multiply(ciphertextFirst, ciphertextSecond, rk);

        Plaintext decryptedResult = decryptor.decrypt(result, null);
        BigInteger[] decodedResult = encoder.decode(decryptedResult);

        assertNotNull(decodedResult);
        assertEquals(polynomialDegree, decodedResult.length);

        BigInteger[] expectedResult = new BigInteger[] {
                new BigInteger("0"), new BigInteger("10"), new BigInteger("24"), new BigInteger("8"),
                new BigInteger("25"), new BigInteger("96"), new BigInteger("28"), new BigInteger("40")
        };

        for (int i = 0; i < polynomialDegree; i++) {
            assertEquals(expectedResult[i], decodedResult[i]);
        }
    }


    @Test
    public void testAddition() {
        BigInteger[] message1 = {
                BigInteger.ZERO, BigInteger.valueOf(5), BigInteger.valueOf(8), BigInteger.valueOf(2),
                BigInteger.valueOf(5), BigInteger.valueOf(16), BigInteger.valueOf(4), BigInteger.valueOf(5)
        };

        BigInteger[] message2 = {
                BigInteger.ONE, BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.valueOf(4),
                BigInteger.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(7), BigInteger.valueOf(8)
        };

        Plaintext plaintextFirst = encoder.encode(message1);
        Plaintext plaintextSecond = encoder.encode(message2);

        Ciphertext ciphertextFirst = encryptor.encrypt(plaintextFirst);
        Ciphertext ciphertextSecond = encryptor.encrypt(plaintextSecond);

        Ciphertext result = evaluator.add(ciphertextFirst, ciphertextSecond);

        Plaintext decryptedResult = decryptor.decrypt(result, null);
        BigInteger[] decodedResult = encoder.decode(decryptedResult);

        assertNotNull(decodedResult);
        assertEquals(polynomialDegree, decodedResult.length);

        BigInteger[] expectedResult = new BigInteger[] {
                new BigInteger("1"), new BigInteger("7"), new BigInteger("11"), new BigInteger("6"),
                new BigInteger("10"), new BigInteger("22"), new BigInteger("11"), new BigInteger("13")
        };

        for (int i = 0; i < polynomialDegree; i++) {
            assertEquals(expectedResult[i], decodedResult[i]);
        }
    }

    //tests 3 homomorphic operations in a row - one addition and two multiplications
    @Test
    public void testHomomorphicOperations() {
        BigInteger[] message1 = {
                BigInteger.ZERO, BigInteger.valueOf(5), BigInteger.valueOf(8), BigInteger.valueOf(2),
                BigInteger.valueOf(5), BigInteger.valueOf(16), BigInteger.valueOf(4), BigInteger.valueOf(5)
        };

        BigInteger[] message2 = {
                BigInteger.ONE, BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.valueOf(4),
                BigInteger.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(7), BigInteger.valueOf(8)
        };

        Plaintext plaintextFirst = encoder.encode(message1);
        Plaintext plaintextSecond = encoder.encode(message2);

        Ciphertext ciphertextFirst = encryptor.encrypt(plaintextFirst);
        Ciphertext ciphertextSecond = encryptor.encrypt(plaintextSecond);

        Ciphertext result = evaluator.multiply(ciphertextFirst, ciphertextSecond, rk);
        result = evaluator.add(ciphertextFirst, result);
        result = evaluator.multiply(ciphertextSecond, result, rk);

        Plaintext decryptedResult = decryptor.decrypt(result, null);
        BigInteger[] decodedResult = encoder.decode(decryptedResult);

        assertNotNull(decodedResult);
        assertEquals(polynomialDegree, decodedResult.length);

        BigInteger[] expectedResult = new BigInteger[] {
                new BigInteger("0"), new BigInteger("30"), new BigInteger("96"), new BigInteger("40"),
                new BigInteger("150"), new BigInteger("672"), new BigInteger("224"), new BigInteger("360")
        };

        for (int i = 0; i < polynomialDegree; i++) {
            assertEquals(expectedResult[i], decodedResult[i]);
        }

    }
}
