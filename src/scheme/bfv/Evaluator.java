package scheme.bfv;


import utils.structures.Ciphertext;
import utils.structures.Polynomial;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * A class encapsulating the necessary functionality to perform the homomorphic operations: addition and multiplication
 * with the relinearzation step afterwards.
 */
public class Evaluator {

    private BigInteger plaintextModulus;
    private BigInteger cipherTextModulus;
    private BigDecimal scalingFactor;

    public Evaluator(Parameters parameters) {
        this.plaintextModulus = parameters.getPlaintextModulus();
        this.cipherTextModulus = parameters.getCiphertextModulus();
        this.scalingFactor = parameters.getScalingFactor();
    }


    /**
     * Performs homomomorphic addition on two ciphertexts by adding each of the corresponding part of the ciphertext polynomial tuple.
     */
    public Ciphertext add(Ciphertext first, Ciphertext second) {
        Polynomial additionFirstPart = first.getEncryptionPolynomial().add(second.getEncryptionPolynomial(), this.cipherTextModulus);
        Polynomial additionSecondPart = first.getAdditionalComponent().add(second.getAdditionalComponent(), this.cipherTextModulus);

        return  new Ciphertext(additionFirstPart, additionSecondPart, this.scalingFactor.toBigInteger(), this.cipherTextModulus);
    }


    /**
     * Performs homomomorphic subtraction on two ciphertexts by subtracting each of the corresponding part of the ciphertext polynomial tuple.
     * Since there is a possibility that the result is negative, smallMod operation should be performed instead of standard modular reduction
     * to fit the range [-q/2, q/2).
     */
    public Ciphertext subtract(Ciphertext first, Ciphertext second) {
        Polynomial additionFirstPart = first.getEncryptionPolynomial()
                .subtract(second.getEncryptionPolynomial(), null)
                .applySmallModularReduction(cipherTextModulus);

        Polynomial additionSecondPart = first.getAdditionalComponent()
                .subtract(second.getAdditionalComponent(), null)
                .applySmallModularReduction(cipherTextModulus);

        return  new Ciphertext(additionFirstPart,
                additionSecondPart,
                this.scalingFactor.toBigInteger(),
                this.cipherTextModulus,
                true);
    }

    /**
     * Performs homomomorphic multiplication on two ciphertexts.
     * Each part of the first ciphertext polynomial tuple is multiplied with fast multiplication using FFT
     * with each of the second ciphertext polynomial tuple (total of 4 polynomial multiplications).
     * The result is consists of three parts and s then relinearized using an optimized relinearization technique based on
     * coefficient base decomposition.
     */
    public Ciphertext multiply(Ciphertext first, Ciphertext second, RelinearizationKeys relinearizationKeys) {
        Polynomial c01 = first.getEncryptionPolynomial();//ciph1.c0
        Polynomial c02 = second.getEncryptionPolynomial();//ciph2.c0

        Polynomial c11 = first.getAdditionalComponent();//ciph1.c1
        Polynomial c12 = second.getAdditionalComponent();//ciph2.c1

        Polynomial c0 = c01.multiplyFFT(c02)
                .divideByNonIntegerScalar(scalingFactor, null)
                .getCoefficientsMod(cipherTextModulus);

        Polynomial c1 = c01.multiplyFFT(c12)
                .add(c02.multiplyFFT(c11), null)
                .divideByNonIntegerScalar(scalingFactor, null)
                .getCoefficientsMod(cipherTextModulus);

        Polynomial c2 = c11.multiplyFFT(c12)
                .divideByNonIntegerScalar(scalingFactor, null)
                .getCoefficientsMod(cipherTextModulus);

        return relinearize(c0, c1, c2, relinearizationKeys);
    }

    /**
     * Reduces the size of the ciphertext after multiplication
     * by substituting the tree parts c0, c1, c2 with such polynomials c0', c1' that have the same result when evaluated.
     */
    private Ciphertext relinearize(Polynomial c0, Polynomial c1, Polynomial c2, RelinearizationKeys relinearizationKeys) {
        BigInteger base = relinearizationKeys.getBase();
        List<List<Polynomial>> keys = relinearizationKeys.getKeys();
        int levels = keys.size();

        Polynomial[] decomposed = c2.decomposeCoefficients(base, levels);

        Polynomial resultFirstPart = c0;
        Polynomial resultSecondPart = c1;

        // Perform relinearization by combining the decomposed parts with relinearization keys.
        // Each level of relinearization keys consists of two polynomials, which are used
        // to adjust the corresponding parts of the ciphertext.
        for (int i = 0; i < levels; i++) {
            resultFirstPart = resultFirstPart
                    .add(keys.get(i).get(0)
                            .multiply(decomposed[i], this.cipherTextModulus), this.cipherTextModulus);

            resultSecondPart = resultSecondPart
                    .add(keys.get(i).get(1)
                            .multiply(decomposed[i], this.cipherTextModulus), this.cipherTextModulus);
        }

        return new Ciphertext(resultFirstPart, resultSecondPart, this.scalingFactor.toBigInteger(), this.cipherTextModulus);
    }
}
