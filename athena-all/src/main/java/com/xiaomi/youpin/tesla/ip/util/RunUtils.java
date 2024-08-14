package com.xiaomi.youpin.tesla.ip.util;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

/**
 * @author goodjava@qq.com
 * @date 2024/4/27 22:17
 */
public class RunUtils {

    public static void runMainMethod(Project project, PsiClass psiClass) {
        // 检查是否存在 main 方法
        if (psiClass.findMethodsByName("main", false).length > 0) {
            // 创建应用程序运行配置
            ApplicationConfiguration configuration = new ApplicationConfiguration("Run Main", project);
            // 设置要运行的主类
            configuration.setMainClassName(psiClass.getQualifiedName());

            // 创建执行环境
            Executor executor = DefaultRunExecutor.getRunExecutorInstance();
            RunnerAndConfigurationSettings runnerAndConfigurationSettings = RunManager.getInstance(project).createConfiguration(configuration,  ApplicationConfigurationType.getInstance().getConfigurationFactories()[0]);
            ExecutionEnvironment environment = new ExecutionEnvironment(executor, ProgramRunner.getRunner(executor.getId(), configuration), runnerAndConfigurationSettings, project);


//            ExecutionUtil.runConfiguration(runnerAndConfigurationSettings, new DefaultDebugExecutor());

            try {
                ProgramRunner<?> runner = ProgramRunner.getRunner(executor.getId(), configuration);
                if (runner != null) {
                    runner.execute(environment);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No main method found in class " + psiClass.getName());
        }
    }



}
