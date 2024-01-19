package run.mone.ultraman.quickfix;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 * @date 2023/7/9 07:32
 */
public class AthenaInspection extends LocalInspectionTool {

    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new MyVisitor(holder);
    }

    private static class MyVisitor extends JavaElementVisitor {

        private final ProblemsHolder holder;

        public MyVisitor(ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);

            // 检查方法名是否以大写字母开头
            if (Character.isUpperCase(method.getName().charAt(0))) {
                holder.registerProblem(method.getNameIdentifier(), "Method name should start with a lowercase letter", ProblemHighlightType.WEAK_WARNING);
            }
        }
    }

}
