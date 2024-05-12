package demos;


import scheme.bfv.*;
import utils.structures.Ciphertext;
import utils.structures.Plaintext;
import utils.structures.PublicKey;
import utils.structures.SecretKey;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Test case for the correctness of the homomorphic operations addition and multiplication on encrypted data.
 * The class serves also as a tutorial for the way the BFV module could be used
 */
public class TestHomomorphicOperations {
    public static void main(String[] args) {

        BigInteger polynomialDegree = new BigInteger("8");
        BigInteger plaintextModulus = new BigInteger("257");
        BigInteger ciphertextModulus = new BigInteger("799999999");

        Parameters parameters = new Parameters(polynomialDegree, plaintextModulus, ciphertextModulus);

        KeyGenerator generator = new KeyGenerator(parameters);

        PublicKey pk = generator.getPublicKey();
        SecretKey sk = generator.getSecretKey();
        RelinearizationKeys rk = generator.getRelinearizationKeys();

        BatchEncoder encoder = new BatchEncoder(parameters);
        Encryptor encryptor = new Encryptor(parameters, pk);
        Decryptor decryptor = new Decryptor(parameters, sk);
        Evaluator evaluator = new Evaluator(parameters);

        // message1 = [0, 5, 8, 2, 5, 16, 4, 5]
        //    message2 = [1, 2, 3, 4, 5, 6, 7, 8]

        BigInteger[] message1 = {BigInteger.ZERO, BigInteger.valueOf(5), BigInteger.valueOf(8), BigInteger.valueOf(2),
                BigInteger.valueOf(5), BigInteger.valueOf(16), BigInteger.valueOf(4), BigInteger.valueOf(5)};

        BigInteger[] message2 = {BigInteger.ONE, BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.valueOf(4),
                BigInteger.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(7), BigInteger.valueOf(8)};

        Plaintext plaintextFirst = encoder.encode(message1);
        Plaintext plaintextSecond = encoder.encode(message2);

        Ciphertext ciphertextFirst = encryptor.encrypt(plaintextFirst);
        Ciphertext ciphertextSecond = encryptor.encrypt(plaintextSecond);

        Ciphertext result = evaluator.multiply(ciphertextFirst, ciphertextSecond, rk);

        Plaintext decryptedResult = decryptor.decrypt(result, null);

        BigInteger[] decodedResult = encoder.decode(decryptedResult);

        System.out.println(Arrays.stream(decodedResult)
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));
    }
}
