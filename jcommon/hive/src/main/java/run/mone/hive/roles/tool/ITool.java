package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.utils.JsonUtils;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 11:07
 * prompt中就定义的工具
 */
public interface ITool {

    String getName();

    String description();

    String parameters();

    String usage();

    default boolean taskProgress() {
        return true;
    }

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

    //是否由调用方处理后触发下一轮执行
    default boolean callerRunTrigger() {
        return false;
    }

    // 是否在调用工具时将toolInfo作为参数
    default boolean toolInfoAsParam() {
        return false;
    }

    default String formatResult(JsonObject res) {
        return JsonUtils.toolResult(res.toString());
    }

    default String formatResult(String res) {
        return JsonUtils.toolResult(res);
    }

    default JsonObject execute(ReactorRole role, JsonObject req) {
        return new JsonObject();
    }


}
