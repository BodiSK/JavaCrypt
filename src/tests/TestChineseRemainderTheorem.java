package tests;

import utils.optimizations.ChineseRemainderTheorem;

import java.math.BigInteger;

public class TestChineseRemainderTheorem {
    public static void main(String[] args) {

        int numberOfPrimes = 4;
        int bitSize = 9;
        BigInteger polynomialDegree = BigInteger.valueOf(256);

        ChineseRemainderTheorem chineseRemainderTheorem = new ChineseRemainderTheorem(polynomialDegree, bitSize, numberOfPrimes);

        //test generation of primes
//        BigInteger[] primes = chineseRemainderTheorem.getPrimeNumbers();
//
//        System.out.println(primes.length == numberOfPrimes);
//
//        for (int i = 0; i < primes.length; i++) {
//            System.out.println(primes[i].compareTo(BigInteger.valueOf(1<<bitSize))> 0);
//            System.out.println(primes[i].mod(BigInteger.TWO.multiply(polynomialDegree)));
//            System.out.println(primes[i]);
//        }


        //test crt transformations - inverse and reverse

        BigInteger value = BigInteger.valueOf(178);
        var result = chineseRemainderTheorem.deconstruct(value);
        var reconstructed = chineseRemainderTheorem.reconstruct(result);

        System.out.println(reconstructed);

    }
}
