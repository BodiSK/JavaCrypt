package tests.utils.structures;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.Utilities;
import utils.operations.AlgebraicOperations;
import utils.optimizations.ChineseRemainderTheorem;
import utils.optimizations.NumberTheoreticTransform;
import utils.structures.Polynomial;

import java.math.BigInteger;
import java.util.Arrays;

public class TestPolynomial {

    private Polynomial first;
    private Polynomial second;
    private Polynomial multiplicandFirst;
    private Polynomial multiplicandSecond;
    private BigInteger modulus;
    private BigInteger primeModulus;
    private int powerOfTwoDegree;
    private int degree;

    private ChineseRemainderTheorem initializeCRTObject() {
        BigInteger primeSize = BigInteger.valueOf(59);

        int numberOfPrimes = AlgebraicOperations.performBigIntegerDivisionHalfDown(
                        BigInteger.valueOf(102),
                        primeSize)
                .intValue();

        return new ChineseRemainderTheorem(BigInteger.valueOf(powerOfTwoDegree), primeSize.intValue(), numberOfPrimes);
    }

    @Before
    public void setUp() {
        degree = 5;
        modulus = new BigInteger("60");

        //first polynomial 60 + 5x + 4x^2 + x^3
        BigInteger[] polynomialFirstCoefficients = Utilities.transformArrayValuesTo(new int[]{60, 5, 4, 1, 0});
        //second polynomial 2 + 3x + 4x^2 + 2x^3 + x^4
        BigInteger[] polynomialSecondCoefficients = Utilities.transformArrayValuesTo(new int[]{2, 3, 4, 2, 1});

        //setup for basic operations
        first = new Polynomial(degree, polynomialFirstCoefficients);
        second = new Polynomial(degree, polynomialSecondCoefficients);

        powerOfTwoDegree = 4;
        primeModulus = BigInteger.valueOf(73);

        //first multiplicand x + 4x^2 + 5x^3
        BigInteger[] multiplicandFirstCoefficients = Utilities.transformArrayValuesTo(new int[]{0, 1, 4, 5});

        //first multiplicand 1 + 2x + 4x^2 + 3x^3
        BigInteger[] multiplicandSecondCoefficients = Utilities.transformArrayValuesTo(new int[]{1, 2, 4, 3});

        //setup for optimized multiplication operations
        multiplicandFirst = new Polynomial(powerOfTwoDegree, multiplicandFirstCoefficients);
        multiplicandSecond = new Polynomial(powerOfTwoDegree, multiplicandSecondCoefficients);

    }

    @Test
    public void testAddition() {
        Polynomial actual = first.add(second, modulus);
        // expected result polynomial 2 + 8x + 8x^2 + 3x^3 + x^4
        Polynomial expected = new Polynomial(degree, Utilities.transformArrayValuesTo(new int[] {2, 8, 8, 3, 1}));

        Assert.assertEquals(actual, expected);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAdditionWithNotMatchingLengthException() {
        BigInteger[] coefficients = new BigInteger[degree+1];
        Arrays.fill(coefficients, BigInteger.ONE);

        Polynomial result = first.add(new Polynomial(degree+1, coefficients), modulus);
    }

    @Test
    public void testSubtraction() {
        Polynomial actual = first.subtract(second, modulus);
        // expected result polynomial 58 + 2x + 0x^2 + -x^3 + -x^4 transformed with modular arithmetic mod 60
        // --> 58 + 2x + 59x^3 + 59x^4
        Polynomial expected = new Polynomial(degree, Utilities.transformArrayValuesTo(new int[] {58, 2, 0, 59, 59}));

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testSubtractionWithoutModReduction() {
        Polynomial actual = first.subtract(second);
        // expected result polynomial 58 + 2x + 0x^2 + -x^3 + -x^4
        Polynomial expected = new Polynomial(degree, Utilities.transformArrayValuesTo(new int[] {58, 2, 0, -1, -1}));

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testMultiplication() {
        Polynomial actual = first.multiply(second, modulus);

        // expected result polynomial 29x^4 + 34x^3 + 22x^2 + 4x + 43
        Polynomial expected = new Polynomial(degree, Utilities.transformArrayValuesTo(new int[] {43, 4, 22, 34, 29}));

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testNTTMultiplication() {
        NumberTheoreticTransform numberTheoreticTransform =
                new NumberTheoreticTransform(BigInteger.valueOf(powerOfTwoDegree), primeModulus);

        Polynomial actual = multiplicandFirst.multiplyNTT(multiplicandSecond, numberTheoreticTransform);
        Polynomial expected = multiplicandFirst.multiply(multiplicandSecond, primeModulus);

        Assert.assertEquals(expected, actual);

    }

    @Test
    public void testCRTMultiplication() {
        ChineseRemainderTheorem chineseRemainderTheorem = initializeCRTObject();

        Polynomial actual = multiplicandFirst.multiplyCRT(multiplicandSecond, chineseRemainderTheorem).getCoefficientsMod(primeModulus);

        Polynomial expected = multiplicandFirst.multiply(multiplicandSecond, primeModulus);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCRTParallelMultiplication() {

        ChineseRemainderTheorem chineseRemainderTheorem = initializeCRTObject();

        Polynomial actual = multiplicandFirst.multiplyCRTParallel(multiplicandSecond, chineseRemainderTheorem);

        Polynomial expected = multiplicandFirst.multiplyCRT(multiplicandSecond, chineseRemainderTheorem);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFFTMultiplication() {
        Polynomial actual = multiplicandFirst.multiplyFFT(multiplicandSecond);

        Polynomial expected = multiplicandFirst
                .multiply(multiplicandSecond, primeModulus)
                .applySmallModularReduction(primeModulus);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEvaluateOn() {
        BigInteger actual = multiplicandSecond.evaluateOnValue(BigInteger.valueOf(5));

        Assert.assertEquals(actual, BigInteger.valueOf(486));
    }

}
