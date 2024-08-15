package run.mone.ultraman.statusbar;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/6/27 13:01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PopUpReq implements Serializable {

    public static final int POP_ORIGIN_STATUS_BAR = 0;

    public static final int POP_ORIGIN_LINE_MARK = 1;

    private PsiMethod psiMethod;

    private PsiComment psiComment;

    private Editor editor;

    private int offset;

    @Builder.Default
    private int origin = POP_ORIGIN_STATUS_BAR;

}
