package utils.structures;


import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A wrapper class for Plaintext from RLWE based homomorphic scheme
 * holds:
 *      a single polynomial representing a plaintext from Zt[X]/(X^d+1)
 *      and modulus t
 */
public class Plaintext {

    private Polynomial polynomial;
    private BigInteger modulus;

    public Plaintext(Polynomial polynomial, BigInteger modulus) {
        this.polynomial = polynomial;
        this.modulus = modulus;
    }

    public Polynomial getPolynomial() {
        return polynomial;
    }

    public BigInteger getModulus() {
        return modulus;
    }

    @Override
    public String toString() {
        return "Plaintext = ( " + polynomial +
                " ), modulus =" + modulus +
                '}';
    }
}
