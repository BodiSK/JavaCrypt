package demos;

import scheme.bfv.*;
import utils.operations.SamplingOperations;
import utils.structures.Ciphertext;
import utils.structures.Plaintext;
import utils.structures.PublicKey;
import utils.structures.SecretKey;

import java.math.BigInteger;

public class BatchingParameterTest {
    public static void main(String[] args)  {
        int polynomialDegree = 64;
        System.out.println(BigInteger.valueOf(polynomialDegree).bitLength());
        BigInteger plaintextModulus = new BigInteger("642049");
        //257, 337, 353, 401, 433, 449, 577, 593, 641, 673, 881,
        BigInteger ciphertextModulus = new BigInteger("99999999999999999991");
        int dataRange = 300;

        Parameters parameters = new Parameters(polynomialDegree, plaintextModulus, ciphertextModulus);

        int iterations = 300;

        double successRate = 0;

        long startTime = System.nanoTime();  // Start time

        while (iterations > 0) {

            double rate =  test(parameters, polynomialDegree, dataRange);

            successRate+=rate;

            iterations--;
        }

        System.out.println("Success rate: " + successRate*100/300);

        long endTime = System.nanoTime();  // End time
        long duration = endTime - startTime;

        System.out.println("Time taken: " + (duration / 1_000_000) + " ms");
    }

    public static double test(Parameters parameters, int polynomialDegree, int dataRange)  {
        KeyGenerator generator = new KeyGenerator(parameters);

        PublicKey pk = generator.getPublicKey();
        SecretKey sk = generator.getSecretKey();
        RelinearizationKeys rk = generator.getRelinearizationKeys();

        BatchEncoder encoder = new BatchEncoder(parameters);
        Encryptor encryptor = new Encryptor(parameters, pk);
        Decryptor decryptor = new Decryptor(parameters, sk);
        Evaluator evaluator = new Evaluator(parameters);

        BigInteger [] randomCoefficientsFirst = SamplingOperations.normalSampling(BigInteger.ZERO, BigInteger.valueOf(dataRange), polynomialDegree);
        BigInteger[] randomCoefficientsSecond = SamplingOperations.normalSampling(BigInteger.ZERO, BigInteger.valueOf(dataRange), polynomialDegree);

        Plaintext first = encoder.encode(randomCoefficientsFirst);
        Plaintext second = encoder.encode(randomCoefficientsSecond);

        Ciphertext ct1 = encryptor.encrypt(first);
        Ciphertext ct2 = encryptor.encrypt(second);

        Ciphertext result = evaluator.multiply(ct1, ct2, rk);

        Plaintext decrypted = decryptor.decrypt(result, null);
        BigInteger[] actual = encoder.decode(decrypted);

        BigInteger[] expected = new BigInteger[polynomialDegree];

        double success = 0;

        for (int i = 0; i < polynomialDegree; i++) {
            expected[i] = randomCoefficientsFirst[i].multiply(randomCoefficientsSecond[i]);
            System.out.println(expected[i] + " " + actual[i]);
            success+= expected[i].compareTo(actual[i]) == 0? 1: 0;
        }
        System.out.println();


        return success/polynomialDegree;
    }
}
