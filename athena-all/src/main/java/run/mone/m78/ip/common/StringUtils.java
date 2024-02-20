package run.mone.m78.ip.common;

/**
 * @author goodjava@qq.com
 * @date 2023/5/18 22:26
 */
public abstract class StringUtils {

    public static String convertToCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        String[] words = input.split("_");
        result.append(words[0]);
        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            String capitalizedWord = Character.toUpperCase(word.charAt(0)) + word.substring(1);
            result.append(capitalizedWord);
        }
        return result.toString();
    }

}
