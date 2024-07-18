package run.mone.ultraman.common;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.util.MarkdownFilter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

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

    private MarkdownFilter markdownFilter = new MarkdownFilter(new Consumer<String>() {
        @Override
        public void accept(String s) {
            CodeService.writeCode2(project, editor, s);
        }
    });

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
            //过滤不必要的markdown标签
            markdownFilter.accept(str);
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
