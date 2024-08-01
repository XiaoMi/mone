package com.xiaomi.youpin.tesla.ip.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

/**
 * @author goodjava@qq.com
 * @date 2023/4/18 00:08
 */
public class WindowUtils {


    public static void actionPerformed(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Project");
        toolWindow.hide();
    }


}
