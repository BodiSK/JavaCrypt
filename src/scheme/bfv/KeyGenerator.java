package scheme.bfv;

import utils.operations.RoundingOperations;
import utils.operations.SamplingOperations;
import utils.structures.Polynomial;
import utils.structures.PublicKey;
import utils.structures.SecretKey;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * A class encapsulating the logic for private and public key generation.
 * Also responsible for generating relinearization key generation.
 */
public class KeyGenerator {

    private SecretKey secretKey;
    private PublicKey publicKey;
    private RelinearizationKeys relinearizationKeys;

    public KeyGenerator(Parameters parameters) {
        generateSecretKey(parameters);
        generatePublicKey(parameters);
        generateRelinerizationKeysWithBaseDecompositionTechnique(parameters);
    }


    /**
     * Generates an instance of the secret key class as a random sequence of [-1, 0, 1]
     * using the triangular distribution
     */
    private void generateSecretKey(Parameters parameters) {
        BigInteger[] randomCoefficients = SamplingOperations
                .triangleSample(parameters.getPolynomialDegree().intValue());


        Polynomial secret = new Polynomial(parameters.getPolynomialDegree(), randomCoefficients);

        this.secretKey = new SecretKey(secret);
    }

    /**
     * Generates an instance of the public key class.
     * The public key consists of a polynomial tuple, the first part is the masked secret and the second is a random polynomial.
     */
    private void generatePublicKey(Parameters parameters) {
        //also known as a
        BigInteger[] randomCoefficients = SamplingOperations
                .normalSampling(BigInteger.ZERO, parameters.getCiphertextModulus(), parameters.getPolynomialDegree().intValue());


        Polynomial randomPolynomial = new Polynomial(parameters.getPolynomialDegree(), randomCoefficients);

        BigInteger[] randomError = SamplingOperations
                .triangleSample(parameters.getPolynomialDegree().intValue());

        Polynomial errorPolynomial = new Polynomial(parameters.getPolynomialDegree(), randomError);

        Polynomial keyFirstPart = errorPolynomial
                .add(randomPolynomial
                        .multiply(this.secretKey.getSecret(), parameters.getCiphertextModulus()), parameters.getCiphertextModulus())
                .multiplyByScalar(new BigInteger("-1"), parameters.getCiphertextModulus());

        this.publicKey = new PublicKey(keyFirstPart, randomPolynomial);
    }

    //todo - check notes row 12 downward to find an optimal solution for performing og ith less precision loss
    private void generateRelinerizationKeysWithBaseDecompositionTechnique(Parameters parameters) {
        BigInteger ciphertextMod = parameters.getCiphertextModulus();

        //todo use the guava library to perform the operation must check if the squaring and logarithm operations
        // could be performed safely on BigInteger and have as result integer without that resulting in a precision loss
        BigInteger base = RoundingOperations
                .roundSquareRootToCeil(ciphertextMod, parameters.getCiphertextModulus().sqrt());

        //todo - check the original operation - that takes the floor of log and adds one
        int levels = RoundingOperations.getRoundedLogarithmOfArbitraryBaseToFloor(ciphertextMod, base).intValue();

        BigInteger[] keys = new BigInteger[levels];
        int power = 1;

        Polynomial squaredSecretKey = this.secretKey.getSecret().multiply(this.secretKey.getSecret(), ciphertextMod);
        List<List<Polynomial>> keyTuples = new ArrayList<>();

        for (int i = 0; i < levels; i++) {
            BigInteger[] randomCoefficients = SamplingOperations
                    .normalSampling(BigInteger.ZERO, ciphertextMod, parameters.getPolynomialDegree().intValue());

            Polynomial k1 = new Polynomial(parameters.getPolynomialDegree(), randomCoefficients);

            BigInteger[] randomErrorCoefficients = SamplingOperations.triangleSample(parameters.getPolynomialDegree().intValue());

            Polynomial error = new Polynomial(parameters.getPolynomialDegree(), randomErrorCoefficients);

            //todo - there might be an error due to scalar multiply operation implementation - must check
            Polynomial k0 = this.secretKey.getSecret()
                    .multiply(k1, ciphertextMod)
                    .add(error, ciphertextMod)
                    .multiplyByScalar(new BigInteger("-1"), null)
                    .add(squaredSecretKey
                            .multiplyByScalar(BigInteger.valueOf(power), null), ciphertextMod)
                    .getCoefficientsMod(ciphertextMod);

            List<Polynomial> keyTuple = new ArrayList<Polynomial>();
            keyTuple.add(k0);
            keyTuple.add(k1);
            keyTuples.add(keyTuple);

            power *=base.intValue();
            power%= ciphertextMod.intValue();

        }

        this.relinearizationKeys = new RelinearizationKeys(base.intValue(), keyTuples);
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public RelinearizationKeys getRelinearizationKeys() {
        return relinearizationKeys;
    }
}
