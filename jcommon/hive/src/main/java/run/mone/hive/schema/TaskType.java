
package run.mone.hive.schema;

import lombok.Getter;

@Getter
public enum TaskType {
    CODE_REVIEW("代码Review"),
    CODE_TESTING("代码测试"),
    CODE_WRITING("代码编写");

    private final String description;

    TaskType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
