package utils.operations;

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

    //TODO - all methods that do rounding operations must be placed here (extract from Algebraic operations and rename to Modular arithmetic)
    // must find a way to fix the loss of precision error due ti explicit type conversion
    public static  int performIntegerDivisionHalfDown(int dividend, int divisor) {
        BigDecimal dividendToDecimal = new BigDecimal(dividend);
        BigDecimal divisorToDecimal = new BigDecimal(divisor);
        //possible solution of preccision loss problem - use build in methods for conversion between BigDecimal nd BigInteger
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
}
