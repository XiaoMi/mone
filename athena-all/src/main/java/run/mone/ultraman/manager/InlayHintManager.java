package run.mone.ultraman.manager;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.InlayModel;
import lombok.Getter;
import run.mone.ultraman.render.AthenaInlayRenderer;

/**
 * @author goodjava@qq.com
 * @date 2023/7/21 14:07
 */
public class InlayHintManager {

    @Getter
    private Inlay inlay;

    @Getter
    private String hintText;


    private static final class LazyHolder {
        private static final InlayHintManager ins = new InlayHintManager();
    }

    public static final InlayHintManager ins() {
        return LazyHolder.ins;
    }

    public void dispose() {
        if (null != inlay) {
            inlay.dispose();
            this.hintText = null;
        }
    }


    public Inlay addInlayHint(Editor editor, int offset, String hintText) {
        InlayModel inlayModel = editor.getInlayModel();
        this.hintText = hintText;
        inlay = inlayModel.addInlineElement(offset, true, new AthenaInlayRenderer(hintText, editor));
        return inlay;
    }

}
