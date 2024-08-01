package run.mone.ultraman.service;

import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.bo.GenerateCodeReq;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.service.PromptService;
import lombok.extern.slf4j.Slf4j;
import run.mone.ultraman.bo.DesensitizeReq;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.http.HttpClient;

/**
 * @author goodjava@qq.com
 * @date 2023/12/10 15:28
 */
@Slf4j
public class AiCodeService {

    public static void generateBizCode(String scope, Project project, String comment) {
        ApplicationManager.getApplication().invokeLater(() -> PromptService.dynamicInvoke(getGenerateCodeReq(project, scope, comment)));
    }

    //计算两数和(method)




    public static GenerateCodeReq getGenerateCodeReq(Project project, String scope, String comment) {
        PromptInfo promptInfo = Prompt.getPromptInfo(Const.GENERATE_CODE);
        return GenerateCodeReq.builder()
                .scope(scope)
                .chatComment(comment)
                .project(project)
                .promptInfo(promptInfo)
                .promptName(promptInfo.getPromptName())
                .promptType(Prompt.getPromptType(promptInfo))
                .projectName(project.getName())
                .build();
    }

    /**
     * Desensitizes a given Java code snippet by sending it to a configured AI proxy service and returns the desensitized version if the service responds with success; otherwise, returns the original snippet.
     */
    public static String desensitizeCode(String codeSnippet) {
        try {
            String url = ConfigUtils.getConfig().getAiProxy() + "/desensitize";
            DesensitizeReq req = DesensitizeReq.builder().text(codeSnippet).langType("java").aiDesensitizeFlag(false).zzToken(ConfigUtils.getConfig().getzToken()).build();
            String rst = HttpClient.post(url, GsonUtils.gson.toJson(req));
            JsonObject res = GsonUtils.gson.fromJson(rst, JsonObject.class);
            int code = res.get("code").getAsInt();
            if (code == 0) {
                return res.get("data").getAsString();
            }
            return codeSnippet;
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            return codeSnippet;
        }
    }

}
