package scheme.bfv;

import utils.operations.SamplingOperations;
import utils.structures.Ciphertext;
import utils.structures.Plaintext;
import utils.structures.Polynomial;
import utils.structures.PublicKey;
import java.math.BigInteger;

/**
 * A class encapsulating the logic for encryption a message.
 * More precisely transforming an element of the Plaintext space with modulus t - Zt[X]/(X^d+1)
 * to an element of the Ciphertext space with modulus q - Zt[X]/(X^d+1)
 */
public class Encryptor {

    private int polynomialDegree;
    private BigInteger modulus;
    private PublicKey publicKey;
    private BigInteger delta;

    private Ciphertext encrypt(Plaintext message,Polynomial u, Polynomial e1, Polynomial e2) {
        Polynomial p0 = this.publicKey.getPk0();
        Polynomial p1 = this.publicKey.getPk1();

        Polynomial scaledMessage = message.getPolynomial().multiplyByScalar(delta, this.modulus);

        Polynomial firstPart = e1.add(p0.multiply(u, this.modulus), this.modulus).add(scaledMessage, this.modulus);
        Polynomial secondPart = e2.add(p1.multiply(u, this.modulus), this.modulus);

        return new Ciphertext(firstPart, secondPart, this.delta, this.modulus);
    }


    public Encryptor(Parameters parameters, PublicKey publicKey) {
        this.publicKey = publicKey;
        this.polynomialDegree = parameters.getPolynomialDegree();
        this.modulus = parameters.getCiphertextModulus();
        this.delta = parameters.getScalingFactor().toBigInteger();
    }

    public Ciphertext encrypt(Plaintext message) {

        Polynomial u = new Polynomial(polynomialDegree, SamplingOperations.triangleSample(this.polynomialDegree));
        Polynomial e1 = new Polynomial(polynomialDegree, SamplingOperations.triangleSample(this.polynomialDegree));
        Polynomial e2 = new Polynomial(polynomialDegree, SamplingOperations.triangleSample(this.polynomialDegree));

        return encrypt(message, u, e1, e2);
    }

    public Ciphertext encryptWithHammingWeight(Plaintext message, int hammingWeight) {

        Polynomial u = new Polynomial(polynomialDegree, SamplingOperations.hammingWeightSample(this.polynomialDegree, hammingWeight));
        Polynomial e1 = new Polynomial(polynomialDegree, SamplingOperations.hammingWeightSample(this.polynomialDegree, hammingWeight));
        Polynomial e2 = new Polynomial(polynomialDegree, SamplingOperations.hammingWeightSample(this.polynomialDegree, hammingWeight));

        return encrypt(message, u, e1, e2);
    }
}
