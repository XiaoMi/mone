package run.mone.ultraman.quickfix;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 * @date 2024/4/21 11:58
 */
public class M78Inspection extends LocalInspectionTool {


    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new MyVisitor(holder);
    }


    private static class MyVisitor extends JavaElementVisitor {

        private String packageStr = "";

        private String className = "";

        private final ProblemsHolder holder;

        public MyVisitor(ProblemsHolder holder) {
            this.holder = holder;
        }

    }



    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getDisplayName() {
        return "MyInspection";
    }

    @Override
    public @NonNls @NotNull String getShortName() {
        return "MyInspection";
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getGroupDisplayName() {
        return "MyInspection";
    }
}
