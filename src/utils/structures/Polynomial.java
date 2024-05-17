package utils.structures;

import org.apache.commons.math3.complex.Complex;
import utils.Utilities;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import utils.operations.AlgebraicOperations;
import utils.operations.RoundingOperations;

import utils.optimizations.ChineseRemainderTheorem;
import utils.optimizations.FastFourierTransform;
import utils.optimizations.NumberTheoreticTransform;

import static utils.Constants.*;

/**
* A class representing an element from the quotient ring Zq[X]/(X^d+1).
 * Also holds the necessary functions for performing polynomial operations inside the ring.
 * Attributes:
 *              array of BigInteger values representing the coefficients of the polynomial
 *              integer value representing the polynomial degree
*/
public class Polynomial {

    private BigInteger[] coefficients;
    private int polynomialDegree;

    /**
     * Initializes Polynomial in the ring Zq[X]/(X^d+1) with the given coefficients.
     *
     * @param polynomialDegree degree d of the quotient polynomial.
     * @param coefficients array representing coefficients of polynomial.
     * @throws IllegalArgumentException if the length of the coefficients array is not equal to the specified degree.
     */
    public Polynomial(int polynomialDegree, BigInteger[] coefficients) {
        if(coefficients.length != polynomialDegree) {
            throw new IllegalArgumentException(String.format(NON_MATCHING_DEGREE_TO_COEFFICIENT_SIZE_EXCEPTION,
                    coefficients.length, polynomialDegree));
        }

        this.coefficients = coefficients;
        this.polynomialDegree = polynomialDegree;
    }

    /**
     * Performs addition of two Polynomials in the ring Zq[X]/(X^d+1).
     *
     * @param polynomial to serve as the second addend.
     * @param modulus the modulus q.
     * @return  the result of addition of current polynomial and input polynomial with coefficients taken modulo q.
     * @throws UnsupportedOperationException if the degree of the polynomials does not match
     */
    public Polynomial add(Polynomial polynomial, BigInteger modulus) {

        if(this.polynomialDegree != polynomial.getPolynomialDegree()) {
            throw new UnsupportedOperationException(String.format(NON_MATCHING_DEGREE_WHILE_PERFORMING_OPERATION, "addition"));
        }

        BigInteger[] result = new BigInteger[this.polynomialDegree];

        for (int i = 0; i < this.coefficients.length; i++) {
            result[i] = this.coefficients[i].add(polynomial.getCoefficients()[i]);

            if(modulus != null) {
                result[i] = result[i].mod(modulus);
            }
        }

        return  new Polynomial(this.polynomialDegree, result);
    }

    /**
     * Performs addition of two Polynomials in the ring Z[X]/(X^d+1).
     * No modular reduction is applied.
     * Overloads method add with two arguments, that returns result in the ring Zq[X]/(X^d+1)
     *
     * @param polynomial to serve as the second addend.
     * @return  the result of addition of current polynomial and input polynomial.
     * @throws UnsupportedOperationException if the degree of the polynomials does not match
     */
    public Polynomial add(Polynomial polynomial) {
        return  add(polynomial, null);
    }

    /**
     * Performs subtraction of two Polynomials in the ring Zq[X]/(X^d+1).
     * @throws UnsupportedOperationException if the degree of the polynomials does not match.
     */
    public Polynomial subtract(Polynomial polynomial, BigInteger modulus) {

        if(this.polynomialDegree != polynomial.getPolynomialDegree()) {
            throw new UnsupportedOperationException(String.format(NON_MATCHING_DEGREE_WHILE_PERFORMING_OPERATION, "subtraction"));
        }

        Polynomial subtrahend = polynomial.reverseSign();
        return add(subtrahend, modulus);
    }

    /**
     * Performs subtraction of two Polynomials in the ring Z[X]/(X^d+1).
     * Does not perform modular reduction on the coefficients of the result.
     * @throws UnsupportedOperationException if the degree of the polynomials does not match.
     */
    public Polynomial subtract(Polynomial polynomial) {
        return subtract(polynomial, null);
    }

    /**
     * Initializes Polynomial with coefficients that have the negative value of the coefficients of the current polynomial .
     *
     * @return  polynomial with negative coefficients.
     */
    public Polynomial reverseSign() {

        BigInteger[] result = new BigInteger[this.polynomialDegree];

        for (int i = 0; i < this.coefficients.length; i++) {
            result[i] = this.coefficients[i].negate();
        }

        return new Polynomial(this.polynomialDegree, result);
    }

    /**
     * Performs standard multiplication of two Polynomials in the ring Zq[X]/(X^d+1) with complexity O(N^2).
     *
     * @param polynomial to serve as the second multiplicand.
     * @param modulus the modulus q.
     * @return  the result of multiplication of current polynomial and input polynomial with coefficients taken modulo q
     *            and degree in range 0 to d.
     * @throws  UnsupportedOperationException if the degree of the polynomials does not match
     */
    public Polynomial multiply(Polynomial polynomial, BigInteger modulus) {

        if(this.polynomialDegree != polynomial.getPolynomialDegree()) {
            throw new UnsupportedOperationException(String.format(NON_MATCHING_DEGREE_WHILE_PERFORMING_OPERATION, "subtraction"));
        }

        int degree = this.polynomialDegree;
        BigInteger[] result = new BigInteger[degree];
        Arrays.fill(result, BigInteger.ZERO);
        BigInteger[] coefficients = polynomial.getCoefficients();

        int currentIndex;
        boolean isPositiveIndex;
        BigInteger coefficient;

        for (int i = 0; i < 2 * degree-1; i++) {
            //cycle through the coefficients, if both polynomials are of degree n, the result will be of degree 2*n
            //which in terms of highest value the power of certain coefficient can reach is maximum 2*n-2
            currentIndex = i % degree;
            isPositiveIndex = i < degree;
            coefficient = BigInteger.ZERO;

            //standard polynomial multiplication performed as a convolution
            for (int j = 0; j < degree; j++) {
                if (0 <= i - j && i-j < degree) {
                    BigInteger convolutionResult = this.coefficients[j].multiply(coefficients[i - j]);
                    coefficient = coefficient.add(convolutionResult);
                }
            }
            // respect to the relation of congruence in the quotient ring meaning that we apply the following rule
            // x^(d+1) = -x
            // x^(d+2) = -x^2 and so on
            if (!isPositiveIndex) {
                coefficient = coefficient.negate();
            }

            // Update the result with the coefficient mod modulus if provided modulus is not null
            result[currentIndex] = modulus != null
                    ?result[currentIndex].add(coefficient).mod(modulus)
                    :result[currentIndex].add(coefficient);
        }

        return new Polynomial(polynomialDegree, result);
    }

    /**
     * Performs standard multiplication of two Polynomials in the ring Z[X]/(X^d+1) with complexity O(N^2).
     * No modular reduction is applied.
     *
     * @param polynomial to serve as the second multiplicand.
     * @return  the result of multiplication of current polynomial and input polynomial with degree in range 0 to d.
     * @throws  UnsupportedOperationException if the degree of the polynomials does not match
     */
    public Polynomial multiply(Polynomial polynomial) {
        return multiply(polynomial, null);
    }

    /**
     * Performs faster multiplication of two Polynomials in the ring Zq[X]/(X^d+1) with complexity O(N*log(N))
     * using NumberTheoreticTransform optimization. First transforms both polynomial coefficients using the forward transformation
     * then applies component wise multiplication of their coefficients and performs the inverse transformation to obtain the final result.
     * Thus a
     *
     * @param polynomial to serve as the second multiplicand.
     * @param numberTheoreticTransform an instance of the number theoretic transform class to optimize the multiplication.
     * @return  the result of multiplication of current polynomial and input polynomial with coefficients taken modulo q
     *            and degree in range 0 to d.
     */
    public Polynomial multiplyNTT(Polynomial polynomial, NumberTheoreticTransform numberTheoreticTransform) {

        BigInteger[] transformedFirst = numberTheoreticTransform.forwardTransform(this.coefficients);
        BigInteger[] transformedSecond = numberTheoreticTransform.forwardTransform(polynomial.getCoefficients());

        BigInteger[] transformedResultCoefficients = new BigInteger[polynomialDegree];

        for (int i = 0; i < this.polynomialDegree; i++) {
            transformedResultCoefficients[i] = transformedFirst[i].multiply(transformedSecond[i]);
        }

        BigInteger[] coefficients = numberTheoreticTransform.inverseTransform(transformedResultCoefficients);

        return new Polynomial(this.polynomialDegree, coefficients);
    }

    /**
     * Performs multiplication of two Polynomials in the ring Zq[X]/(X^d+1) with complexity O(N*log(N))
     * using ChineseRemainderTheorem optimization. First splits the big ring Zq[X]/(X^d+1) into multiple subrings so that
     * the product of their moduli qi is equal to the modulus of the big ring q. Then using Number Theoretic Transform
     * performs fast multiplication in the subrings and finally recombines the results using Chinese Remainder Theorem.
     * This enables multiplication operations on BigInteger values to be performed on smaller values, thus reducing time complexity
     * and the risk of overflow.
     *
     * @param polynomial to serve as the second multiplicand.
     * @param chineseRemainderTheorem an instance of the chinese remainder theorem class to optimize the operations on coefficients
     *                                of the Java BigInteger range.
     * @return  the result of multiplication of current polynomial and input polynomial
     *  with coefficients taken modulo q (but in the range (-q/2, q/2])  and degree in range 0 to d.
     */
    public Polynomial multiplyCRT(Polynomial polynomial, ChineseRemainderTheorem chineseRemainderTheorem) {
        int primesLength = chineseRemainderTheorem.getPrimeNumbers().length;

        Polynomial[] crtProducts = new Polynomial[primesLength];

        for (int i = 0; i < primesLength; i++) {
            Polynomial product = multiplyNTT(polynomial, chineseRemainderTheorem.getTheoreticTransformList().get(i));
            crtProducts[i] = product;
        }

        int reconstructedCoefficientsLength = this.polynomialDegree;
        BigInteger[] coefficients = new BigInteger[reconstructedCoefficientsLength];
        List<BigInteger> deconstructedValues = new ArrayList<>();

        for (int i = 0; i < reconstructedCoefficientsLength; i++) {
            deconstructedValues.clear();
            for (int j = 0; j < primesLength; j++) {
                deconstructedValues.add(crtProducts[j].getCoefficients()[i]);
            }
            coefficients[i] = chineseRemainderTheorem.reconstruct(deconstructedValues.toArray(BigInteger[]::new));
        }
        return new Polynomial(this.polynomialDegree, coefficients)
                .applySmallModularReduction(chineseRemainderTheorem.getPrimesProduct());
    }

    /**
     * Performs faster multiplication of two Polynomials in the ring Zq[X]/(X^d+1) with complexity O(N*log(N))
     * using ChineseRemainderTheorem optimization and with the use of Java stream API with parallel stream.
     * The parallel stream would optimize the performance in cases of large arrays.
     * For smaller arrays the parallelization could introduce unnecessary overhead.
     *
     * @param polynomial to serve as the second multiplicand.
     * @param chineseRemainderTheorem an instance of the chinese remainder theorem class to optimize the operations on coefficients
     *                                of the Java BigInteger range.
     * @return  the result of multiplication of current polynomial and input polynomial
     *  with coefficients taken modulo q (but in the range (-q/2, q/2])  and degree in range 0 to d.
     */
    public Polynomial multiplyCRTParallel(Polynomial polynomial, ChineseRemainderTheorem chineseRemainderTheorem) {
        int primesLength = chineseRemainderTheorem.getPrimeNumbers().length;
        Polynomial[] crtProducts = new Polynomial[primesLength];

        IntStream.range(0, primesLength)
                .parallel()
                .forEach(i -> {
                    Polynomial product = multiplyNTT(polynomial, chineseRemainderTheorem.getTheoreticTransformList().get(i));
                    crtProducts[i] = product;
                });

        int reconstructedCoefficientsLength = this.polynomialDegree;
        BigInteger[] coefficients = new BigInteger[reconstructedCoefficientsLength];
        List<BigInteger> deconstructedValues = new ArrayList<>();

        IntStream.range(0, reconstructedCoefficientsLength)
                .parallel()
                .forEach(i -> {
                    deconstructedValues.clear();
                    for (int j = 0; j < primesLength; j++) {
                        deconstructedValues.add(crtProducts[j].getCoefficients()[i]);
                    }
                    coefficients[i] = chineseRemainderTheorem.reconstruct(deconstructedValues.toArray(BigInteger[]::new));
                });

        return new Polynomial(this.polynomialDegree, coefficients)
                .applySmallModularReduction(chineseRemainderTheorem.getPrimesProduct());
    }

    /**
     * Transforms each coefficient of a given polynomial in the range (-q/2, q/2] where q is  the modulus in the ring Zq[X]/(X^d+1)
     *
     * @param modulus the modulus q with respect to which the operation is performed
     * @throws UnsupportedOperationException if the operation fails
     * @return  a Polynomial whose coefficients are transformed in the range (-q/2, q/2]
     */
    public Polynomial applySmallModularReduction(BigInteger modulus) {
        BigInteger[] transformedCoefficients =  new BigInteger[this.coefficients.length];

        BigInteger modulusHalfDown = modulus.divide(BigInteger.TWO);

        try {
            for (int i = 0; i < coefficients.length; i++) {
                transformedCoefficients[i] = AlgebraicOperations.takeRemainder(coefficients[i], modulus);

                transformedCoefficients[i] = transformedCoefficients[i].compareTo(modulusHalfDown) > 0
                                                ? transformedCoefficients[i].subtract(modulus)
                                                : transformedCoefficients[i];

            }
        }
        catch (Exception e){
            throw new UnsupportedOperationException (
                    String.format(ERROR_BY_SMALL_ROUNDING,
                    modulus.intValue()));
        }

        return new Polynomial(polynomialDegree, transformedCoefficients);
    }

    /**
     * Performs multiplication of two Polynomials in the ring Zq[X]/(X^d+1) with complexity O(N*log(N))
     * using FastFourierTransform optimization. First transforms both polynomial coefficients using the forward transformation
     * then applies component wise multiplication of their coefficients and performs the inverse transformation to obtain the final result
     *
     * @param polynomial to serve as the second multiplicand.
     * @return  the result of multiplication of current polynomial and input polynomial with coefficients taken modulo q
     *            int the range (-q/2, q/2]
     *            and degree in range 0 to d. There might be an error due to rounding.
     */
    public Polynomial multiplyFFT(Polynomial polynomial) {

        FastFourierTransform fastFourierTransform = new FastFourierTransform(this.polynomialDegree*8);

        Complex[] toAppend = new Complex[this.polynomialDegree];
        Arrays.fill(toAppend, new Complex(0,0));

        Complex[] firstCoefficientsExtended = Utilities.appendArrayTo(
                RoundingOperations.transformIntegerArrayToComplexArray(this.coefficients),
                toAppend);

        Complex[] secondCoefficientsExtended = Utilities.appendArrayTo(
                RoundingOperations.transformIntegerArrayToComplexArray(polynomial.getCoefficients()),
                toAppend);

        Complex[] transformedFirst = fastFourierTransform.forwardTransform(firstCoefficientsExtended);
        Complex[] transformedSecond = fastFourierTransform.forwardTransform(secondCoefficientsExtended);

        Complex[] transformedResultCoefficients = new Complex[polynomialDegree*2];
        Arrays.fill(transformedResultCoefficients, new Complex(0,0));

        for (int i = 0; i <this.polynomialDegree*2; i++) {
            transformedResultCoefficients[i] = transformedFirst[i].multiply(transformedSecond[i]);
        }

        Complex[] inverseTransform = fastFourierTransform.inverseTransform(transformedResultCoefficients);
        Complex[] coefficients = new Complex[this.polynomialDegree];
        Arrays.fill(coefficients, new Complex(0,0));

        for (int i = 0; i < this.polynomialDegree*2; i++) {
            int index = i % polynomialDegree;
            int sign = (i < polynomialDegree ? 1 : -1);
            coefficients[index] = coefficients[index].add(inverseTransform[i].multiply(sign));
        }

        BigInteger[] polynomialCoefficients = RoundingOperations.transformComplexArrayIntegerArray(coefficients);
        return new Polynomial(polynomialDegree, polynomialCoefficients);
    }

    /**
     * Performs multiplication on each coefficient of the polynomial with a BigInteger scalar value
     * then applies modular reduction to return the value in the range [0, q), where q is the modulus provided.
     *
     * @param scalar to multiply the polynomial with
     * @param modulus to apply the reduction with respect to. Can be null.
     * @return polynomial with coefficients the same as the original value multiplied by scalar and reduced in the ring Zq[X]/(X^d+a)
     * @throws IllegalArgumentException if the provided value for scalar is null
     */
    public Polynomial multiplyByScalar(BigInteger scalar, BigInteger modulus) {
        if(scalar == null) {
            throw new IllegalArgumentException(
                    String.format(INCORRECT_SCALAR_VALUE_EXCEPTION, "scalar multiplication")
            );
        }

        int degree = this.polynomialDegree;
        BigInteger[] scalarProductCoefficients = new BigInteger[degree];

        for (int i = 0; i < degree; i++) {
            scalarProductCoefficients[i] = coefficients[i].multiply(scalar);
            if(modulus != null) {
                scalarProductCoefficients[i] = scalarProductCoefficients[i].mod(modulus);
            }
        }

        return new Polynomial(polynomialDegree, scalarProductCoefficients);
    }

    /**
     * Performs division on each coefficient of the polynomial with a BigInteger scalar value
     * then applies modular reduction if modulus q is provided.
     * Results are rounded half down.
     *
     * @throws IllegalArgumentException if the provided value for scalar is null
     */
    public Polynomial divideByScalar(BigInteger scalar, BigInteger modulus) {
        if(scalar == null) {
            throw new IllegalArgumentException(
                    String.format(INCORRECT_SCALAR_VALUE_EXCEPTION, "scalar multiplication")
            );
        }

        int degree = this.polynomialDegree;
        BigInteger[] scalarDivisionCoefficients = new BigInteger[degree];

        for (int i = 0; i < degree; i++) {
            scalarDivisionCoefficients[i] = AlgebraicOperations.performBigIntegerDivisionHalfDown(
                    coefficients[i],
                    scalar);
            if(modulus != null) {
                scalarDivisionCoefficients[i] = scalarDivisionCoefficients[i].mod(modulus);
            }
        }

        return new Polynomial(polynomialDegree, scalarDivisionCoefficients);
    }

    /**
     * Performs multiplication on each coefficient of the polynomial with a BigDecimal scalar value.
     * The results are rounded with Half even - e.g. to the closest whole value.
     *
     * @throws IllegalArgumentException if the provided value for scalar is null
     */
    public Polynomial multiplyByNonIntegerScalar(BigDecimal scalar, BigInteger modulus) {
        if(scalar == null) {
            throw new IllegalArgumentException(
                    String.format(INCORRECT_SCALAR_VALUE_EXCEPTION, "non integer scalar multiplication")
            );
        }

        int degree = this.polynomialDegree;
        BigInteger[] scalarProductCoefficients = new BigInteger[degree];

        for (int i = 0; i < degree; i++) {
            scalarProductCoefficients[i] = (new BigDecimal(coefficients[i]).multiply(scalar, MathContext.DECIMAL128))
                    .toBigInteger();
            if(modulus != null) {
                scalarProductCoefficients[i] = scalarProductCoefficients[i].mod(modulus);
            }
        }

        return new Polynomial(polynomialDegree, scalarProductCoefficients);
    }

    /**
     * Performs division on each coefficient of the polynomial with a BigDecimal scalar value.
     * The results are rounded with down - to the smaller value.
     *
     * @throws IllegalArgumentException if the provided value for scalar is null
     */
    public Polynomial divideByNonIntegerScalar(BigDecimal scalar, BigInteger modulus) {
        if(scalar == null) {
            throw new IllegalArgumentException(
                    String.format(INCORRECT_SCALAR_VALUE_EXCEPTION, "non integer scalar division")
            );
        }

        int degree = this.polynomialDegree;
        BigInteger[] scalarDivisionCoefficients = new BigInteger[degree];

        for (int i = 0; i < degree; i++) {
            scalarDivisionCoefficients[i] = new BigDecimal(coefficients[i]).divide(scalar, RoundingMode.HALF_DOWN)
                    .toBigInteger();
            if(modulus != null) {
                scalarDivisionCoefficients[i] = scalarDivisionCoefficients[i].mod(modulus);
            }
        }

        return new Polynomial(polynomialDegree, scalarDivisionCoefficients);
    }

    /**
     * Performs modular reduction with respect to given modulus
     */
    public Polynomial getCoefficientsMod(BigInteger modulus) {
        BigInteger[] newCoefficients = Arrays.stream(this.coefficients)
                .map(coeff -> coeff.mod(modulus))
                .toArray(BigInteger[]::new);

        return new Polynomial(this.polynomialDegree, newCoefficients);
    }

    /**
     * Decomposes the coefficients of a polynomial into certain base.
     * Returns a set of polynomials, each of it has the value in the decomposition on a certain index that corresponds
     * to the decomposed value of the original coefficient on the index with respect to the given base and level.
     *
     * @return an array of polynomials
     */
    public Polynomial[] decomposeCoefficients(int base, int levels) {
        Polynomial[] result = new Polynomial[levels];
        Polynomial polynomial = new Polynomial(this.polynomialDegree, this.coefficients);

        for (int i = 0; i < levels; i++) {
            result[i] = polynomial.getCoefficientsMod(BigInteger.valueOf(base));
            polynomial = polynomial.divideByScalar(BigInteger.valueOf(base), null);
        }

        return result;
    }

    /**
     * Evaluates the polynomial with respect to given x.
     * @return  the evaluation of P(x) where P is the polynomial as function and x is the input value.
     *
     */
    public BigInteger evaluateOnValue(BigInteger value) {
        BigInteger result = coefficients[coefficients.length - 1];

        for (int i = coefficients.length - 2; i >= 0; i--) {
            result = result.multiply(value).add(coefficients[i]);
        }

        return result;
    }

    public BigInteger[] getCoefficients() {
        // return a copy of the array of coefficient to ensure immutability of the getter method
        return coefficients.clone();
    }

    public int getPolynomialDegree() {
        return polynomialDegree;
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();
        int index = this.polynomialDegree-1;

        while (index>=0){
            if (!coefficients[index].equals(BigInteger.ZERO)){
                if (!str.toString().equals("")){
                    str.append(" + ");
                }
                if (index == 0 || !coefficients[index].equals(BigInteger.ONE)){
                    str.append(coefficients[index].toString());
                }
                if (index != 0){
                    str.append("x");
                }
                if (index > 1){
                    str.append('^').append(index);
                }
            }
            index--;
        }

        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Polynomial that)) return false;
        return getPolynomialDegree() == that.getPolynomialDegree() && Arrays.equals(getCoefficients(), that.getCoefficients());
    }
}
