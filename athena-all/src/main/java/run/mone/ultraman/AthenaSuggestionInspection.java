package run.mone.ultraman;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.xiaomi.youpin.tesla.ip.bo.GenerateCodeReq;
import com.xiaomi.youpin.tesla.ip.bo.Message;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.common.PromptType;
import com.xiaomi.youpin.tesla.ip.service.PromptService;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 * 用来提出建议代码
 */
public class AthenaSuggestionInspection extends AbstractBaseJavaLocalInspectionTool {

    //代码修改建议
    private static final String promptName = "hi2";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);

                if (ResourceUtils.checkDisableCodeCompletionStatus()) return;

                holder.registerProblem(method, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {

                    @Override
                    public @IntentionFamilyName @NotNull String getFamilyName() {
                        return Message.suggestion;
                    }

                    @Override
                    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
                        PromptType promptType = Prompt.getPromptType(promptInfo);
                        PromptService.dynamicInvoke(GenerateCodeReq.builder()
                                        .promptName(promptName)
                                        .promptInfo(promptInfo)
                                        .promptType(promptType)
                                        .project(project)
                                        .projectName(project.getName()).build());
                    }
                });
            }

        };
    }


}
