package utils.optimizations;

import utils.operations.AlgebraicOperations;
import utils.operations.BitOperations;
import java.math.BigInteger;
import static utils.Constants.ROOTS_OF_UNITY_LENGTH_NOT_BIG_ENOUGH_EXCEPTION;
import static utils.Constants.SIZE_OF_COEFFICIENTS_ARRAY_NOT_EQUAL_TO_RING_DEGREE_EXCEPTION;

/**
 * A class encapsulating functionality for performing Number Theoretic Transformations using Fermat's theorem
 * for faster polynomial multiplications
 * By default the space computations are performed in is the quotient ring Zq[X]/(X^d+1)
 */
public class NumberTheoreticTransform {

    private BigInteger polynomialDegree;
    private BigInteger modulus;
    private BigInteger rootOfUnity;
    private BigInteger[] powersOfRootOfUnity;
    private BigInteger[] inversePowersOfRootsOfUnity;
    private BigInteger[] reversedBits;

    /**
     * Initializes an instance of the class
     *
     * @param polynomialDegree the degree of the polynomial d
     * @param modulus modulus of the coefficients q
     * @throws IllegalArgumentException if d is not same as coefficients length
     *                                  or d is not a power of 2
     */
    public NumberTheoreticTransform(BigInteger polynomialDegree, BigInteger modulus) {
        if(!BitOperations.isPowerOfTwo(polynomialDegree)) {
            throw new IllegalArgumentException(String.format("Incorrect value %d for polynomial degree provided!" +
                            " Polynomial degree must be a power of 2",
                    polynomialDegree));
        }

        this.polynomialDegree = polynomialDegree;
        this.modulus = modulus;
        this.rootOfUnity = AlgebraicOperations.findRootOfUnity(BigInteger.TWO.multiply(polynomialDegree), modulus);
        initializeContext();
    }


    /**
     * Initializes the powersOfRootOfUnity, inversePowersOfRootsOfUnity arrays
     */
    private void initializeContext() {

        powersOfRootOfUnity = new BigInteger[this.polynomialDegree.intValue()];
        inversePowersOfRootsOfUnity = new BigInteger[this.polynomialDegree.intValue()];
        reversedBits = new BigInteger[this.polynomialDegree.intValue()];


        BigInteger inverseRootOfUnity = AlgebraicOperations.modInverseWithPrimeModulus(this.rootOfUnity, this.modulus);
        int width = BitOperations.logarithmBaseTwoOfBigInteger(polynomialDegree);

        for (int i = 0; i < polynomialDegree.intValue(); i++) {
            powersOfRootOfUnity[i] = AlgebraicOperations.takeRemainder(this.rootOfUnity.pow(i), this.modulus);
            inversePowersOfRootsOfUnity[i] = AlgebraicOperations.takeRemainder(inverseRootOfUnity.pow(i), this.modulus);
            reversedBits[i] = AlgebraicOperations.takeRemainder(BitOperations.bitReversal(BigInteger.valueOf(i), width), modulus);
        }

    }

    public BigInteger[] getPowersOfRootOfUnity() {
        return powersOfRootOfUnity;
    }

    public BigInteger[] getInversePowersOfRootsOfUnity() {
        return inversePowersOfRootsOfUnity;
    }

    /**
     * Runs an iterated version of the butterfly  transformation with time complexity O(n*log(n))
     */
    public BigInteger[] runNumberTheoreticTransform(BigInteger[] inputs, BigInteger[] roots) {

        if(this.powersOfRootOfUnity.length != this.polynomialDegree.intValue()) {
            throw  new IllegalArgumentException(ROOTS_OF_UNITY_LENGTH_NOT_BIG_ENOUGH_EXCEPTION);
        }

        BigInteger[] result = BitOperations.vectorBitReversal(inputs);
        int logarithmBaseTwoOfDegree = BitOperations.logarithmBaseTwoOfBigInteger(polynomialDegree);

        for (int i = 1; i < logarithmBaseTwoOfDegree + 1; i++) {
            for (int j = 0; j < inputs.length; j += 1<<i) {
                for(int k = 0; k< (1<<(i-1)); k++) {
                    int evenIdx = j + k;
                    int oddIdx = j + k + (1<<(i-1));

                    int indexOfRootOfUnity = (k << (1+logarithmBaseTwoOfDegree - i));
                    BigInteger omegaFactor = AlgebraicOperations.takeRemainder(
                            roots[indexOfRootOfUnity].multiply(result[oddIdx]) ,
                            modulus);

                    BigInteger butterflyPlus = AlgebraicOperations.takeRemainder(result[evenIdx].add(omegaFactor), modulus);
                    BigInteger butterflyMinus = AlgebraicOperations.takeRemainder(result[evenIdx].subtract(omegaFactor), modulus);

                    result[evenIdx] = butterflyPlus;
                    result[oddIdx] = butterflyMinus;
                }
            }

        }
        return  result;
    }

    public BigInteger[] forwardTransform(BigInteger[] toTransform) {
        if(toTransform.length != polynomialDegree.intValue()) {
            throw new IllegalArgumentException(String.format(SIZE_OF_COEFFICIENTS_ARRAY_NOT_EQUAL_TO_RING_DEGREE_EXCEPTION,
                    toTransform.length, polynomialDegree));
        }

        BigInteger[] inputs = new BigInteger[polynomialDegree.intValue()];

        for (int i = 0; i < polynomialDegree.intValue(); i++) {
            inputs[i] = AlgebraicOperations.takeRemainder(
                    toTransform[i].multiply(powersOfRootOfUnity[i]),
                    modulus);
        }

        return runNumberTheoreticTransform(inputs, this.powersOfRootOfUnity);
    }

    public BigInteger[] inverseTransform(BigInteger[] toTransform) {

        if(toTransform.length != polynomialDegree.intValue()) {
            throw new IllegalArgumentException(String.format(SIZE_OF_COEFFICIENTS_ARRAY_NOT_EQUAL_TO_RING_DEGREE_EXCEPTION,
                    toTransform.length, polynomialDegree));
        }

        BigInteger [] toScaleDown = runNumberTheoreticTransform(toTransform, this.inversePowersOfRootsOfUnity);
        BigInteger inversePolynomialDegree = AlgebraicOperations.modInverseWithPrimeModulus(this.polynomialDegree, this.modulus);

        BigInteger[] result = new BigInteger[polynomialDegree.intValue()];

        for (int i = 0; i < polynomialDegree.intValue(); i++) {
            result[i] = AlgebraicOperations.takeRemainder(
                    toScaleDown[i].multiply(inversePowersOfRootsOfUnity[i]).multiply(inversePolynomialDegree),
                    this.modulus);
        }

        return  result;
    }
}
