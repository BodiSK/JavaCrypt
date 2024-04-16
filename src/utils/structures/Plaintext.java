package utils.structures;


import java.math.BigDecimal;

/**
 * A wrapper class for Plaintext from RLWE based homomorphic scheme
 * holds a single polynomial representing a plaintext from Zt[X]/(X^d+1)
 * and modulus t
 */
public class Plaintext {

    private Polynomial polynomial;
    private BigDecimal modulus;

    public Plaintext(Polynomial polynomial, BigDecimal modulus) {
        this.polynomial = polynomial;
        this.modulus = modulus;
    }

    public Polynomial getPolynomial() {
        return polynomial;
    }

    public BigDecimal getModulus() {
        return modulus;
    }

    @Override
    public String toString() {
        return "Plaintext = ( " + polynomial +
                " ), modulus =" + modulus +
                '}';
    }
}
