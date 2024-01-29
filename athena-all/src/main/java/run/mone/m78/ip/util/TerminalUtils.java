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

package run.mone.m78.ip.util;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.terminal.JBTerminalPanel;
import com.jediterm.terminal.TerminalOutputStream;
import lombok.SneakyThrows;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/7 11:21
 */
public class TerminalUtils {

    @SneakyThrows
    public static void show(Project project) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow terminal = toolWindowManager.getToolWindow("Terminal");
        if (terminal != null) {
            terminal.show(null);
            if (!terminal.isActive()) {
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }


    public static void send(Project project, String message) {

    }

    public static ToolWindow getTerminalToolWindow(Project project) {
        return null;
    }


    public static void runCommandOnTerminal(Project project, String command) {


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


}
