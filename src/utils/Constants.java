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

    public static final String ROOTS_OF_UNITY_LENGTH_NOT_BIG_ENOUGH_EXCEPTION =
            "Roots of unity length is not big enough, must be equal to the length of coefficients";

    public static final String SIZE_OF_COEFFICIENTS_ARRAY_NOT_EQUAL_TO_RING_DEGREE_EXCEPTION =
            "Size of coefficients array %d is not equal to degree %d of ring";

    public static final String INCORRECT_VALUE_FOR_MODULUS_PROVIDED_EXCEPTION =
            "Incorrect value %d for modulus provided. Modulus should be prime number";

    public static final String INCORRECT_VALUE_FOR_ORDER_PROVIDED =
            "Incorrect value %d for order provided. Order should divide modulus - 1  = %d";

    public static final String NO_PRIMITIVE_ROOT_OF_UNITY_FOUND_EXCEPTION =
            "No primitive root of unity mod m = %d";

    public static final String INVALID_VECTOR_LENGTH_EXCEPTION =
            "Invalid length of input vector %d! Length of vector to be reversed must be a power of two.";
}
