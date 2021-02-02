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


import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Notify {



    /**
     * Shows {@link Notification}.
     *
     * @param project  current project
     * @param title    notification title
     * @param group    notification group
     * @param content  notification text
     * @param type     notification type
     * @param listener optional listener
     */
    public static void show(@NotNull Project project, @NotNull String title, @NotNull String content,
                            @NotNull NotificationGroup group, @NotNull NotificationType type,
                            @Nullable NotificationListener listener) {
        Notification notification = group.createNotification(title, content, type, listener);
        Notifications.Bus.notify(notification, project);
    }


}
