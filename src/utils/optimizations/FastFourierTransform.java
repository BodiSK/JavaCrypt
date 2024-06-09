package utils.optimizations;

import org.apache.commons.math3.complex.Complex;
import utils.operations.BitOperations;
import java.math.BigInteger;
import static utils.Constants.ROOTS_OF_UNITY_LENGTH_NOT_BIG_ENOUGH_EXCEPTION;

/**
 * A class encapsulating functionality for performing Fast Fourier Transform
 * for faster polynomial multiplications
 * By default the space computations are performed in is the quotient ring Zq[X]/(X^d+1)
 * it operates with a vector with complex arguments, representing a list of complex coefficients to be transformed
 */
public class FastFourierTransform {

    private int transformationLength;
    private Complex[]  powersOfRootOfUnity;
    private Complex[] inversePowersOfRootsOfUnity;

    /**
     * Initializes an instance of the class
     *
     * @param transformationLength the length of the vector to be transformed, which must be double the polynomial degree d
     */
    public FastFourierTransform(int transformationLength) {
        this.transformationLength = transformationLength;

        initializeContext();
    }

    /**
     * Initializes the powersOfRootOfUnity, inversePowersOfRootsOfUnity arrays
     */
    private void initializeContext() {

        powersOfRootOfUnity = new Complex[this.transformationLength];
        inversePowersOfRootsOfUnity = new Complex[this.transformationLength];

        for (int i = 0; i < transformationLength; i++) {
            double angle = (Math.PI*2*i)/this.transformationLength;
            powersOfRootOfUnity[i] = new Complex(Math.cos(angle), Math.sin(angle));
            inversePowersOfRootsOfUnity[i] = new Complex(Math.cos(-angle), Math.sin(-angle));
        }
    }

    /**
     * Runs an iterated version of the butterfly  transformation with time complexity O(n*log(n))
     *
     * @param inputs - the coefficients to be transformed
     * @param roots - roots of unity for forward transformation and inverse roots of unity for the inverse transformation
     */
    public Complex[] runFastFourierTransform(Complex[] inputs, Complex[] roots) {

        if(roots.length < inputs.length) {
            throw  new IllegalArgumentException(ROOTS_OF_UNITY_LENGTH_NOT_BIG_ENOUGH_EXCEPTION);
        }

        Complex[] result = BitOperations.vectorBitReversalComplex(inputs);
        int logarithmBaseTwoOfCoeffs = BitOperations.logarithmBaseTwoOfBigInteger(BigInteger.valueOf(inputs.length));

        for (int i = 1; i < logarithmBaseTwoOfCoeffs + 1; i++) {
            for (int j = 0; j < inputs.length; j += 1<<i) {
                for(int k = 0; k< (1<<(i-1)); k++) {
                    int evenIdx = j + k;
                    int oddIdx = j + k + (1<<(i-1));

                    int indexOfRootOfUnity = k*this.transformationLength >> i;
                    Complex omegaFactor = roots[indexOfRootOfUnity].multiply(result[oddIdx]);

                    Complex butterflyPlus = result[evenIdx].add(omegaFactor);
                    Complex butterflyMinus = result[evenIdx].subtract(omegaFactor);

                    result[evenIdx] = butterflyPlus;
                    result[oddIdx] = butterflyMinus;
                }
            }

        }
        return  result;
    }

    public Complex[] forwardTransform(Complex[] toTransform) {

        return runFastFourierTransform(toTransform, this.powersOfRootOfUnity);
    }

    public Complex[] inverseTransform(Complex[] toTransform) {

        Complex[] result = runFastFourierTransform(toTransform, this.inversePowersOfRootsOfUnity);

        int coefficientsLength = toTransform.length;;

        for (int i = 0; i < coefficientsLength; i++) {
            result[i] = result[i].divide(coefficientsLength);
        }

        return  result;
    }
}


