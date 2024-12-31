/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.tesla.ip.util;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.terminal.JBTerminalPanel;
import com.intellij.terminal.JBTerminalWidget;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.jediterm.terminal.TerminalOutputStream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.plugins.terminal.TerminalView;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/7 11:21
 */
@Slf4j
public class TerminalUtils {

    public static final String TERMINAL_ID = "Terminal";

    @SneakyThrows
    public static void show(Project project) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow terminal = toolWindowManager.getToolWindow(TERMINAL_ID);
        if (terminal != null) {
            terminal.show(null);
            if (!terminal.isActive()) {
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }


    public static void send(Project project, String message) {
        if (null == project) {
            project = ProjectManager.getInstance().getOpenProjects()[0];
        }
        ToolWindow terminal = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
        JComponent root = terminal.getComponent();
        terminal.show(() -> {
            JBTerminalPanel terminalPanel = (JBTerminalPanel) getPannel(root);
            //执行history命令
            TerminalOutputStream is = terminalPanel.getTerminalOutputStream();
            if (null != is) {
                is.sendString(message + "\n");
            }
        });
    }

    public static ToolWindow getTerminalToolWindow(Project project) {
        ToolWindowManagerEx toolWindowManager = ToolWindowManagerEx.getInstanceEx(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Terminal");
        if (toolWindow == null) {
            toolWindow = toolWindowManager.registerToolWindow("Terminal", true, ToolWindowAnchor.BOTTOM);
        }
        toolWindow.show(null);
        return toolWindow;
    }


    public static void runCommandOnTerminal(Project project, String command) {
        GeneralCommandLine cmdLine = new GeneralCommandLine();
        cmdLine.setExePath("/bin/bash"); // 设置 shell 解释器
        cmdLine.addParameter("-c"); // 添加参数
        cmdLine.addParameter(command); // 添加要执行的命令

        OSProcessHandler processHandler = null; // 创建进程处理器
        try {
            processHandler = new OSProcessHandler(cmdLine);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void onTextAvailable(ProcessEvent event, Key outputType) {
                // 处理命令输出
                String output = event.getText();
                System.out.println(output);
            }
        });

        processHandler.startNotify(); // 启动进程处理器

    }


    private static Object getPannel(Object root) {
        if (root instanceof JBTerminalPanel) {
            return root;
        }
        if (root instanceof JPanel) {
            return getPannel(((JPanel) root).getComponent(0));
        }
        if (root instanceof JLayeredPane) {
            return getPannel(((JLayeredPane) root).getComponent(0));
        }
        return null;
    }


    public static JBTerminalWidget getExistingTerminal(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
        if (toolWindow != null) {
            if (!toolWindow.isVisible()) {
                // 获取终端工具窗口的内容管理器
                ContentManager contentManager = toolWindow.getContentManager();
                for (Content content : contentManager.getSelectedContents()) {
                    // 检查内容是否是终端视图
                    if (content.getComponent() instanceof JBTerminalWidget) {
                        // 转换并返回JBTerminalWidget
                        return (JBTerminalWidget) content.getComponent();
                    }
                }
            }
        }
        return null;
    }

    public static void executeTerminalCommand(Project project, String command) {
        ApplicationManager.getApplication().invokeLater(()->{
            TerminalView terminalView = TerminalView.getInstance(project);
            try {
                log.info("command:{}", command);
                terminalView.createLocalShellWidget(project.getBasePath(), "Athena").executeCommand(command);
            } catch (IOException err) {
                err.printStackTrace();
            }
        });
    }


}
