package tests;

import utils.structures.Polynomial;

import java.math.BigInteger;

public class TestPolynomial {
    public static void main(String[] args) {
        BigInteger degree = new BigInteger("5");

        BigInteger[] polynomialACoeffs = new BigInteger[5];
        polynomialACoeffs[0] = new BigInteger("1");
        polynomialACoeffs[1] = new BigInteger("2");
        polynomialACoeffs[2] = new BigInteger("3");
        polynomialACoeffs[3] = new BigInteger("4");
        polynomialACoeffs[4] = new BigInteger("5");

        Polynomial a = new Polynomial(degree, polynomialACoeffs);
        Polynomial b = new Polynomial(degree, polynomialACoeffs);

        BigInteger modulus = new BigInteger("7");
        Polynomial additionResult = a.add(b, modulus);
        System.out.println(additionResult.toString());

        BigInteger[] multPolynomialA = new BigInteger[3];
        multPolynomialA[0] = new BigInteger("6");
        multPolynomialA[1] = new BigInteger("4");
        multPolynomialA[2] = new BigInteger("3");

        BigInteger[] multPolynomialB = new BigInteger[3];
        multPolynomialB[0] = new BigInteger("7");
        multPolynomialB[1] = new BigInteger("2");
        multPolynomialB[2] = new BigInteger("0");
        Polynomial polyA = new Polynomial(new BigInteger("3"), multPolynomialA);
        Polynomial polyB = new Polynomial(new BigInteger("3"), multPolynomialB);

        System.out.println(polyA.multiply(polyB, modulus).toString());
    }
}
