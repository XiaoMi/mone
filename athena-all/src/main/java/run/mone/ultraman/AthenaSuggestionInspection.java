package run.mone.ultraman;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.bo.Message;
import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.common.Prompt;
import run.mone.m78.ip.common.PromptType;
import run.mone.m78.ip.service.PromptService;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 * 用来提出建议代码
 */
public class AthenaSuggestionInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final String promptName = "hi2";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitMethod(PsiMethod method) {
                if (true) {
                    return;
                }
                super.visitMethod(method);
                holder.registerProblem(method, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {

                    @Override
                    public @IntentionFamilyName @NotNull String getFamilyName() {
                        return Message.suggestion;
                    }

                    @Override
                    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
                        PromptType promptType = Prompt.getPromptType(promptInfo);
                        PromptService.inlayHint(GenerateCodeReq.builder()
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
