package run.mone.ultraman.quickfix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 * @date 2024/4/21 12:41
 */
public class MethoNameLocalQuickFix implements LocalQuickFix {
    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Method Name Inspection";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement element = descriptor.getPsiElement();
        if (element instanceof PsiIdentifier) {
            PsiMethod method = (PsiMethod)element.getParent();
            String methodName = method.getName();
            String newMethodName = methodName.replaceAll("\\d", "");
            method.setName(newMethodName);
        }
    }
}
