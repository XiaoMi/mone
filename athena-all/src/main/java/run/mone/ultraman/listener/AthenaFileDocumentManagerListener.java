package run.mone.ultraman.listener;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 * @date 2023/7/14 10:25
 *
 * 保存document的时候,会回调回来,以后做统计用
 *
 */
@Slf4j
public class AthenaFileDocumentManagerListener implements FileDocumentManagerListener {

    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        log.debug(document.getText());
    }
}
