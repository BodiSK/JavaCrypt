package scheme.bfv;


import utils.operations.AlgebraicOperations;
import utils.optimizations.NumberTheoreticTransform;
import utils.structures.Plaintext;
import utils.structures.Polynomial;

import java.math.BigInteger;

/**
 * A class encapsulating the logic for encoding an integer into Plaintext .
 * A Polynomial from the ring Z[X]/(X^d+1) maps to a d-length vector which arguments are the polynomial evaluated
 * on the roots of X^d +1, the vector is then transformed into a Polynomial of the quotient ring.
 * An optimization with Number Theoretic Transform is obtained, where d integers are encoded into a d-length vector,
 * where d is the degree of the quotient polynomial ring - Zq[X]/(X^d+1).
 */
public class BatchEncoder {

    private int polynomialDegree;
    private BigInteger plaintextModulus;
    private NumberTheoreticTransform numberTheoreticTransform;

    public BatchEncoder(Parameters parameters) {
        this.polynomialDegree = parameters.getPolynomialDegree();
        this.plaintextModulus = parameters.getPlaintextModulus();
        this.numberTheoreticTransform = new NumberTheoreticTransform(BigInteger.valueOf(polynomialDegree), plaintextModulus);
    }


    /**
     * Encodes a list of BigInteger values, each corresponding to a message into a Plaintext Polynomial.
     *
     * @param toEncode the values to be encoded
     * @throws  IllegalArgumentException if the values to be encoded are not of same length as the polynomial degree
     * @return a Plaintext object in which the Polynomial is constructed with the transformed values
     */
    public Plaintext encode(BigInteger[] toEncode) {

        if(toEncode.length != this.polynomialDegree) {
            throw  new IllegalArgumentException("In order to perform encoding correctly the values to be encoded must be as many" +
                    "as the degree of the of the ring in which they are to be transformed!");
        }

        BigInteger[] transformedValues = this.numberTheoreticTransform.inverseTransform(toEncode);
        Polynomial plaintextPolynomial = new Polynomial(this.polynomialDegree, transformedValues);
        return new Plaintext(plaintextPolynomial, this.plaintextModulus);
    }

    /**
     * Decodes a Plaintext Polynomial coefficients into a list of BigInteger.
     *
     * @param toDecode the plaintext to be reconstructed back to the original list of messages
     * @return a list of values
     */
    public BigInteger[] decode(Plaintext toDecode) {
        BigInteger[] decodedValues = this.numberTheoreticTransform.forwardTransform(toDecode.getPolynomial().getCoefficients());

        for (int i = 0; i < decodedValues.length; i++) {
            decodedValues[i] = AlgebraicOperations.takeRemainder(decodedValues[i], this.plaintextModulus);
        }

        return decodedValues;
    }
}
