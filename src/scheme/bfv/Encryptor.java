package scheme.bfv;

import utils.operations.SamplingOperations;
import utils.structures.Ciphertext;
import utils.structures.Plaintext;
import utils.structures.Polynomial;
import utils.structures.PublicKey;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A class encapsulating the logic for encryption a message.
 * More precisely transforming an element of the Plaintext space with modulus t - Zt[X]/(X^d+1)
 * to an element of the Ciphertext space with modulus q - Zt[X]/(X^d+1)
 */
public class Encryptor {

    private BigInteger polynomialDegree;
    private BigInteger modulus;
    private PublicKey publicKey;
    private BigInteger delta;


    public Encryptor(Parameters parameters, PublicKey publicKey) {
        this.publicKey = publicKey;
        this.polynomialDegree = parameters.getPolynomialDegree();
        this.modulus = parameters.getCiphertextModulus();
        this.delta = parameters.getScalingFactor().toBigInteger();
    }

    public Ciphertext encrypt(Plaintext message) {

        Polynomial p0 = this.publicKey.getPk0();
        Polynomial p1 = this.publicKey.getPk1();

        //todo hard code this
        Polynomial u = new Polynomial(polynomialDegree, SamplingOperations.triangleSample(this.polynomialDegree.intValue()));
        //todo - check this lines
        Polynomial e1 = new Polynomial(polynomialDegree, SamplingOperations.triangleSample(this.polynomialDegree.intValue()));
        Polynomial e2 = new Polynomial(polynomialDegree, SamplingOperations.triangleSample(this.polynomialDegree.intValue()));

        Polynomial scaledMessage = message.getPolynomial().multiplyByScalar(delta, this.modulus);

        Polynomial firstPart = e1.add(p0.multiply(u, this.modulus), this.modulus).add(scaledMessage, this.modulus);
        Polynomial secondPart = e2.add(p1.multiply(u, this.modulus), this.modulus);

        return new Ciphertext(firstPart, secondPart, this.delta, this.modulus);
    }
}
