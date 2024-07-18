package run.mone.ultraman.common.adapter;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiMethod;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2024/6/30 10:47
 */
@Slf4j
public class TestRunnerProcessAdapter extends ProcessAdapter {

    private List<String> list = new ArrayList<>();

    private Project project;

    private Consumer<Pair<String, String>> consumer;

    public TestRunnerProcessAdapter(Project project, Consumer<Pair<String, String>> consumer) {
        this.project = project;
        this.consumer = consumer;
    }

    @Override
    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        list.add(event.getText());

    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        if (event.getExitCode() == 0) {
            log.info("Process finished successfully.");
        } else {
            log.info("Process finished with errors.");
            //发生错误了
            String error = list.stream().filter(it -> it.startsWith("##teamcity[testFailed")).findAny().orElse("");
            //获取当前PsiMethod的方法信息
            String methodStr = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> {
                PsiMethod method = CodeService.getMethod(project);
                if (!Objects.isNull(method)) {
                    String methodCode = CodeService.getMethodAndLineNumbers(method);
                    return methodCode;
                }
                return "";
            });
            log.info("method:{} \n error:{}", methodStr, error);
            consumer.accept(Pair.of(methodStr, error));
        }
    }
}
