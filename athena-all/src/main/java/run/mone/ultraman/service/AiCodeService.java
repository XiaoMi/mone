package run.mone.ultraman.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.common.Prompt;
import run.mone.m78.ip.service.PromptService;

/**
 * @author goodjava@qq.com
 * @date 2023/12/10 15:28
 */
public class AiCodeService {

    public static void generateBizCode(String scope, Project project, String comment) {
        ApplicationManager.getApplication().invokeLater(() -> PromptService.dynamicInvoke(getGenerateCodeReq(project, scope, comment)));
    }


    public static GenerateCodeReq getGenerateCodeReq(Project project, String scope, String comment) {
        PromptInfo promptInfo = Prompt.getPromptInfo("biz_sidecar");
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


}
