package utils.structures;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * A wrapper class for ciphertext from RLWE based homomorphic scheme
 * holds:
 *      a tuple of polynomials representing an encrypted message from Zq[X]/(X^d+1),
 *      scaling factor which is the ratio between ciphertext space modulus and plaintext space modulus,
 *      and modulus q
 */
public class Ciphertext implements Serializable {

    private Polynomial encryptionPolynomial; // c0
    private Polynomial additionalComponent; //c1
    private BigInteger scalingFactor;
    private BigInteger modulus;
    private boolean decryptInSmallDomain;

    public Ciphertext(Polynomial encryptionPolynomial,
                      Polynomial additionalComponent,
                      BigInteger scalingFactor,
                      BigInteger modulus) {
        this.encryptionPolynomial = encryptionPolynomial;
        this.additionalComponent = additionalComponent;
        this.scalingFactor = scalingFactor;
        this.modulus = modulus;
        this.decryptInSmallDomain = false;
    }
    public Ciphertext(Polynomial encryptionPolynomial,
                      Polynomial additionalComponent,
                      BigInteger scalingFactor,
                      BigInteger modulus,
                      boolean decryptInSmallDomain) {
        this.encryptionPolynomial = encryptionPolynomial;
        this.additionalComponent = additionalComponent;
        this.scalingFactor = scalingFactor;
        this.modulus = modulus;
        this.decryptInSmallDomain = decryptInSmallDomain;
    }

    public Polynomial getEncryptionPolynomial() {
        return encryptionPolynomial;
    }

    public Polynomial getAdditionalComponent() {
        return additionalComponent;
    }

    public BigInteger getScalingFactor() {
        return scalingFactor;
    }

    public BigInteger getModulus() {
        return modulus;
    }


    @Override
    public String toString() {
        return "Ciphertext = ( " +
                 encryptionPolynomial +
                ", " + additionalComponent +
                " ), scaling factor =" + scalingFactor +
                ", modulus =" + modulus +
                '}';
    }
}