package run.mone.hive.llm;

import lombok.Getter;

/**
 * @author goodjava@qq.com
 * @date 2025/1/23 13:28
 */
@Getter
public enum MessageType {

    BEGIN("begin"),
    FINISH("finish"),
    FAILURE("failure"),
    EVENT("event")

    ;

    private final String name;

    MessageType(String name) {
        this.name = name;
    }



}
