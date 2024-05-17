package utils;

/**
 * A class that contains constants used in the code:
 * string values for the error messages, other static values used more than once in different places in the code.
 * The purpose of the class is to provide a better organized code structure in order to easily apply modifications in the code.
 */
public class Constants {

    public static final String NON_MATCHING_DEGREE_TO_COEFFICIENT_SIZE_EXCEPTION =
            "Size of coefficients array %d is not equal to degree %d of ring";

    public static final String NON_MATCHING_DEGREE_WHILE_PERFORMING_OPERATION =
            "Degree of polynomials must be the same to perform %s";

    public static final String ERROR_BY_SMALL_ROUNDING =
            "Applying small rounding on polynomial coefficients with modulus %d failed";

    public static final String INCORRECT_SCALAR_VALUE_EXCEPTION =
            "Provided null value for scalar is invalid to perform operation %s with! Please provide a correct value!";
}
