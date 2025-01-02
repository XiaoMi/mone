package run.mone.hive.schema;

import lombok.Data;

@Data
public class CodingContext {
    private String filename;
    private Document designDoc;
    private Document taskDoc;
    private Document codeDoc;
    private Document codePlanAndChangeDoc;
    private String requirements;
    private Object currentCode;

    private String context;


    public String toJson() {
        // 实现序列化逻辑
        return "{}"; // 简化示例
    }

}