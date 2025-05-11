package run.mone.hive.common;

import lombok.Data;
import run.mone.hive.roles.ReactorRole;

import java.util.Map;

@Data
public class ToolDataInfo {

    private final String tag;

    private final Map<String, String> keyValuePairs;

    private String from;

    private String userId;

    private String agentId;

    private ReactorRole role;

    public ToolDataInfo(String tag, Map<String, String> keyValuePairs) {
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