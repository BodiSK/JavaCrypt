package utils.structures;

import utils.operations.AlgebraicOperations;
import utils.optimizations.ChineseRemainderTheorem;
import utils.optimizations.NumberTheoreticTransform;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * Performs addition of two Polynomials in the ring Zq[X]/(X^d+1).
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
     * Performs standard multiplication of two Polynomials in the ring Zq[X]/(X^d+1) with complexity O(N^2).
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

    /**
     * Performs standard multiplication of two Polynomials in the ring Zq[X]/(X^d+1) with complexity O(N*log(N))
     * using NumberTheoreticTransform optimization. First transforms both polynomial coefficients using the forward transformation
     * then applies component wise multiplication of their coefficients and performs the inverse transformation to obtain the final result
     *
     * @param polynomial to serve as the second multiplicand.
     * @param numberTheoreticTransform an instance of the number theoretic transform class to optimize the multiplication.
     * @return  the result of multiplication of current polynomial and input polynomial with coefficients taken modulo q
     *            and degree in range 0 to d.
     */
    public Polynomial multiplyNTT(Polynomial polynomial, NumberTheoreticTransform numberTheoreticTransform) {

        BigInteger[] transformedFirst = numberTheoreticTransform.forwardTransform(this.coefficients);
        BigInteger[] transformedSecond = numberTheoreticTransform.forwardTransform(polynomial.getCoefficients());

        BigInteger[] transformedResultCoefficients = new BigInteger[polynomialDegree.intValue()];

        for (int i = 0; i < this.polynomialDegree.intValue(); i++) {
            transformedResultCoefficients[i] = transformedFirst[i].multiply(transformedSecond[i]);
        }

        BigInteger[] coefficients = numberTheoreticTransform.inverseTransform(transformedResultCoefficients);

        Polynomial result = new Polynomial(this.polynomialDegree, coefficients);

        return result;
    }

    /**
     * Performs standard multiplication of two Polynomials in the ring Zq[X]/(X^d+1) with complexity O(N*log(N))
     * using ChineseRemainderTheorem optimization. First splits the big ring Zq[X]/(X^d+1) into multiple subrings so that
     * the product of their moduli qi is equal to the modulus of the big ring q. Then using Number Theoretic Transform
     * performs fast multiplication in the subrings and finally recombines the results using Chinese Remainder Theorem
     *
     * @param polynomial to serve as the second multiplicand.
     * @param chineseRemainderTheorem an instance of the chinese remainder theorem class to optimize the operations on coefficients
     *                                of the Java BigInteger range.
     * @return  the result of multiplication of current polynomial and input polynomial with coefficients taken modulo q
     *            and degree in range 0 to d.
     */
    public Polynomial multiplyCRT(Polynomial polynomial, ChineseRemainderTheorem chineseRemainderTheorem) {
        int primesLength = chineseRemainderTheorem.getPrimeNumbers().length;

        Polynomial[] crtProducts = new Polynomial[primesLength];

        for (int i = 0; i < primesLength; i++) {
            Polynomial product = multiplyNTT(polynomial, chineseRemainderTheorem.getTheoreticTransformList().get(i));
            crtProducts[i] = product;
        }

        int reconstructedCoefficientsLength = this.polynomialDegree.intValue();
        BigInteger[] coefficients = new BigInteger[reconstructedCoefficientsLength];
        BigInteger[] deconstructedValues = new BigInteger[reconstructedCoefficientsLength];

        for (int i = 0; i < reconstructedCoefficientsLength; i++) {
            for (int j = 0; j < primesLength; j++) {
                deconstructedValues[i] = crtProducts[j].getCoefficients()[i];
            }
            coefficients[i] = chineseRemainderTheorem.reconstruct(deconstructedValues);
        }
        return  new Polynomial(this.polynomialDegree, coefficients)
                .applySmallRoundingToCoefficients(chineseRemainderTheorem.getPrimesProduct());
    }

    //todo implement synchronization mechanisms
//    public Polynomial multiplyCRTParallel(Polynomial polynomial, ChineseRemainderTheorem chineseRemainderTheorem) {
//        int primesLength = chineseRemainderTheorem.getPrimeNumbers().length;
//        Polynomial[] crtProducts = new Polynomial[primesLength];
//
//        // Create an ExecutorService with a fixed number of threads
//        ExecutorService executor = Executors.newFixedThreadPool(primesLength);
//
//        try {
//            // Parallelize NTT multiplication
//            CompletableFuture<Void>[] multiplicationTasks = new CompletableFuture[primesLength];
//            for (int i = 0; i < primesLength; i++) {
//                final int index = i;
//                multiplicationTasks[i] = CompletableFuture.runAsync(() -> {
//                    crtProducts[index] = multiplyNTT(polynomial, chineseRemainderTheorem.getTheoreticTransformList().get(index));
//                }, executor);
//            }
//
//            // Wait for all NTT multiplication tasks to complete
//            CompletableFuture<Void> allMultiplications = CompletableFuture.allOf(multiplicationTasks);
//            allMultiplications.join();
//
//            // Parallelize coefficient reconstruction
//            BigInteger[] coefficients = new BigInteger[this.polynomialDegree.intValue()];
//            CompletableFuture<BigInteger>[] reconstructionTasks = new CompletableFuture[coefficients.length];
//            for (int i = 0; i < coefficients.length; i++) {
//                final int index = i;
//                reconstructionTasks[i] = CompletableFuture.supplyAsync(() -> {
//                    BigInteger[] deconstructedValues = new BigInteger[primesLength];
//                    for (int j = 0; j < primesLength; j++) {
//                        deconstructedValues[j] = crtProducts[j].getCoefficients()[index];
//                    }
//                    return chineseRemainderTheorem.reconstruct(deconstructedValues);
//                }, executor);
//            }
//
//            // Wait for all coefficient reconstruction tasks to complete
//            CompletableFuture<Void> allReconstructions = CompletableFuture.allOf(reconstructionTasks);
//            allReconstructions.join();
//
//            // Retrieve results from reconstruction tasks
//            for (int i = 0; i < coefficients.length; i++) {
//                coefficients[i] = reconstructionTasks[i].join();
//            }
//
//            return new Polynomial(this.polynomialDegree, coefficients);
//        } finally {
//            // Shutdown the ExecutorService
//            executor.shutdown();
//        }
//    }

    /**
     * Transforms each coefficient of a given polynomial in the range (-q/2, q/2] where q is  the modulus in the ring Zq[X]/(X^d+1)
     *
     * @param modulus the modulus with respect to which the operation is performed
     * @throws UnsupportedOperationException if the operation fails
     * @return  a Polynomial whose coefficients are transformed in the range (-q/2, q/2]
     */
    public Polynomial applySmallRoundingToCoefficients(BigInteger modulus) {
        BigInteger[] transformedCoefficients =  new BigInteger[this.coefficients.length];

        BigInteger modulusHalfDown = AlgebraicOperations.performBigIntegerDivisionHalfDown(modulus, BigInteger.TWO);

        try {
            for (int i = 0; i < coefficients.length; i++) {
                transformedCoefficients[i] = AlgebraicOperations.takeRemainder(coefficients[i], modulus);

                transformedCoefficients[i] = transformedCoefficients[i].compareTo(modulusHalfDown) > 0
                                                ? transformedCoefficients[i].subtract(modulus)
                                                : transformedCoefficients[i];

            }
        }
        catch (Exception e){
            throw new UnsupportedOperationException (String.format("Applying small rounding on polynomial coefficients with modulus %d failed",
                    modulus.intValue()));
        }

        return new Polynomial(polynomialDegree, transformedCoefficients);
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
