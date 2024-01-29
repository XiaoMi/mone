package run.mone.ultraman.manager;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 * @date 2023/7/21 14:21
 */
public class AthenaTypedActionHandler extends TypedHandlerDelegate {


    @Override
    public @NotNull Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        System.out.println(c);
        return super.charTyped(c, project, editor, file);
    }


}
