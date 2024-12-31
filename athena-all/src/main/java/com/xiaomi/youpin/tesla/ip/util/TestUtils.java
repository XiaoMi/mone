package com.xiaomi.youpin.tesla.ip.util;

import com.google.common.collect.Lists;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.junit.JUnitConfigurationType;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/8/11 09:46
 */
public class TestUtils {


    public static List<String> junitImportList = Lists.newArrayList("import org.junit.Test", "import org.junit.Assert");

    public static List<String> jupiterImportList = Lists.newArrayList("import org.junit.jupiter.api.Test", "import static org.junit.jupiter.api.Assertions.assertEquals", "import static org.junit.jupiter.api.Assertions.assertNotNull");


    public static boolean isJupiter() {
        if (isClassPresent("org.junit.Test")) {
            return false;
        } else if (isClassPresent("org.junit.jupiter.api.Test")) {
            return true;
        } else {
            return false;
        }
    }

    public static List<String> getImportList(String version) {
        if (version.equals("unknow")) {
            return Lists.newArrayList();
        }
        if (version.equals("junit")) {
            return junitImportList;
        }
        if (version.equals("jupiter")) {
            return junitImportList;
        }
        return Lists.newArrayList();
    }


    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    public static void runAllTestsInClass(Project project, PsiClass psiClass) {
        // 创建 JUnit 测试配置
        JUnitConfiguration configuration = new JUnitConfiguration("Run all tests in " + psiClass.getName(), project);
        // 设置要运行的测试类
        configuration.setMainClass(psiClass);
        // 创建 RunnerAndConfigurationSettings 对象
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = RunManager.getInstance(project).createConfiguration(configuration, getJUnitConfigurationFactory());
        // 运行测试
        ExecutionUtil.runConfiguration(runnerAndConfigurationSettings, new DefaultDebugExecutor());
    }



    private static ConfigurationFactory getJUnitConfigurationFactory() {
        return JUnitConfigurationType.getInstance().getConfigurationFactories()[0];
    }


}
