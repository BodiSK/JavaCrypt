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

public class TestDecryptEncrypt {

    private int polynomialDegree;
    private BigInteger plaintextModulus;
    private BigInteger ciphertextModulus;
    private Parameters parameters;
    private KeyGenerator generator;
    private PublicKey pk;
    private SecretKey sk;
    private BatchEncoder encoder;
    private Encryptor encryptor;
    private Decryptor decryptor;

    @Before
    public void setUp() {
        polynomialDegree = 8;
        plaintextModulus = new BigInteger("257");
        ciphertextModulus = new BigInteger("65537");

        parameters = new Parameters(polynomialDegree, plaintextModulus, ciphertextModulus);

        generator = new KeyGenerator(parameters);

        pk = generator.getPublicKey();
        sk = generator.getSecretKey();

        encoder = new BatchEncoder(parameters);
        encryptor = new Encryptor(parameters, pk);
        decryptor = new Decryptor(parameters, sk);
    }

    @Test
    public void testEncryptDecrypt() {
        BigInteger[] message1 = {BigInteger.ZERO, BigInteger.valueOf(5), BigInteger.valueOf(8), BigInteger.valueOf(2),
                BigInteger.valueOf(5), BigInteger.valueOf(16), BigInteger.valueOf(4), BigInteger.valueOf(5)};

        BigInteger[] message2 = {BigInteger.ONE, BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.valueOf(4),
                BigInteger.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(7), BigInteger.valueOf(8)};

        Plaintext plaintextFirst = encoder.encode(message1);
        Plaintext plaintextSecond = encoder.encode(message2);

        Ciphertext ciphertextFirst = encryptor.encrypt(plaintextFirst);
        Ciphertext ciphertextSecond = encryptor.encrypt(plaintextSecond);

        Plaintext decryptedFirst = decryptor.decrypt(ciphertextFirst, null);
        Plaintext decryptedSecond = decryptor.decrypt(ciphertextSecond, null);

        BigInteger[] decodedFirst = encoder.decode(decryptedFirst);
        BigInteger[] decodedSecond = encoder.decode(decryptedSecond);

        assertArrayEquals(message1, decodedFirst);
        assertArrayEquals(message2, decodedSecond);
    }
}
