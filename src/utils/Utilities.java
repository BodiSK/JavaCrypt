package utils;

import java.lang.reflect.Array;

/**
 * A class that contains utility functions such as array manipulation.
 */
public class Utilities {

    /**
     * Method to combine two arrays into one. The result will be of length equal to the sum of the lengths of both arrays
     * and will contain the first array and the second array appended at the end of it.
     * Uses generic type.
     *
     * @param firstArray the array which will take the first positions of the result array
     * @param secondArray the array which will be appended at the end of the first
     * @throws IllegalArgumentException if any of the input types are null
     */
    public static <T> T appendArrayTo(T firstArray, T secondArray) {
        if (firstArray == null || secondArray == null) {
            throw new IllegalArgumentException("Input arrays cannot be null");
        }

        // Get the class type of the arrays
        Class<?> arrayType = firstArray.getClass();

        // Determine the length of the combined array
        int firstLength = Array.getLength(firstArray);
        int secondLength = Array.getLength(secondArray);
        int combinedLength = firstLength + secondLength;

        // Create a new array of the same type with combined length
        @SuppressWarnings("unchecked")
        T result = (T) Array.newInstance(arrayType.getComponentType(), combinedLength);

        // Copy elements from the first array
        System.arraycopy(firstArray, 0, result, 0, firstLength);

        // Copy elements from the second array, starting from the end of the first array
        System.arraycopy(secondArray, 0, result, firstLength, secondLength);

        return result;
    }
}
