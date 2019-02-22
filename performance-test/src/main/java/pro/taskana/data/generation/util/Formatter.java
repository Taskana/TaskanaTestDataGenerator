package pro.taskana.data.generation.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class for string formatting.
 * 
 * @author fe
 *
 */
public class Formatter {

    public static final char PADDING_CHARACTER = '0';
    private static final char SUFFIX_CHAR = 'X';
    
    /**
     * Converts a number to a {@link String} with a specified length by adding
     * leading zeros.
     * 
     * @param num
     *            number which should be converted to a string
     * @param requiredLength
     *            length of the returned string
     * @return formatted string
     */
    public static String format(int num, int requiredLength) {

        String formattedText = Integer.toString(num);
        if (formattedText.length() > requiredLength) {
            throw new IllegalArgumentException(
                    "The number " + num + " is longer than the required length: " + requiredLength + "!");
        }
        while (formattedText.length() < requiredLength) {
            formattedText = PADDING_CHARACTER + formattedText;
        }
        return formattedText;
    }

    /**
     * Expands or cut the string to fit the expected length.
     * 
     * @param string
     *            word to be expand
     * @param expectedLength
     *            number of characters in result
     * @param fillingChar
     *            character to fill the string with
     * @return resulting string with expected length
     */
    public static String fitToExpectedLength(String string, int expectedLength) {
        return fitToExpectedLength(string, expectedLength, SUFFIX_CHAR);
    }

    /**
     * Expands or cut the string to fit the expected length.
     * 
     * @param string
     *            word to be expand
     * @param expectedLength
     *            number of characters in result
     * @param fillingChar
     *            character to fill the string with
     * @return resulting string with expected length
     */
    public static String fitToExpectedLength(String string, int expectedLength, char filling) {
        int currentLength = 0;
        if(string != null) {
            currentLength = string.length();
        }
        
        int difference = expectedLength - currentLength;
        if (difference < 0) {
            return string.substring(0, expectedLength - 1);
        }

        String suffix = Stream.generate(() -> String.valueOf(filling)).limit(difference)
                .collect(Collectors.joining());

        return string + suffix;
    }

}
