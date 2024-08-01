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

    //    @Override
//    public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
//        if (c == '\u001B') {  // ESC 键
//            // 处理取消操作
////            Inlay inlay = InlayHintManager.addInlayHint(editor,"abc");  // 获取你的 Inlay 对象
////            inlay.dispose();  // 移除 Inlay
//            System.out.println("-->Esc");
//        } else if (c == '\n') {  // ENTER 键
//            // 处理确认操作
////            String hintText = ;  // 获取你的预输入内容
////            int offset = editor.getCaretModel().getOffset();  // 获取当前的插入点位置
////            editor.getDocument().insertString(offset, hintText);  // 插入预输入内容
//            System.out.println("-->Enter");
//        } else {
//            // 如果按下的不是 ESC 或 ENTER 键，就按照默认的行为处理
//            originalHandler.execute(editor, c, dataContext);
//        }
//    }

}
