package tests.bfv;

import scheme.bfv.*;
import utils.structures.Ciphertext;
import utils.structures.Plaintext;
import utils.structures.PublicKey;
import utils.structures.SecretKey;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TestDecryptEncrypt {
    public static void main(String[] args) {
        int polynomialDegree = 8;
        BigInteger plaintextModulus = new BigInteger("257");
        BigInteger ciphertextModulus = new BigInteger("65537");

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

        Plaintext decryptedFirst = decryptor.decrypt(ciphertextFirst, null);
        Plaintext decryptedSecond = decryptor.decrypt(ciphertextSecond, null);

        BigInteger[] decodedFirst = encoder.decode(decryptedFirst);
        BigInteger[] decodedSecond = encoder.decode(decryptedSecond);

        System.out.println(Arrays.stream(plaintextFirst.getPolynomial().getCoefficients())
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));

        System.out.println(Arrays.stream(decryptedFirst.getPolynomial().getCoefficients())
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));

        System.out.println(Arrays.stream(decodedFirst)
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));

        System.out.println();

        System.out.println(Arrays.stream(plaintextSecond.getPolynomial().getCoefficients())
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));

        System.out.println(Arrays.stream(decryptedSecond.getPolynomial().getCoefficients())
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));

        System.out.println(Arrays.stream(decodedSecond)
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));
    }
}
