package tests;

import utils.operations.AlgebraicOperations;

import java.math.BigInteger;

public class TestNumberTheory {

    public static void main(String[] args) {

//        BigInteger value = new BigInteger("2");
//        BigInteger power = new BigInteger("10");
//        BigInteger modulus = new BigInteger("13");
//
//        System.out.println(testRaiseExponentInModulus(value, power, modulus));
//        System.out.println(AlgebraicOperations.testPrime(new BigInteger("15485863")));

        //System.out.println(AlgebraicOperations.findPrimitiveElementPrime(new BigInteger("7"), new BigInteger("5")));

        //System.out.println(AlgebraicOperations.findRootOfUnity(new BigInteger("5"), new BigInteger("6")));

//        HashSet<BigInteger> factors = AlgebraicOperations.findPrimeFactors(new BigInteger("315"));
//        StringBuilder sb = new StringBuilder();
//
//        for (BigInteger factor : factors) {
//            sb.append(factor).append(" ");
//        }
//
//        String result = sb.toString().trim(); // Trim to remove trailing space
//        System.out.println(result);


        System.out.println(AlgebraicOperations.findRootOfUnity(new BigInteger("3"), new BigInteger("13")));

    }


    private static BigInteger testRaiseExponentInModulus (BigInteger value, BigInteger power, BigInteger modulus){
        return AlgebraicOperations.raiseExponentInModulus(value, power, modulus);
    }
}
