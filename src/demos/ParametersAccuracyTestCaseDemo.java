package demos;

import scheme.bfv.*;
import utils.operations.SamplingOperations;
import utils.structures.*;

import java.math.BigInteger;

public class ParametersAccuracyTestCaseDemo {

    public double test(Parameters parameters, int polynomialDegree, int dataRange, String operation) {
        KeyGenerator generator = new KeyGenerator(parameters);

        PublicKey pk = generator.getPublicKey();
        SecretKey sk = generator.getSecretKey();
        RelinearizationKeys rk = generator.getRelinearizationKeys();

        BatchEncoder encoder = new BatchEncoder(parameters);
        Encryptor encryptor = new Encryptor(parameters, pk);
        Decryptor decryptor = new Decryptor(parameters, sk);
        Evaluator evaluator = new Evaluator(parameters);

        BigInteger[] randomCoefficientsFirst = SamplingOperations.normalSampling(BigInteger.ZERO, BigInteger.valueOf(dataRange), polynomialDegree);
        BigInteger[] randomCoefficientsSecond = SamplingOperations.normalSampling(BigInteger.ZERO, BigInteger.valueOf(dataRange), polynomialDegree);

        Plaintext first = encoder.encode(randomCoefficientsFirst);

        Plaintext second = encoder.encode(randomCoefficientsSecond);

        Ciphertext ct1 = encryptor.encrypt(first);
        Ciphertext ct2 = encryptor.encrypt(second);

        Ciphertext result;
        if ("add".equals(operation)) {
            result = evaluator.add(ct1, ct2);
        } else if ("multiply".equals(operation)) {
            result = evaluator.multiply(ct1, ct2, rk);
        } else {
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }

        Plaintext decrypted = decryptor.decrypt(result, null);
        BigInteger[] actual = encoder.decode(decrypted);

        BigInteger[] expected = new BigInteger[polynomialDegree];

        double success = 0;

        for (int i = 0; i < polynomialDegree; i++) {
            if ("add".equals(operation)) {
                expected[i] = randomCoefficientsFirst[i].add(randomCoefficientsSecond[i]);
            } else {
                expected[i] = randomCoefficientsFirst[i].multiply(randomCoefficientsSecond[i]);
            }
            //System.out.println(expected[i] + " " + actual[i]);
            success += expected[i].compareTo(actual[i]) == 0 ? 1 : 0;
        }
        //System.out.println();

        return success / polynomialDegree;
    }

    public String run(int polynomialDegree, BigInteger plaintextModulus,
                      BigInteger ciphertextModulus, int dataRange, String operation,
                      int iterations) {

        Parameters parameters = new Parameters(polynomialDegree, plaintextModulus, ciphertextModulus);

        double successRate = 0;
        int originalIterations = iterations;

        while (iterations > 0) {
            double rate = test(parameters, polynomialDegree, dataRange, operation);
            successRate += rate;
            iterations--;
        }

        return "Success rate: " + successRate * 100 / originalIterations;
    }
}
