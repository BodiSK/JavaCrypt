package scheme;

/**
 * A class that contains constants used in the bfv package:
 * string values for the error messages, other static values used more than once in different places in the code.
 * The purpose of the class is to provide a better organized code structure in order to easily apply modifications in the code.
 */
public class Constants {
    public static String BATCH_ENCODER_INCORRECT_NUMBER_OF_VALUES_EXCEPTION =
            "In order to perform encoding correctly the values to be encoded must be as many " +
                    "as the degree of the of the ring in which they are to be transformed!";
}
