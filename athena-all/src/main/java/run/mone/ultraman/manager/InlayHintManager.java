package run.mone.ultraman.manager;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.InlayModel;
import org.apache.commons.lang3.StringUtils;
import run.mone.ultraman.render.AthenaInlayRenderer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2023/7/21 14:07
 */
public class InlayHintManager {

    private Inlay inlay;

    private String hintText;

    private AtomicInteger caretOffsetWhenTriggered = new AtomicInteger();


    public synchronized Inlay getInlay() {
        return inlay;
    }


    public synchronized void setInlay(Inlay inlay, String hintText) {
        this.inlay = inlay;
        this.hintText = hintText;
    }

    public void removeBlockInlay() {
        if (inlay != null) {
            inlay.dispose();
        }
    }

    public synchronized boolean hasInlay() {
        return null != this.inlay;
    }

    public int getCaretOffsetWhenTriggered() {
        return caretOffsetWhenTriggered.get();
    }

    public void setCaretOffsetWhenTriggered(int offset) {
        this.caretOffsetWhenTriggered.set(offset);
    }

    private static final class LazyHolder {
        private static final InlayHintManager ins = new InlayHintManager();
    }

    public static final InlayHintManager ins() {
        return LazyHolder.ins;
    }

    public synchronized void dispose() {
        if (null != inlay) {
            inlay.dispose();
            inlay = null;
            this.hintText = null;
        }
    }


    public synchronized void dispose(Consumer<String> consumer) {
        if (null != inlay) {
            if (StringUtils.isEmpty(this.hintText)) {
                this.hintText = "null";
            }
            consumer.accept(this.hintText);
            inlay.dispose();
            inlay = null;
            this.hintText = null;
        } else {
            consumer.accept("");
        }
    }



    public Inlay addInlayHint(Editor editor, int offset, String hintText) {
        InlayModel inlayModel = editor.getInlayModel();
        this.hintText = hintText;
        inlay = inlayModel.addInlineElement(offset, true, new AthenaInlayRenderer(hintText, editor));
        return inlay;
    }

}
