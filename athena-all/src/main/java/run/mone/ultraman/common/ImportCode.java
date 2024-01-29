package run.mone.ultraman.common;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import run.mone.m78.ip.service.CodeService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * <p>
 * 带import的code
 */
@Data
@Slf4j
public class ImportCode {

    private Project project;

    private Editor editor;

    private StringBuilder importBuilder = new StringBuilder();

    private boolean isImport = false;

    public void append(String str) {
        if (isImportBegin(str)) {
            isImport = true;
        } else if (isImportEnd(str)) {
            isImport = false;
            log.info(importBuilder.toString());
            CodeService.addImport(this.project, this.editor, CodeUtils.getImportList2(importBuilder.toString()));
        } else if (isImport) {
            importBuilder.append(str);
        } else {
            CodeService.writeCode2(project, editor, str);
        }
    }

    private boolean isImportEnd(String str) {
        return str.trim().equals("☽");
    }

    private boolean isImportBegin(String str) {
        str = str.trim();
        return str.equals("☾");
    }

}
