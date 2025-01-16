package run.mone.m78.service.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Collection;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/16/24 16:04
 */
public class M78StringUtils {

    private M78StringUtils() {
        // default constructor, do nothing
    }

    public static double getStrEditDistanceSimilarity(String str1, String str2) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int distance = levenshteinDistance.apply(str1, str2);
        return 1 - (double) distance / Math.max(str1.length(), str2.length());
    }

    /**
     * Removes the specified characters from the start and end of the given string.
     * @param str string that needs to be processed
     * @param stripChars stripChars
     * @return stripped string
     */
    public static String stripStr(String str, String stripChars) {
        String headStripped = StringUtils.stripStart(str, stripChars);
        return StringUtils.stripEnd(headStripped, stripChars);
    }

    public static String collection2DelimitedStr(Collection<?> coll, String delim) {
        return org.springframework.util.StringUtils.collectionToDelimitedString(coll, delim);
    }
}
