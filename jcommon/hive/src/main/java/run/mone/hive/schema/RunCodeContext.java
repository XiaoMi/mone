
package run.mone.hive.schema;

import lombok.Data;
import com.fasterxml.jackson.databind.ObjectMapper;

@Data
public class RunCodeContext {
    private String code;
    private String testCode;
    private String executionResult;

    public static RunCodeContext fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, RunCodeContext.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to RunCodeContext", e);
        }
    }
}
