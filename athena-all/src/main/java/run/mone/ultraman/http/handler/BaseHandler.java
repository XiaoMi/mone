package run.mone.ultraman.http.handler;

import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.common.GsonUtils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2024/4/28 15:45
 */
public class BaseHandler {

    public static String getProjectName(JsonObject obj) {
        String projectName = GsonUtils.get(obj, "projectName", () -> {
            ConcurrentHashMap<String, Project> map = AthenaContext.ins().getProjectMap();
            if (map.size() >= 1) {
                return new ArrayList<>(map.values()).get(0).getName();
            }
            return "";
        });
        return projectName;
    }


}
