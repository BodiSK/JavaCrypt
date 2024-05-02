package utils.operations;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundingOperations {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.FLOOR;

    //TODO - all methods that do rounding operations must be placed here (extract from Algebraic operations and rename to Modular arithmetic)
    public static  int performIntegerDivisionHalfDown(int dividend, int divisor) {
        BigDecimal dividendToDecimal = new BigDecimal(dividend);
        BigDecimal divisorToDecimal = new BigDecimal(divisor);
        BigDecimal result = dividendToDecimal.divide(divisorToDecimal, ROUNDING_MODE);
        return result.intValue();
    }
}
