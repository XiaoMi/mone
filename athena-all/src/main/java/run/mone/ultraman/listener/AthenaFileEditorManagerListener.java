package run.mone.ultraman.listener;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import run.mone.m78.ip.bo.ValueInfo;
import run.mone.m78.ip.common.Const;
import run.mone.m78.ip.common.Safe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.event.AthenaEventBus;
import run.mone.ultraman.service.AutoFlushBizService;

/**
 * @author goodjava@qq.com
 * @date 2023/7/14 11:21
 */
@Slf4j
public class AthenaFileEditorManagerListener implements FileEditorManagerListener {

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        AutoFlushBizService.notifyDocumentClosed(file, source.getProject());
        String data = file.getUserData(Const.T_KEY);
        if (StringUtils.isNotEmpty(data)) {
            Safe.run(() -> {
                if (AthenaEventBus.ins().getListener().getValueInfoConsumer() != null) {
                    String text = FileDocumentManager.getInstance().getDocument(file).getText();
                    log.info(text);
                    ValueInfo pi = new ValueInfo();
                    pi.setValue(text);
                    AthenaEventBus.ins().post(pi);
                }
            });
        }
    }
}
