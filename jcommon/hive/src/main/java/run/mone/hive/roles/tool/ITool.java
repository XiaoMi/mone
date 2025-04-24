package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 11:07
 */
public interface ITool {

    String getName();

    String description();

    String parameters();

    String usage();

    default String example() {
        return "";
    }

    //需要执行
    default boolean needExecute() {
        return false;
    }

    //是否是结束工具
    default boolean completed() {
        return false;
    }

    //是否回显
    default boolean show() {
        return false;
    }

    default JsonObject execute(JsonObject req) {
        return new JsonObject();
    }


}
