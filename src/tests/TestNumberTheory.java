package tests;

import utils.operations.NumberTheory;

import java.math.BigInteger;

public class TestNumberTheory {

    public static void main(String[] args) {

//        BigInteger value = new BigInteger("2");
//        BigInteger power = new BigInteger("10");
//        BigInteger modulus = new BigInteger("13");
//
//        System.out.println(testRaiseExponentInModulus(value, power, modulus));
//        System.out.println(NumberTheory.testPrime(new BigInteger("15485863")));

        //System.out.println(NumberTheory.findPrimitiveElementPrime(new BigInteger("7"), new BigInteger("5")));

        //System.out.println(NumberTheory.findRootOfUnity(new BigInteger("5"), new BigInteger("6")));

//        HashSet<BigInteger> factors = NumberTheory.findPrimeFactors(new BigInteger("315"));
//        StringBuilder sb = new StringBuilder();
//
//        for (BigInteger factor : factors) {
//            sb.append(factor).append(" ");
//        }
//
//        String result = sb.toString().trim(); // Trim to remove trailing space
//        System.out.println(result);


        System.out.println(NumberTheory.findRootOfUnity(new BigInteger("3"), new BigInteger("13")));

    }


    private static BigInteger testRaiseExponentInModulus (BigInteger value, BigInteger power, BigInteger modulus){
        return NumberTheory.raiseExponentInModulus(value, power, modulus);
    }
}
