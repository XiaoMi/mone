package run.mone.m78.ip.util;

import com.intellij.openapi.editor.Editor;

/**
 * @author goodjava@qq.com
 * @date 2023/6/23 09:07
 */
public abstract class LockUtils {

    public static void lockDocument(Editor editor) {
        editor.getDocument().setReadOnly(true);
    }


}
