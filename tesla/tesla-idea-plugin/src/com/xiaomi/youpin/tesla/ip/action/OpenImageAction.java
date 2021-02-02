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

import com.intellij.ide.DataManager;
import com.intellij.notification.Notification;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.Consumer;
import com.xiaomi.youpin.tesla.ip.common.OpenImageConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 */
public class OpenImageAction extends AnAction {


    private static final Logger LOG = Logger.getInstance(OpenImageAction.class);

    private final Notification notification;

    /**
     * 构造方法
     *
     * @param text         通知中可点击的按钮的文案
     * @param notification 通知对象，在点击按钮之后需要调用 {@link Notification#expire()}
     */
    OpenImageAction(String text, Notification notification) {
        super(text);
        this.notification = notification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        DataManager.getInstance().getDataContextFromFocus()
                .doWhenDone((Consumer<DataContext>) (dataContext -> new OpenImageConsumer().accept(dataContext)))
                .doWhenRejected((Consumer<String>) LOG::error);
        notification.expire();
        LOG.info("notification action has been expired");
    }
}
