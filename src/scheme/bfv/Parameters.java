package scheme.bfv;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * A class that holds the necessary parameters for initializing a BFV homomorphic scheme
 */
public class Parameters implements Serializable {

    private int polynomialDegree;
    private BigInteger plaintextModulus;
    private BigInteger ciphertextModulus;
    private BigDecimal scalingFactor;

    public Parameters(int polynomialDegree, BigInteger plaintextModulus, BigInteger ciphertextModulus) {
        this.polynomialDegree = polynomialDegree;
        this.plaintextModulus = plaintextModulus;
        this.ciphertextModulus = ciphertextModulus;

        this.scalingFactor = new BigDecimal(ciphertextModulus).divide(new BigDecimal(plaintextModulus),10, RoundingMode.HALF_EVEN);
    }

    public int getPolynomialDegree() {
        return polynomialDegree;
    }

    public BigInteger getPlaintextModulus() {
        return plaintextModulus;
    }

    public BigInteger getCiphertextModulus() {
        return ciphertextModulus;
    }

    public BigDecimal getScalingFactor() {
        return scalingFactor;
    }
}
