package tests.bfv;

import scheme.bfv.BatchEncoder;
import scheme.bfv.Parameters;
import utils.operations.SamplingOperations;
import utils.structures.Plaintext;
import utils.structures.Polynomial;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TestEncoder {
    public static void main(String[] args) {

        int polynomialDegree = 8;
        BigInteger plainTextMod = BigInteger.valueOf(17);
        BigInteger cipherTextMod = new BigInteger("3fffffff000001", 16);


        Parameters params = new Parameters(polynomialDegree, plainTextMod, cipherTextMod);

        BatchEncoder encoder = new BatchEncoder(params);

        BigInteger[] toTest = SamplingOperations.normalSampling(BigInteger.ZERO, plainTextMod, polynomialDegree);

        //test encode decode
        Plaintext encodedPlaintext =  encoder.encode(toTest);
        BigInteger[] encoded = encodedPlaintext.getPolynomial().getCoefficients();
        System.out.println(Arrays.stream(toTest)
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));

        System.out.println(Arrays.stream(encoded)
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));

        BigInteger[] decoded = encoder.decode(encodedPlaintext);

        System.out.println(Arrays.stream(decoded)
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));
    }
}
