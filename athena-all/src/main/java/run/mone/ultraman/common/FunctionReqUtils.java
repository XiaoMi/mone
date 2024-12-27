package run.mone.ultraman.common;

import com.google.common.collect.Maps;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.util.GitUtils;
import run.mone.ultraman.AthenaContext;

import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/12/18 20:31
 */
public class FunctionReqUtils {


    public static Map<String, Object> getCallScriptMap(String projectName, Map<String, Object> memary, Map<String, String> promptLables) {
        Map<String, Object> map = Maps.newHashMap();
        map.putAll(memary);
        Project project = AthenaContext.ins().getProjectMap().get(projectName);
        map.put("project", project);
        map.put("_project", projectName);
        map.put("projectName", projectName);
        map.put("user", ConfigUtils.user());
        map.put("token", AthenaContext.ins().getToken());
        map.put("prompt_labels", promptLables);
        map.put("memary", memary);
        //这个项目的git地址
        map.put("gitUrl", GitUtils.getGitAddress(project));
        //这个项目最后一次提交的commitId
        List<String> commitList = GitUtils.getLastCommit(project);
        String lastCommitId = "";
        if (commitList.size() > 0) {
            lastCommitId = commitList.get(0);
            lastCommitId = lastCommitId.substring(1, lastCommitId.length() - 1);
        }
        map.put("lastCommitId", lastCommitId);
        return map;
    }


}
