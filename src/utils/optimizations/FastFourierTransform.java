package utils.optimizations;

import org.apache.commons.math3.complex.Complex;
import java.math.BigInteger;

/**
 * A class encapsulating functionality for performing Fast Fourier Transform
 * for faster polynomial multiplications
 * By default the space computations are performed in is the quotient ring Zq[X]/(X^d+1)
 */
public class FastFourierTransform {

    private BigInteger polynomialDegree;
    private int transformationLength;
    private Complex[]  powersOfRootOfUnity;
    private Complex[] inversePowersOfRootsOfUnity;

    /**
     * Initializes an instance of the class
     *
     * @param transformationLength the length of the vector to be transformed, which must be double the polynomial degree d
     * @throws IllegalArgumentException if transformationLength is not twice the polynomialDegree
     */
    public FastFourierTransform(int transformationLength, BigInteger polynomialDegree) {
        if(!BigInteger.valueOf(transformationLength).equals(BigInteger.TWO.multiply(polynomialDegree))) {
            throw  new IllegalArgumentException("Transformation length must be twice the polynomial degree!");
        }

        this.transformationLength = transformationLength;
        this.polynomialDegree = polynomialDegree;

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
}
