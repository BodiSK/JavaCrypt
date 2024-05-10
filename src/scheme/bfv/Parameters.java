package scheme.bfv;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * A class that holds the necessary parameters for initializing a BFV homomorphic scheme
 */
public class Parameters {

    private BigInteger polynomialDegree;
    private BigInteger plaintextModulus;
    private BigInteger ciphertextModulus;
    private BigDecimal scalingFactor;

    public Parameters(BigInteger polynomialDegree, BigInteger plaintextModulus, BigInteger ciphertextModulus) {
        this.polynomialDegree = polynomialDegree;
        this.plaintextModulus = plaintextModulus;
        this.ciphertextModulus = ciphertextModulus;

        this.scalingFactor = new BigDecimal(ciphertextModulus).divide(new BigDecimal(plaintextModulus), RoundingMode.HALF_EVEN);
    }

    public BigInteger getPolynomialDegree() {
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
