package run.mone.ultraman.background;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.bo.ModelRes;
import run.mone.m78.ip.bo.PromptContext;
import run.mone.m78.ip.bo.ZAddrRes;
import run.mone.m78.ip.bo.robot.AiChatMessage;
import run.mone.m78.ip.bo.robot.ProjectAiMessageManager;
import run.mone.m78.ip.bo.robot.Role;
import run.mone.m78.ip.common.Prompt;
import run.mone.m78.ip.service.CodeService;
import run.mone.m78.ip.util.UltramanConsole;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author goodjava@qq.com
 * @date 2023/6/23 08:10
 * 底边栏会显示进度,避免用户不知道当前的ai执行状态
 * 以后再chat 和 editor的都会再这里处理
 */
@Slf4j
public class AthenaTask extends Task.Backgroundable {

    /**
     * 是否是确定的(有时间进度)
     */
    private boolean indeterminate = false;

    private Project project;

    private String promptName;

    private String text;

    private Map<String, String> params;

    @Setter
    private boolean format = true;

    @Setter
    private PromptContext promptContext;

    private static final String METHOD_CONTEXT = "method_context";

    private static final String MODULE_CONTEXT = "module_context";

    private static final String RESOURCE_CONTEXT = "resource_context";

    private static final String CONTEXT = "context";

    //chat(聊天窗口) editor(ide中)
    @Setter
    private String type = "chat";

    @Setter
    private Runnable initRunnable;


    public AthenaTask(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title, String promptName, String text, Map<String, String> params) {
        super(project, title);
        this.project = project;
        this.promptName = promptName;
        this.text = text;
        this.params = params;
    }

    @SneakyThrows
    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        if (null != this.initRunnable) {
            initRunnable.run();
        }

        String scope = "";
        if (null != this.promptContext) {
            callResourceContext();
            callMethodContext();
            callModuleContext();
            scope = this.promptContext.getScope();
        }

        callContextSize();

        ZAddrRes zAddrRes = Prompt.zAddrRes();
        if (CollectionUtils.isNotEmpty(zAddrRes.getModels())) {
            Long userCredits = 0L;
            // Hint: the first model carries the user credit info
            ModelRes modelRes = zAddrRes.getModels().get(0);
            if (modelRes != null) {
                userCredits = modelRes.getPoints();
            }
            String progressIndicatorText = "Athena Running, User Credits: " + userCredits;
            progressIndicator.setText(progressIndicatorText);
            progressIndicator.setText2(progressIndicatorText);
        }

        progressIndicator.setIndeterminate(this.indeterminate);
        progressIndicator.setFraction(0.0);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        UltramanConsole.append(project, "\ncall prompt begin:" + promptName + " scope:" + scope);
        AiCode aiCode = getAiCode(progressIndicator, countDownLatch);
        Stopwatch sw = Stopwatch.createStarted();
        //一点一点的生成代码
        CodeService.generateCodeWithAi5(GenerateCodeReq.builder().format(format).promptName(promptName).project(project).build(), project, promptName, new String[]{text}, params, (p, code) -> {
        }, aiCode);
        //最多等1分钟
        countDownLatch.await(3, TimeUnit.MINUTES);
        String text = aiCode.getMarkDownText();
        UltramanConsole.append(project, "call prompt finish:" + promptName + " use time:" + sw.elapsed(TimeUnit.SECONDS) + "s\n");
        ProjectAiMessageManager.getInstance().appendMsg(project, AiChatMessage.builder().id(aiCode.getMessageId()).role(Role.assistant).message(text).data(text).build());
    }


    /**
     * 无论如何都会在chat里显示信息
     *
     * @param progressIndicator
     * @param countDownLatch
     * @return
     */
    @NotNull
    private AiCode getAiCode(@NotNull ProgressIndicator progressIndicator, CountDownLatch countDownLatch) {
        return null;
    }

    //计算请求的size,有写大小需要优化(最终大小需要低于3500)
    private void callContextSize() {

    }


    private static Function<String, String> f = str -> {
        List<String> l = Splitter.on(".").splitToList(str);
        if (l.size() > 0) {
            return l.get(l.size() - 1);
        }
        return str;
    };

    private void callModuleContext() {

    }

    //引入一些class内部的方法(能起到一定的教学作用)
    private void callMethodContext() {
    }

    private void callResourceContext() {

    }


    //启动任务
    public static void start(Task task) {
        ProgressManager.getInstance().run(task);
    }
}
