package run.mone.m78.ip.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author caobaoyu
 * @description:
 * @date 2023-05-22 11:02
 */
@Getter
public enum InvokePromptEnums {
    /**
     * 创建类
     */
    CREATE_CLASS(1, "createClass", "创建类"),
    CREATE_METHOD(2, "createMethod", "创建方法"),
    ADD_COMMENT(3, "addComment", "添加注释"),
    CREATE_FILE(4, "createFile", "创建文件"),
    UPDATE_CLASS(5, "updateClass", "修改类"),

    ;
    private int code;
    private String msg;
    private String desc;

    InvokePromptEnums(int code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }

    public static Map<Integer, String> getInvokeMthMap() {
        Map<Integer, String> map = new HashMap<>();
        for (InvokePromptEnums value : InvokePromptEnums.values()) {
            map.put(value.getCode(), value.getDesc());
        }
        return map;
    }

}
