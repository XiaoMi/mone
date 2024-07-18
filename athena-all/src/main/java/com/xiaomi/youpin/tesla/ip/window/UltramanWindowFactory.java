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

package com.xiaomi.youpin.tesla.ip.window;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.jcef.JBCefBrowser;
import com.xiaomi.youpin.tesla.ip.common.NotificationCenter;
import com.xiaomi.youpin.tesla.ip.listener.UltrmanTreeKeyAdapter;
import com.xiaomi.youpin.tesla.ip.ui.UltramanTreeUi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/8 20:55
 */
public class UltramanWindowFactory implements ToolWindowFactory, DumbAware {

    private static final String nginxChatUrl = "http://127.0.0.1/ultraman/#/code";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.getInstance();
        JPanel pannel = new UltramanTreeUi(project).jpanel();
        Content content = contentFactory.createContent(pannel, "", false);
        toolWindow.getContentManager().addContent(content);
        if (toolWindow instanceof ToolWindowEx toolWindowEx) {
            toolWindowEx.setAdditionalGearActions(new AthenaWindowCustomActionGroup());
        }
    }

    private static class AthenaWindowCustomActionGroup extends ActionGroup {
        @Override
        public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
            List<AnAction> actions = new ArrayList<>();
            actions.add(new AthenaWindowCustomRefreshAction());
            return actions.toArray(new AnAction[0]);
        }
    }

    private static class AthenaWindowCustomRefreshAction extends AnAction {

        public AthenaWindowCustomRefreshAction() {
            super("Force Refresh");
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            // 自定义选项的操作
            NotificationCenter.notice(null, "custom refresh action performed!", true);
            Project project = e.getProject();
            if (project != null) {
                JBCefBrowser jbCefBrowser = UltrmanTreeKeyAdapter.browserMap.get(project.getName());
                UltrmanTreeKeyAdapter.forceRefresh(jbCefBrowser, nginxChatUrl);
            }
        }
    }
}
