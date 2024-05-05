package tests;

import utils.operations.AlgebraicOperations;
import utils.optimizations.ChineseRemainderTheorem;
import utils.optimizations.NumberTheoreticTransform;
import utils.structures.Polynomial;

import java.math.BigInteger;
import java.util.Arrays;

public class TestPolynomial {
    public static void main(String[] args) {

//        //Test subtract
//        BigInteger degree = new BigInteger("5");
//        BigInteger modulus = new BigInteger("60");
//
//        BigInteger[] polynomialACoeffs = new BigInteger[5];
//        //0, 1, 4, 5, 59
//        polynomialACoeffs[4] = new BigInteger("0");
//        polynomialACoeffs[3] = new BigInteger("1");
//        polynomialACoeffs[2] = new BigInteger("4");
//        polynomialACoeffs[1] = new BigInteger("5");
//        polynomialACoeffs[0] = new BigInteger("59");
//
//        BigInteger[] polynomialBCoeffs = new BigInteger[5];
//        //1, 2, 4, 3, 2
//        polynomialBCoeffs[4] = new BigInteger("1");
//        polynomialBCoeffs[3] = new BigInteger("2");
//        polynomialBCoeffs[2] = new BigInteger("4");
//        polynomialBCoeffs[1] = new BigInteger("3");
//        polynomialBCoeffs[0] = new BigInteger("2");
//
//        Polynomial a = new Polynomial(degree, polynomialACoeffs);
//        Polynomial b = new Polynomial(degree, polynomialBCoeffs);
//
//        Polynomial subtractionResult = a.subtract(b, modulus);
        //this works well because of the way the mod operation in BigInteger class is defined in Java
//        System.out.println(subtractionResult.toString());



        //Test fast multiplication with number theoretic transform
//        BigInteger degree = new BigInteger("4");
//        BigInteger modulus = new BigInteger("73");
//
//        BigInteger[] polynomialACoeffs = new BigInteger[4];
//        //0, 1, 4, 5
//        polynomialACoeffs[0] = new BigInteger("0");
//        polynomialACoeffs[1] = new BigInteger("1");
//        polynomialACoeffs[2] = new BigInteger("4");
//        polynomialACoeffs[3] = new BigInteger("5");
//
//        BigInteger[] polynomialBCoeffs = new BigInteger[4];
//        //1, 2, 4, 3
//        polynomialBCoeffs[0] = new BigInteger("1");
//        polynomialBCoeffs[1] = new BigInteger("2");
//        polynomialBCoeffs[2] = new BigInteger("4");
//        polynomialBCoeffs[3] = new BigInteger("3");
//
//        NumberTheoreticTransform transform = new NumberTheoreticTransform(degree, modulus);
//
//        Polynomial a = new Polynomial(degree, polynomialACoeffs);
//        Polynomial b = new Polynomial(degree, polynomialBCoeffs);
//
//        Polynomial multiplicationResult = a.multiplyNTT(b, transform);
//        Polynomial standardMultiplicationResult = a.multiply(b, modulus);
//        System.out.println(multiplicationResult.toString());
//        System.out.println(standardMultiplicationResult.toString());

        //Test fast multiplication with chinese remainder theorem

//        BigInteger mod = BigInteger.valueOf(1<<10);
//        BigInteger primeSize = BigInteger.valueOf(59);
//        BigInteger polynomialDegree = BigInteger.valueOf(1<<2);
//
//        int numberOfPrimes = AlgebraicOperations.performBigIntegerDivisionHalfDown(
//                BigInteger.valueOf(2+ 2 +4*10 + 58),
//                primeSize)
//                .intValue();
//        ChineseRemainderTheorem chineseRemainderTheorem = new ChineseRemainderTheorem(polynomialDegree, 59, numberOfPrimes);
//
//        BigInteger degree = new BigInteger("4");
//        BigInteger modulus = new BigInteger("73");
//
//        BigInteger[] polynomialACoeffs = new BigInteger[4];
//        //0, 1, 4, 5
//        polynomialACoeffs[0] = new BigInteger("0");
//        polynomialACoeffs[1] = new BigInteger("1");
//        polynomialACoeffs[2] = new BigInteger("4");
//        polynomialACoeffs[3] = new BigInteger("5");
//
//        BigInteger[] polynomialBCoeffs = new BigInteger[4];
//        //1, 2, 4, 3
//        polynomialBCoeffs[0] = new BigInteger("1");
//        polynomialBCoeffs[1] = new BigInteger("2");
//        polynomialBCoeffs[2] = new BigInteger("4");
//        polynomialBCoeffs[3] = new BigInteger("3");
//
//        Polynomial a = new Polynomial(degree, polynomialACoeffs);
//        Polynomial b = new Polynomial(degree, polynomialBCoeffs);
//
//        Polynomial crtProduct = a.multiplyCRT(b, chineseRemainderTheorem);
//        Polynomial result = crtProduct.applySmallRoundingToCoefficients(mod);
//        Polynomial actual = a.multiply(b, mod).applySmallRoundingToCoefficients(mod);
//
//        System.out.println(result.toString());
//        System.out.println(actual.toString());


        //test polynomial multiplication with fast fourier transform
        BigInteger degree = new BigInteger("4");
        BigInteger modulus = new BigInteger("73");

        BigInteger[] polynomialACoeffs = new BigInteger[4];
        //0, 1, 4, 5
        polynomialACoeffs[0] = new BigInteger("0");
        polynomialACoeffs[1] = new BigInteger("1");
        polynomialACoeffs[2] = new BigInteger("4");
        polynomialACoeffs[3] = new BigInteger("5");

        BigInteger[] polynomialBCoeffs = new BigInteger[4];
        //1, 2, 4, 3
        polynomialBCoeffs[0] = new BigInteger("1");
        polynomialBCoeffs[1] = new BigInteger("2");
        polynomialBCoeffs[2] = new BigInteger("4");
        polynomialBCoeffs[3] = new BigInteger("3");


        Polynomial a = new Polynomial(degree, polynomialACoeffs);
        Polynomial b = new Polynomial(degree, polynomialBCoeffs);

        Polynomial multiplicationResult = a.multiplyFFT(b);
        Polynomial standardMultiplicationResult = a.multiply(b, modulus);
        System.out.println(multiplicationResult.toString());
        System.out.println(standardMultiplicationResult.toString());

    }
}
