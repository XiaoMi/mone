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

package com.xiaomi.youpin.tesla.ip.common;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.util.LabelUtils;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/1 12:20
 */
public class NotificationCenter {


    public static void notice(String message) {
        notice(message, NotificationType.INFORMATION);
    }


    public static void notice(String message, NotificationType type) {
        Notification n = new Notification(
                "Athena",
                "Athena",
                message,
                type);
        Notifications.Bus.notify(n);
    }


    /**
     * 只有debug模式才输出
     *
     * @param message
     * @param type
     * @param debug
     */
    public static void notice(String message, NotificationType type, boolean debug) {
        if (debug && LabelUtils.getLabelValue(null, "debug", "true").equals("true")) {
            notice(message, type);
        }
    }

    public static void notice(final Project project, String message, boolean debug) {
        if (debug) {
            if (LabelUtils.getLabelValue(null, "debug", "true").equals("true")) {
                Notification notification = new Notification("Athena Notifications", "Athena", message, NotificationType.INFORMATION);
                Notifications.Bus.notify(notification, project);
            }
        } else {
            Notification notification = new Notification("Athena Notifications", "Athena", message, NotificationType.INFORMATION);
            Notifications.Bus.notify(notification, project);
        }
    }

}
