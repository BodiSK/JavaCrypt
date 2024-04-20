package utils.optimizations;

import utils.operations.AlgebraicOperations;
import utils.operations.BitOperations;

import java.math.BigInteger;

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
     * for a more optimized variant check
     * https://pdf.sciencedirectassets.com/272313/1-s2.0-S0747717113X00100/1-s2.0-S0747717113001181/main.pdf?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEM%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMSJHMEUCIQDl5uhv1AEVAZja9UE3alu03jdh1LEBX3CNjstOfKJoGAIgVGv9oZsl8K7szgjloE%2FNaq9saC%2Bpr6AvOU3AuwDCA%2FAqswUIGBAFGgwwNTkwMDM1NDY4NjUiDIbdVQfBClGvn%2FGBnCqQBcE3huYMiVLTJFQtjEMDQAkPybUblFexUEGT0wtPq8Vq%2FxHIbywli%2BfwOrixY0SPYizd1pGm1PZ5E51ayo%2B5qV1Qp0g5kMfWCye0UrfcLlCQGFq6ORdFIlPKwtsSZVOMp3aTq7BKKD9Pa4TSUBI2zSRobq1QpPlgza0Zj3eDOEl9nHfQR3zOLPsKqLLkjmS3SV%2BLfFDTMyhsQxsRPYHqExMyA3RuYzUZtefI9hDrykAJJoa9H%2BSKLl1YoARjgCAuRjvVQRfD7xl69KvQQkBRbVWt7hSOWRbOu5bdf6cKJAOkDkvvjAWZtaF4mAGWEqicq5IFF3OUEWrpYAEuJ%2BbtvcC7IiK70yb3fVifOVfP7C2%2BgohkFhn%2B%2FTTt11Nocrkc613Ebcd55RLQO5mYdftxPVSMwB4IdpCqmWU%2Bc4i9Ri5rQj8vt6BcLjbNdlHcDlURgJ5HAmIrqyhqXxbfxBe5%2BIEU25MVfLhgO5asfWFMpTeB6ON53DiJ8vmaSrpUjyTGdXKuQjVi3siE22ApagowHiCMVHjyR9k7X3kz2V132gjOOWoOJQTede6dR%2FCD03tEh84QVhln%2FY%2F0Jh5XoTNtSBVz5m2MUkE1%2BPICCFWQNqP2xkXDkvQ5pC7ABBBr9NbzT%2FuVo4ZB7Nr%2BgFO5lmZxo3absMYoXPCUJmrCT2SfpFIcCOlqyUPr4lOPl%2Fj%2FAKT35j4uzTaQDfvolspHZq8%2BGYjDzQDMBZlai4Y3r2JhllDWduagSyWCBtHaE9QXqFtJTaB8aWySI7Ko1ejtitmHy2E%2FXEQS2sQx7XnC%2BodpLzStZDh0W3zRt%2BbZM04SXLlSD7H3R%2B%2Fuu7qNu4lg9JBjhkdwA0BPw%2BNdr9HFce5KcdPBMICFirEGOrEBwF2Vy3DxEWzC962nSgEHADR%2FoHK2%2B6%2BEJ9uXh49oM%2BHp6NRgDsd%2FbYFWUPOltwpaGD5LJorKYTZ6dm2yM6sf5pT2n6iog4kfq8yUt7AhFYRY59G5wwjLf4Os2S4WuM0xoqNRKuFo%2BeCO5lMss6nybhxBhTWLqT4v%2F%2F3tgPk1rnXwXT4OGcttUYZGJnjRE3mcC8TgkX5nVDfxGzgv6Hnrcd6Gm%2B9f3KdMMC8Vt%2Bxen%2BpA&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240419T161928Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=ASIAQ3PHCVTYTOZTLHOD%2F20240419%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=6d2c4dd5ef1983b2dff18fdc4868c3233cf1ec7cac8734e7c950040ad8352889&hash=cc2330886909416a59e9492828dcfd6505286520fcefe58a79180018863e6e78&host=68042c943591013ac2b2430a89b270f6af2c76d8dfd086a07176afe7c76c2c61&pii=S0747717113001181&tid=spdf-a4ab636b-d36c-4d67-96e7-e7a0941c4d09&sid=06e70b355041194dff892f890b773b81b72bgxrqb&type=client&tsoh=d3d3LnNjaWVuY2VkaXJlY3QuY29t&ua=18015d51510754595253&rr=876e3763ed593141&cc=bg
     */
    //todo rewrite and make an explanation of the class
    public BigInteger[] runNumberTheoreticTransform(BigInteger[] inputs, BigInteger[] roots) {

        if(this.powersOfRootOfUnity.length != this.polynomialDegree.intValue()) {
            throw  new IllegalArgumentException("Roots of unity length is not big enough, must be equal to the length of coefficients");
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
            throw new IllegalArgumentException(String.format("Size of coefficients array %d is not equal to degree %d of ring",
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
            throw new IllegalArgumentException(String.format("Size of coefficients array %d is not equal to degree %d of ring",
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


    //TODO proposition - make mathematical classes be of template type to make it easier to work with complex and other types of numbers
    //TODO optimizations - https://pdf.sciencedirectassets.com/272313/1-s2.0-S0747717113X00100/1-s2.0-S0747717113001181/main.pdf?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEM%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMSJHMEUCIQDl5uhv1AEVAZja9UE3alu03jdh1LEBX3CNjstOfKJoGAIgVGv9oZsl8K7szgjloE%2FNaq9saC%2Bpr6AvOU3AuwDCA%2FAqswUIGBAFGgwwNTkwMDM1NDY4NjUiDIbdVQfBClGvn%2FGBnCqQBcE3huYMiVLTJFQtjEMDQAkPybUblFexUEGT0wtPq8Vq%2FxHIbywli%2BfwOrixY0SPYizd1pGm1PZ5E51ayo%2B5qV1Qp0g5kMfWCye0UrfcLlCQGFq6ORdFIlPKwtsSZVOMp3aTq7BKKD9Pa4TSUBI2zSRobq1QpPlgza0Zj3eDOEl9nHfQR3zOLPsKqLLkjmS3SV%2BLfFDTMyhsQxsRPYHqExMyA3RuYzUZtefI9hDrykAJJoa9H%2BSKLl1YoARjgCAuRjvVQRfD7xl69KvQQkBRbVWt7hSOWRbOu5bdf6cKJAOkDkvvjAWZtaF4mAGWEqicq5IFF3OUEWrpYAEuJ%2BbtvcC7IiK70yb3fVifOVfP7C2%2BgohkFhn%2B%2FTTt11Nocrkc613Ebcd55RLQO5mYdftxPVSMwB4IdpCqmWU%2Bc4i9Ri5rQj8vt6BcLjbNdlHcDlURgJ5HAmIrqyhqXxbfxBe5%2BIEU25MVfLhgO5asfWFMpTeB6ON53DiJ8vmaSrpUjyTGdXKuQjVi3siE22ApagowHiCMVHjyR9k7X3kz2V132gjOOWoOJQTede6dR%2FCD03tEh84QVhln%2FY%2F0Jh5XoTNtSBVz5m2MUkE1%2BPICCFWQNqP2xkXDkvQ5pC7ABBBr9NbzT%2FuVo4ZB7Nr%2BgFO5lmZxo3absMYoXPCUJmrCT2SfpFIcCOlqyUPr4lOPl%2Fj%2FAKT35j4uzTaQDfvolspHZq8%2BGYjDzQDMBZlai4Y3r2JhllDWduagSyWCBtHaE9QXqFtJTaB8aWySI7Ko1ejtitmHy2E%2FXEQS2sQx7XnC%2BodpLzStZDh0W3zRt%2BbZM04SXLlSD7H3R%2B%2Fuu7qNu4lg9JBjhkdwA0BPw%2BNdr9HFce5KcdPBMICFirEGOrEBwF2Vy3DxEWzC962nSgEHADR%2FoHK2%2B6%2BEJ9uXh49oM%2BHp6NRgDsd%2FbYFWUPOltwpaGD5LJorKYTZ6dm2yM6sf5pT2n6iog4kfq8yUt7AhFYRY59G5wwjLf4Os2S4WuM0xoqNRKuFo%2BeCO5lMss6nybhxBhTWLqT4v%2F%2F3tgPk1rnXwXT4OGcttUYZGJnjRE3mcC8TgkX5nVDfxGzgv6Hnrcd6Gm%2B9f3KdMMC8Vt%2Bxen%2BpA&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240419T161928Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=ASIAQ3PHCVTYTOZTLHOD%2F20240419%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=6d2c4dd5ef1983b2dff18fdc4868c3233cf1ec7cac8734e7c950040ad8352889&hash=cc2330886909416a59e9492828dcfd6505286520fcefe58a79180018863e6e78&host=68042c943591013ac2b2430a89b270f6af2c76d8dfd086a07176afe7c76c2c61&pii=S0747717113001181&tid=spdf-a4ab636b-d36c-4d67-96e7-e7a0941c4d09&sid=06e70b355041194dff892f890b773b81b72bgxrqb&type=client&tsoh=d3d3LnNjaWVuY2VkaXJlY3QuY29t&ua=18015d51510754595253&rr=876e3763ed593141&cc=bg
}
