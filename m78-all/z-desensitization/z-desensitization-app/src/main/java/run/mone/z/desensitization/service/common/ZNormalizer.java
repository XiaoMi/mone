package run.mone.z.desensitization.service.common;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/31/24 2:32 PM
 */
public class ZNormalizer {

    public static String normailize(String word) {
        // remove all nonsense char
        removeNonLetters(word);
        // to lower case
        return word.toLowerCase();
    }

    public static String removeNonLetters(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }
}
