package run.mone.m78.ip.service;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author goodjava@qq.com
 * @date 2023/6/3 22:31
 */
public class PsiMethodUtils {


    /**
     * 删除指定方法中的注释
     *
     * @param psiMethod
     */
    public static void deleteCommentsFromMethod(Project project, PsiMethod psiMethod, Predicate<PsiComment> predicate) {
        @NotNull Collection<PsiComment> comments = PsiTreeUtil.collectElementsOfType(psiMethod, PsiComment.class);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (PsiComment comment : comments) {
                if (predicate.test(comment)) {
                    comment.delete();
                }
            }
        });
    }

    /**
     * 修改方法名
     *
     * @param project  项目对象
     * @param document 文档对象
     * @param method   方法对象
     * @param newName  新方法名
     */
    public static void modifyMethodName(Project project, Document document, PsiMethod method, String newName) {

    }


}
