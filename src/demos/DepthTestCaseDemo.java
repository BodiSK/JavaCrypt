package demos;

import scheme.bfv.*;
import utils.operations.SamplingOperations;
import utils.structures.*;

import java.math.BigInteger;

public class DepthTestCaseDemo {

    public int test(Parameters parameters, int polynomialDegree, int dataRange, String operation) {
        KeyGenerator generator = new KeyGenerator(parameters);

        PublicKey pk = generator.getPublicKey();
        SecretKey sk = generator.getSecretKey();
        RelinearizationKeys rk = generator.getRelinearizationKeys();

        BatchEncoder encoder = new BatchEncoder(parameters);
        Encryptor encryptor = new Encryptor(parameters, pk);
        Decryptor decryptor = new Decryptor(parameters, sk);
        Evaluator evaluator = new Evaluator(parameters);

        BigInteger[] randomCoefficients = SamplingOperations
                .normalSampling(BigInteger.ZERO, BigInteger.valueOf(dataRange), polynomialDegree);
        Plaintext plaintext = encoder.encode(randomCoefficients);
        Ciphertext ciphertext = encryptor.encrypt(plaintext);

        int depth = 0;
        boolean success = true;
        Ciphertext result = ciphertext;

        while (success) {
            try {
                if ("add".equals(operation)) {
                    result = evaluator.add(result, ciphertext);
                } else if ("multiply".equals(operation)) {
                    result = evaluator.multiply(result, ciphertext, rk);
                } else {
                    throw new IllegalArgumentException("Invalid operation: " + operation);
                }

                Plaintext decrypted = decryptor.decrypt(result, null);
                BigInteger[] actual = encoder.decode(decrypted);

                for (int i = 0; i < polynomialDegree; i++) {
                    if ("add".equals(operation)) {
                        if (!actual[i].equals(randomCoefficients[i].multiply(BigInteger.valueOf(depth + 2)))) {
                            success = false;
                            break;
                        }
                    } else {
                        if (!actual[i].equals(randomCoefficients[i].pow(depth + 2))) {
                            success = false;
                            break;
                        }
                    }
                }

                if (success) {
                    depth++;
                }
            } catch (Exception e) {
                success = false;
            }
        }

        return depth;
    }

    public String run(int polynomialDegree, BigInteger plaintextModulus,
                      BigInteger ciphertextModulus, int dataRange, String operation) {

        Parameters parameters = new Parameters(polynomialDegree, plaintextModulus, ciphertextModulus);

        int depth = test(parameters, polynomialDegree, dataRange, operation);

        return String.format("%s depth: %d",operation, depth);
    }
}

