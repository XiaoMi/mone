package run.mone.ultraman.common;

import com.intellij.execution.PsiLocation;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.junit.JUnitConfigurationType;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import run.mone.ultraman.common.adapter.TestRunnerProcessAdapter;

import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2024/6/29 09:03
 */
public class TestRunnerUtils {

    /**
     * 运行测试的方法
     * 从数据管理器获取数据上下文，得到相关类，获取类名
     * 尝试根据类名获取测试类并获取其方法
     * 对有测试注解的方法进行 JUnit 测试并根据结果显示相应消息框
     * 处理类未找到的异常情况
     */
    @SneakyThrows
    public static void runTest(Project project, String methodName, Consumer<Pair<String, String>> consumer) {
        PsiClass psiClass = CodeService.getPsiClass(project);
        String className = psiClass.getQualifiedName();

        PsiMethod[] methods = psiClass.getMethods();

        // 逐个执行测试方法
        for (PsiMethod method : methods) {
            if (hasTestAnnotation(method, methodName)) {
                RunnerAndConfigurationSettings runSettings = RunManager.getInstance(project).createConfiguration(className + "." + method.getName(), JUnitConfigurationType.getInstance().getConfigurationFactories()[0]);
                JUnitConfiguration configuration = (JUnitConfiguration) runSettings.getConfiguration();

                // 设置测试类和方法
                JUnitConfiguration.Data data = configuration.getPersistentData();
                data.setMainClass(psiClass);
                //这个很关键,保证是值测试一个method,而不是class
                data.setTestMethod(PsiLocation.fromPsiElement(method));
                data.setTestMethodName(method.getName());

                ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder
                        .createOrNull(DefaultRunExecutor.getRunExecutorInstance(), runSettings);

                ExecutionEnvironment environment = builder.build();

                ProgramRunner<?> runner = ProgramRunner.getRunner(DefaultRunExecutor.EXECUTOR_ID, environment.getRunProfile());

                //直接执行,并且捕获异常,然后问ai修复
                runner.execute(environment, descriptor -> descriptor.getProcessHandler().addProcessListener(new TestRunnerProcessAdapter(project, consumer)));

            }
        }
    }

    private static boolean hasTestAnnotation(PsiMethod psiMethod, String methodName) {
        if (!psiMethod.getName().equals(methodName)) {
            return false;
        }
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (annotation.getQualifiedName() != null && annotation.getQualifiedName().contains("Test")) {
                return true;
            }
        }
        return false;
    }

}
