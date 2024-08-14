package run.mone.ultraman.gutter;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.FunctionUtil;
import com.xiaomi.youpin.tesla.ip.service.PsiMethodUtils;
import com.xiaomi.youpin.tesla.ip.util.EditorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.statusbar.AthenaStatusBarWidget;
import run.mone.ultraman.statusbar.PopUpReq;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

import static run.mone.ultraman.statusbar.PopUpReq.POP_ORIGIN_LINE_MARK;

/**
 * @author HawickMason@xiaomi.com
 * @date 6/26/24 2:27 PM
 */
public class AthenaMethodLineMarkerProvider implements LineMarkerProvider {


    private final String icons = "/icons/M2.svg";

    @Override
    public @Nullable LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            if (isMethodNotConstructor(method)) {
                PsiElement firstChild = PsiMethodUtils.getFirstLeafNode(method);
                Icon icon = IconLoader.getIcon(icons, getClass());
                TextRange range = method.getNameIdentifier() != null ? method.getNameIdentifier().getTextRange() : method.getModifierList().getTextRange();
                return new LineMarkerInfo<>(
                        firstChild,
                        method.getNameIdentifier() != null ? method.getNameIdentifier().getTextRange() : method.getModifierList().getTextRange(),
                        icon,
                        FunctionUtil.nullConstant(),
                        ((mouseEvent, pe) -> {
                            PsiMethod psiMethod = PsiTreeUtil.getParentOfType(pe, PsiMethod.class);
                            AthenaStatusBarWidget.popUp(pe.getProject(), null, mouseEvent, PopUpReq.builder().offset(range.getEndOffset()).psiMethod(psiMethod).origin(POP_ORIGIN_LINE_MARK).build());
                        }),
                        GutterIconRenderer.Alignment.LEFT
                );
            }
        }

        if (element instanceof PsiComment) {
            PsiComment psiComment = (PsiComment) element;
            PsiElement firstChild = psiComment;

            if (isValidComment(psiComment)) {
                Icon icon = IconLoader.getIcon(icons, getClass());
                return new LineMarkerInfo<>(
                        firstChild,
                        psiComment.getTextRange(),
                        icon,
                        FunctionUtil.nullConstant(),
                        ((mouseEvent, pc) -> {
                            AthenaStatusBarWidget.popUp(pc.getProject(), null, mouseEvent, PopUpReq.builder().offset(pc.getTextOffset()).psiComment((PsiComment) pc).origin(POP_ORIGIN_LINE_MARK).build());
                        }),
                        GutterIconRenderer.Alignment.LEFT
                );
            }
        }
        return null;
    }

    private static boolean isMethodNotConstructor(PsiMethod method) {
        return !method.isConstructor();
    }

    private static boolean isValidComment(PsiComment psiComment) {
        return EditorUtils.isNotClassHeaderComment(psiComment)
                && EditorUtils.isCommentInsideClass(psiComment)
                && !EditorUtils.isCommentInsideMethod(psiComment);
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
        // No slow line markers to collect
    }


}
