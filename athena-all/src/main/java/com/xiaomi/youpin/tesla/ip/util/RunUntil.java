package com.xiaomi.youpin.tesla.ip.util;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.project.Project;

/**
 * @author goodjava@qq.com
 * @date 2023/4/17 23:50
 */
public class RunUntil {


    public static void run(Project project) {
        RunnerAndConfigurationSettings runConfig = RunManager.getInstance(project).getSelectedConfiguration();
        ExecutionUtil.runConfiguration(runConfig, new DefaultDebugExecutor());
    }

    public static void run2(Project project) {
        RunnerAndConfigurationSettings runConfig = RunManager.getInstance(project).getSelectedConfiguration();
        ExecutionUtil.runConfiguration(runConfig, new DefaultDebugExecutor());
    }


}
