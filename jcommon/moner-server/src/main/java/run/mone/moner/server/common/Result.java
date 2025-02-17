package run.mone.moner.server.common;

import java.util.Map;

public class Result {
    private final String tag;
    private final Map<String, String> keyValuePairs;

    public Result(String tag, Map<String, String> keyValuePairs) {
        this.tag = tag;
        this.keyValuePairs = keyValuePairs;
    }

    public String getTag() {
        return tag;
    }

    public Map<String, String> getKeyValuePairs() {
        return keyValuePairs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        keyValuePairs.forEach((key, value) -> 
            sb.append(key).append("=").append(value).append(", ")
        );
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2); // 移除最后的 ", "
        }
        sb.append("}");
        return sb.toString();
    }
}