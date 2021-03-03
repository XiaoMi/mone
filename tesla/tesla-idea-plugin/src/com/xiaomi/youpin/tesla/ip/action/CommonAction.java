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

package com.xiaomi.youpin.tesla.ip.action;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.xiaomi.youpin.tesla.ip.common.GlobalConfig;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 */
public class CommonAction extends AnAction {

    private static final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("Plugins " + GlobalConfig.PLUGIN_NAME,
            NotificationDisplayType.STICKY_BALLOON, true);


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        String projectName = project.getName();
        String basePath = project.getBasePath();
//        Messages.showMessageDialog(project, projectName + ":" + basePath, "", Messages.getInformationIcon());


        String title = "title";
        String content = "content";

        Notification notification = NOTIFICATION_GROUP.createNotification(title,
                content, NotificationType.INFORMATION, null);
        OpenImageAction openImageAction = new OpenImageAction("gogogo", notification);
        notification.addAction(openImageAction);
        Notifications.Bus.notify(notification);

    }
}
