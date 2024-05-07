package utils.structures;


import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A wrapper class for ciphertext from RLWE based homomorphic scheme
 * holds a tuple of polynomials representing an encrypted message from Zq[X]/(X^d+1)
 * scaling factor and modulus q
 *
 */
public class Ciphertext {

    private Polynomial encryptionPolynomial;
    private Polynomial additionalComponent;
    private BigInteger scalingFactor;
    private BigInteger modulus;

    public Ciphertext(Polynomial encryptionPolynomial, Polynomial additionalComponent, BigInteger scalingFactor, BigInteger modulus) {
        this.encryptionPolynomial = encryptionPolynomial;
        this.additionalComponent = additionalComponent;
        this.scalingFactor = scalingFactor;
        this.modulus = modulus;
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


//TODO: the number type selected might be of problem (BigInteger) must investigate the issue
//TODO: revisit the namings