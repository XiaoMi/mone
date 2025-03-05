package run.mone.mcp.idea.composer.handler.xml;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 11/25/24 4:00 PM
 */
public enum CustomStreamTag {

    UNKNOWN(0, "unknown"),
    ACTION(1, "boltAction"),
    ARTIFACT(2, "boltArtifact");

    private final int code;
    private final String tagName;

    private static final Map<Integer, CustomStreamTag> valMap = Arrays.stream(values()).collect(Collectors.toMap(CustomStreamTag::getCode, Function.identity()));

    private static final Map<String, CustomStreamTag> nameMap = Arrays.stream(values()).collect(Collectors.toMap(CustomStreamTag::getTagName, Function.identity()));

    CustomStreamTag(int code, String tagName) {
        this.code = code;
        this.tagName = tagName;
    }

    public int getCode() {
        return code;
    }

    public String getTagName() {
        return tagName;
    }

    public static CustomStreamTag getTagByName(String tagName) {
        return nameMap.getOrDefault(tagName, UNKNOWN);
    }

    public static boolean isValidTagName(String tagName) {
        return !UNKNOWN.getTagName().equals(tagName) && nameMap.containsKey(tagName);
    }
}
