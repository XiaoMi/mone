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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import run.mone.m78.ip.common.Safe;
import run.mone.ultraman.common.SafeRun;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/9 17:13
 */
public class UltramanConsole {


    public static void append(String message) {
        append(ProjectUtils.project(), message, true);
    }

    public static void append(String projectName, String messsage) {
        SafeRun.run(()-> append(ProjectUtils.projectFromManager(projectName), messsage));
    }


    public static void append(Project project, String message) {
        append(project, message, true);
    }

    public static void append(Project project, String message, boolean enableAutoWrap) {

    }

    public static void show() {
        Safe.run(() -> {
            ToolWindow toolWindow = ToolWindowManager.getInstance(ProjectUtils.project()).getToolWindow("AthenaConsole");
            toolWindow.show();
        });
    }

}
