package utils.operations;

import com.google.common.math.BigIntegerMath;
import org.apache.commons.math3.complex.Complex;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.stream.Stream;


/**
 * A class encapsulating functionality for rounding operations
 * also responsible for rounding when there is an explicit conversion between two types
 */
public class RoundingOperations {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.FLOOR;

    public static  int performIntegerDivisionHalfDown(int dividend, int divisor) {
        BigDecimal dividendToDecimal = new BigDecimal(dividend);
        BigDecimal divisorToDecimal = new BigDecimal(divisor);
        //possible solution of preccision loss problem - use build in methods for conversion between BigDecimal and BigInteger
        BigDecimal result = dividendToDecimal.divide(divisorToDecimal, ROUNDING_MODE);
        return result.intValue();
    }


    public static Complex transformIntegerToComplex(BigInteger value) {
        return new Complex(value.doubleValue(), 0);
    }

    public static Complex[] transformIntegerArrayToComplexArray(BigInteger[] values) {
        return Stream.of(values)
                .map(RoundingOperations::transformIntegerToComplex)
                .toArray(Complex[]::new);
    }

    public static BigInteger transformComplexToInteger(Complex value) {
        double real = value.getReal();
        BigDecimal decimalValue = BigDecimal.valueOf(real);
        return decimalValue.toBigInteger();
    }

    public static BigInteger[] transformComplexArrayIntegerArray(Complex[] values) {
        return Stream.of(values)
                .map(RoundingOperations::transformComplexToInteger)
                .toArray(BigInteger[]::new);
    }

    /**
     * Performs ceil rounding (rounding up) on a BigInteger.sqrt() value.
     * This is necessary due to the fact that the sqrt on BigInteger is rounded
     * down
    */
    public static BigInteger roundSquareRootToCeil(BigInteger value, BigInteger squareRoot) {
        return squareRoot.multiply(squareRoot).equals(value)
                ? squareRoot
                :squareRoot.add(BigInteger.ONE);
    }


    public static BigInteger getRoundedLogarithmOfArbitraryBaseToFloor(BigInteger value, BigInteger base) {
        BigInteger logarithmValueBaseTwo = BigInteger.valueOf(BigIntegerMath.log2(value, RoundingMode.HALF_DOWN));
        BigInteger logarithmBaseBaseTwo = BigInteger.valueOf(BigIntegerMath.log2(base, RoundingMode.HALF_DOWN));

        return logarithmValueBaseTwo.divide(logarithmBaseBaseTwo);
    }
}
