package run.mone.m78.ip.service;

import com.intellij.openapi.editor.Editor;

/**
 * @author goodjava@qq.com
 * @date 2023/5/19 22:08
 */
public class EditorService {


    public static void readOnly(Editor editor, boolean readOnly) {
        editor.getDocument().setReadOnly(readOnly);
    }

}
