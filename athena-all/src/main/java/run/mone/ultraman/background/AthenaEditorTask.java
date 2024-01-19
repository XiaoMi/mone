package run.mone.ultraman.background;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.bo.MessageConsumer;
import run.mone.m78.ip.bo.PromptContext;
import run.mone.m78.ip.service.CodeService;
import run.mone.m78.ip.service.PromptService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class AthenaEditorTask extends Task.Backgroundable {

    /**
     * 是否是确定的(有时间进度)
     */
    private boolean indeterminate = false;

    private Project project;

    private String promptName;

    private String text;

    private Map<String, String> params;

    private MessageConsumer consumer;

    private GenerateCodeReq req;


    public AthenaEditorTask(GenerateCodeReq req, @NlsContexts.ProgressTitle @NotNull String title, Map<String, String> params, MessageConsumer consumer) {
        this(req, title, null, params, consumer);
    }

    public AthenaEditorTask(GenerateCodeReq req, @NlsContexts.ProgressTitle @NotNull String title, String code, Map<String, String> params, MessageConsumer consumer) {
        super(req.getProject(), title);
        this.project = req.getProject();
        this.promptName = req.getPromptName();
        if (StringUtils.isEmpty(code)) {
            this.text = PromptService.getCode(req, new PromptContext());
        } else {
            this.text = code;
        }
        this.params = params;
        this.consumer = consumer;
        this.req = req;
    }


    @SneakyThrows
    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        progressIndicator.setIndeterminate(this.indeterminate);
        progressIndicator.setFraction(0.0);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CodeService.generateCodeWithAi5(this.req, project, promptName, new String[]{text}, params, (p, code) -> {
        }, new EditorAiCode(progressIndicator, countDownLatch, consumer));
        //最多等30秒
        countDownLatch.await(30, TimeUnit.SECONDS);
    }


    public static void start(AthenaEditorTask task) {
        ProgressManager.getInstance().run(task);
    }

}
