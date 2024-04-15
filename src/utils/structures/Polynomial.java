package utils.structures;

import java.math.BigInteger;
import java.util.Arrays;

/**
* A class representing an element from the quotient ring Zq[X]/(X^d+1)
*/
public class Polynomial {
    private BigInteger[] coefficients;
    private BigInteger polynomialDegree;

    /**
     * Initializes Polynomial in the ring Zq[X]/(X^d+1) with the given coefficients.
     *
     * @param polynomialDegree degree d of quotient polynomial.
     * @param coefficients array representing coefficients of polynomial.
     * @throws IllegalArgumentException if the length of the coefficients array is not equal to the specified degree.
     */
    public Polynomial(BigInteger polynomialDegree, BigInteger[] coefficients) {
        if(coefficients.length != polynomialDegree.intValue()) {
            throw new IllegalArgumentException(String.format("Size of coefficients array %d is not equal to degree %d of ring",
                    coefficients.length, polynomialDegree));
        }

        this.coefficients = coefficients;
        this.polynomialDegree = polynomialDegree;
    }

    /**
     * Addition of two Polynomials in the ring Zq[X]/(X^d+1).
     *
     * @param polynomial to serve as the second addend.
     * @param modulus the modulus q.
     * @return  the result of addition of current polynomial and input polynomial with coefficients taken modulo q.
     */
    public Polynomial add(Polynomial polynomial, BigInteger modulus) {
        if(this.polynomialDegree.compareTo(polynomial.getPolynomialDegree()) != 0) {
            throw new UnsupportedOperationException("Degree of polynomials must be the same to perform addition");
        }

        if(modulus == null) {
            modulus = BigInteger.ONE;
        }

        BigInteger[] result = new BigInteger[this.polynomialDegree.intValue()];

        for (int i = 0; i < this.coefficients.length; i++) {
            result[i] = this.coefficients[i].add(polynomial.getCoefficients()[i]).mod(modulus);
        }

        return  new Polynomial(this.polynomialDegree, result);
    }

    public Polynomial subtract(Polynomial polynomial, BigInteger modulus) {
        if(this.polynomialDegree.compareTo(polynomial.getPolynomialDegree()) != 0) {
            throw new UnsupportedOperationException("Degree of polynomials must be the same to perform subtraction");
        }

        Polynomial subtrahend = polynomial.reverseSign();
        return add(subtrahend, modulus);
    }

    /**
     * Initializes Polynomial with coefficients that have the negative value of the coefficients of the current polynomial .
     *
     * @return  polynomial with negative coefficients.
     */
    public Polynomial reverseSign() {
        //TODO - could be rewritten using negate
        BigInteger[] result = new BigInteger[this.polynomialDegree.intValue()];
        Arrays.fill(result, new BigInteger("-1"));
        for (int i = 0; i < this.coefficients.length; i++) {
            result[i] = result[i].multiply(this.coefficients[i]);
        }

        return new Polynomial(this.polynomialDegree, result);
    }

    /**
     * Standard Multiplication of two Polynomials in the ring Zq[X]/(X^d+1) with complexity O(N^2).
     *
     * @param polynomial to serve as the second multiplicand.
     * @param modulus the modulus q.
     * @return  the result of multiplication of current polynomial and input polynomial with coefficients taken modulo q
     *            and degree in range 0 to d.
     */
    public Polynomial multiply(Polynomial polynomial, BigInteger modulus) {
        if(this.polynomialDegree.compareTo(polynomial.getPolynomialDegree()) != 0) {
            throw new UnsupportedOperationException("Degree of polynomials must be the same to perform multiplication");
        }

        //TODO - refactor code and check with a more comprehensive example
        int degree = this.polynomialDegree.intValue();
        BigInteger[] result = new BigInteger[degree];
        Arrays.fill(result, BigInteger.ZERO);
        BigInteger[] polyCoefficients = polynomial.getCoefficients();

        if(modulus == null) {
            modulus = BigInteger.ONE;
        }

        int index;
        boolean positive;
        BigInteger coefficient;

        /*standard polynomial multiplication performed as a convolution*/
        for (int i = 0; i < 2 * degree - 1; i++) {
            index = i % degree;
            positive = i < degree;
            coefficient = BigInteger.ZERO;
            for (int j = 0; j < degree; j++) {
                if (0 <= i - j && i-j < degree) {
                    coefficient = coefficient.add(coefficients[j].multiply(polyCoefficients[i - j]));
                }
            }
            /* respect to the relation of congruence in the quotient ring meaning that we apply the following rule
            * x^(d+1) = -x
            * x^(d+2) = -x^2 and so on */
            if (positive)
                result[index] = result[index].add(coefficient);
            else
                result[index] = result[index].subtract(coefficient);

            result[index] = result[index].mod(modulus);
        }

        return new Polynomial(polynomialDegree, result);
    }

    public Polynomial multiplyByScalar(BigInteger scalar, BigInteger modulus) {
        if(scalar == null) {
            throw new IllegalArgumentException("Scalar must be a valid value!");
        }

        if(modulus == null) {
            modulus = BigInteger.ONE;
        }

        int degree = this.polynomialDegree.intValue();
        BigInteger[] scalarProductCoefficients = new BigInteger[degree];

        for (int i = 0; i < degree; i++) {
            scalarProductCoefficients[i] = (coefficients[i].multiply(scalar)).mod(modulus);
        }

        return new Polynomial(polynomialDegree, scalarProductCoefficients);
    }

    public BigInteger[] getCoefficients() {
        return coefficients;
    }

    public BigInteger getPolynomialDegree() {
        return polynomialDegree;
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();
        for (int i = this.polynomialDegree.intValue()-1; i >= 0; i--) {
            if (!coefficients[i].equals(BigInteger.ZERO)){
                if (!str.toString().equals("")){
                    str.append(" + ");
                }
                if (i == 0 || !coefficients[i].equals(BigInteger.ONE)){
                    str.append(coefficients[i].toString());
                }
                if (i != 0){
                    str.append("x");
                }
                if (i > 1){
                    str.append('^').append(i);
                }
            }
        }

        return str.toString();
    }
}
