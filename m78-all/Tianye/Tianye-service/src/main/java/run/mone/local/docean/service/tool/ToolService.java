package run.mone.local.docean.service.tool;

import com.google.gson.JsonElement;

/**
 * @author goodjava@qq.com
 * @date 2024/2/27 13:59
 */
public interface ToolService {

    default JsonElement execute(JsonElement params){
        return null;
    }


    default String version() {
        return "0.0.1";
    }

    default String author() {
        return "tianye";
    }

}
