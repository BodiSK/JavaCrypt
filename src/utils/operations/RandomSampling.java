package utils.operations;

import org.apache.commons.math3.complex.Complex;
import java.math.BigInteger;
import java.util.Random;

/**
 * A class encapsulating functionality for random sampling from different distributions
 */
public class RandomSampling {
    private static final Random randomGenerator = new Random();


    /**
     * Picks a given number of values from a normal distribution in given bound.
     *
     * A more complex logic is applied to ensure that the distribution of generated values is uniform
     * across the entire range even for wider range
     * by first generating a random value with bit length of the range
     * then adjusting these values to fall within the desired range
     */
    public static BigInteger[] normalSampling(BigInteger lowerBound, BigInteger upperBound, int numberOfSamples) {

        //TODO refactor and find usage of maths libraries - Apache commons/ Big Maths/ JScience/ matlab/scilab for Java
        if(upperBound.compareTo(lowerBound)<0) {
            throw new IllegalArgumentException("Upper bound must be greater than or equal to lower bound");
        }

        BigInteger[] samples = new BigInteger[numberOfSamples];
        BigInteger range = upperBound.subtract(lowerBound);
        int bitLength = upperBound.bitLength();

        for (int i = 0; i < numberOfSamples; i++) {
            BigInteger result = new BigInteger(bitLength, randomGenerator);
            if (result.compareTo(range) >= 0) {
                result = result.subtract(range).add(lowerBound);
            } else if (result.compareTo(lowerBound) < 0) {
                result = result.add(lowerBound);
            }

            samples[i] = result;
        }
        return samples;
    }

    /**
     * Samples from a discrete triangle distribution  from [-1, 0, 1] with probabilities
     * [0.25, 0.5, 0.25], respectively.
     *
     */
    public static BigInteger[] triangleSample(int numberOfSamples) {
        BigInteger[] samples = new BigInteger[numberOfSamples];

        for(int i =0; i<numberOfSamples; i++) {
            int value = randomGenerator.nextInt(4);

            if(value == 0) {
                samples[i] = new BigInteger("-1");
            } else if (value == 1) {
                samples[i] = BigInteger.ONE;
            } else {
                samples[i] = BigInteger.ZERO;
            }
        }

        return samples;
    }

    /**
     * Generates an array from the random distribution [-1, 0, 1] with exactly hammingWeight number of nonzero elements
     *
     * @param hammingWeight the number of nonzero elements in the result
     */
    public static BigInteger[] hammingWeightSample(int numberOfSamples, int hammingWeight) {
        BigInteger[] samples = new BigInteger[numberOfSamples];

        while (hammingWeight > 0) {
            int randomIndex = randomGenerator.nextInt(numberOfSamples);

            if(samples[randomIndex] == null) {
                int randomValue = randomGenerator.nextInt(2);

                if(randomValue == 0) {
                    samples[randomIndex] = new BigInteger("-1");
                } else {
                    samples[randomIndex] = BigInteger.ONE;
                }
            }

            hammingWeight--;
        }

        return samples;
    }

    /**
     * Initializes vector with complex values with real and imaginary parts in the range [0,1)
     *
     */

    public static Complex[] complexVectorSample(int length) {
        Complex[] result = new Complex[length];

        for (int i = 0; i < length; i++) {
            double realPart = randomGenerator.nextDouble(1);
            double imaginaryPart = randomGenerator.nextDouble(1);

            result[i] = new Complex(realPart, imaginaryPart);
        }
        return result;
    }
}


//TODO: check what advantages come if we use only Apache commons math https://commons.apache.org/proper/commons-math/userguide/random.html