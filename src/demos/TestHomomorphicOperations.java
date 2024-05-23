package demos;


import scheme.bfv.*;
import utils.operations.SamplingOperations;
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

        int polynomialDegree = 512;
        BigInteger plaintextModulus = new BigInteger("12289");
        //257, 337, 353, 401, 433, 449, 577, 593, 641, 673, 881,
        BigInteger ciphertextModulus = new BigInteger("79999999999999999");

        Parameters parameters = new Parameters(polynomialDegree, plaintextModulus, ciphertextModulus);


//        BigInteger[] message1 = {BigInteger.ZERO, BigInteger.valueOf(5), BigInteger.valueOf(8), BigInteger.valueOf(2),
//                BigInteger.valueOf(5), BigInteger.valueOf(16), BigInteger.valueOf(4), BigInteger.valueOf(5)};
//
//        BigInteger[] message2 = {BigInteger.ONE, BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.valueOf(4),
//                BigInteger.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(7), BigInteger.valueOf(8)};
//
//        Plaintext plaintextFirst = encoder.encode(message1);
//        Plaintext plaintextSecond = encoder.encode(message2);
//
//        Ciphertext ciphertextFirst = encryptor.encrypt(plaintextFirst);
//        Ciphertext ciphertextSecond = encryptor.encrypt(plaintextSecond);
//
//        //two multiplications and one addition.
//        //check if batch encoder can perform operations without modular reduction
//        //todo develop a module to perform easier parameter selection
//        Ciphertext result = evaluator.multiply(ciphertextFirst, ciphertextSecond, rk);
//        result = evaluator.add(ciphertextFirst, result);
//        result = evaluator.multiply(ciphertextSecond, result, rk);
//
//        Plaintext decryptedResult = decryptor.decrypt(result, null);
//
//        BigInteger[] decodedResult = encoder.decode(decryptedResult);

//        System.out.println(Arrays.stream(decodedResult)
//                .map(String::valueOf)
//                .collect(Collectors.joining(" ")));
    }
}
