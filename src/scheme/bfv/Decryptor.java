package scheme.bfv;

import utils.structures.Ciphertext;
import utils.structures.Plaintext;
import utils.structures.Polynomial;
import utils.structures.SecretKey;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A class encapsulating the logic for decryption an encrypted message.
 * More precisely transforming an element of the Ciphertext space with modulus q - Zt[X]/(X^d+1)
 * to an element of the Plaintext space with modulus t - Zt[X]/(X^d+1)
 */
public class Decryptor {

    private BigInteger polynomialDegree;
    private BigInteger plaintextModulus;
    private BigInteger ciphertextModulus;
    private BigDecimal scalingFactor;
    private SecretKey secretKey;

    public Decryptor(Parameters parameters, SecretKey secretKey) {
        this.polynomialDegree = parameters.getPolynomialDegree();
        this.ciphertextModulus = parameters.getCiphertextModulus();
        this.plaintextModulus = parameters.getPlaintextModulus();
        this.scalingFactor = parameters.getScalingFactor();
        this.secretKey = secretKey;
    }


    public Plaintext decrypt(Ciphertext ciphertext, Polynomial extraTerm) {

        Polynomial c0 = ciphertext.getEncryptionPolynomial();
        Polynomial c1= ciphertext.getAdditionalComponent();

        Polynomial intermediateResult = c0.add(c1.multiply(secretKey.getSecret(), ciphertextModulus), ciphertextModulus);

        //an additional check to ensure that decryption works correctly even if realization did not work
        if(extraTerm!=null) {
            Polynomial squaredSecret = this.secretKey.getSecret()
                    .multiply(this.secretKey.getSecret(), this.ciphertextModulus);
            intermediateResult = intermediateResult
                    .add(squaredSecret.multiply(extraTerm, this.ciphertextModulus), this.ciphertextModulus);
        }

        intermediateResult = intermediateResult
                .divideByNonIntegerScalar(scalingFactor, null)
                .getCoefficientsMod(this.plaintextModulus);

        return new Plaintext(intermediateResult, this.plaintextModulus);
    }
}
