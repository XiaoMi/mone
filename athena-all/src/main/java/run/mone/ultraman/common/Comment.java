package run.mone.ultraman.common;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/5/26 16:47
 */
@Data
public class Comment {

    private Document document;

    private int offset;

    private Project project;


    public void append(String str) {
        if (isNum(str)) {
            //move
            int lineNum = Integer.parseInt(str.trim());
            offset = document.getLineEndOffset(lineNum - 1);
        } else if (isEnterOrSpace(str)) {
            //忽略
        } else {
            //输入
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.insertString(offset, str);
                offset += str.length();
            });
        }


    }


    private boolean isNum(String str) {
        str = str.trim();
        try {
            Integer.parseInt(str);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    private boolean isEnterOrSpace(String str) {
        return str.trim().equals("\n") || "".equals(str.trim());
    }

}
