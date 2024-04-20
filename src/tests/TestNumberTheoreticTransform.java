package tests;

import utils.operations.AlgebraicOperations;
import utils.optimizations.NumberTheoreticTransform;
import java.math.BigInteger;
import java.util.Arrays;

public class TestNumberTheoreticTransform {

    public static void main(String[] args) {
        NumberTheoreticTransform transform = new NumberTheoreticTransform(new BigInteger("4"), new BigInteger("73"));

        System.out.println(AlgebraicOperations.findRootOfUnity(new BigInteger("8"), new BigInteger("73")));

        BigInteger[] coefficients = new BigInteger[4];

        Arrays.fill(coefficients, BigInteger.ZERO);
        coefficients[0] = new BigInteger("0");
        coefficients[1] = new BigInteger("1");
        coefficients[2] = new BigInteger("4");
        coefficients[3] = new BigInteger("5");

        BigInteger[] forward = transform.runNumberTheoreticTransform(coefficients, transform.getPowersOfRootOfUnity());
        for (BigInteger element : forward) {
            System.out.print(element + " ");
        }

        System.out.println();

        BigInteger[] scaled = Arrays.stream(forward)
                .map(el -> el.multiply(BigInteger.valueOf(-18).mod(BigInteger.valueOf(73))))
                .toArray(BigInteger[]::new);

        BigInteger[] inverse = transform.runNumberTheoreticTransform(scaled, transform.getInversePowersOfRootsOfUnity());
        for (BigInteger element : inverse) {
            System.out.print(element + " ");
        }


        //todo write tests for polynomial multiplication demonstration
    }
}
