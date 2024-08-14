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

package com.xiaomi.youpin.tesla.ip.listener;

import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author goodjava@qq.com
 * @date 2021/11/20
 */
public class DevToolsDialog extends JDialog {

    private final CefBrowser devTools_;

    // 一般使用这个构造方法
    public DevToolsDialog(Frame owner, String title, CefBrowser browser) {
        this(owner, title, browser, null);
    }

    public DevToolsDialog(Frame owner, String title, CefBrowser browser, Point inspectAt) {
        setLayout(new BorderLayout());	// 设置布局
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();	// 拿到屏幕尺寸
        setSize(screenSize.width / 3,screenSize.height / 3);	//设置大小为屏幕尺寸的一半，可以自定大小

        devTools_ = browser.getDevTools(inspectAt);	// 获取到 browser 的 DevTools
        add(devTools_.getUIComponent());	// 将其 UIComponent 添加上去

        // 添加相关监听
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                dispose();
            }
        });
    }

    @Override
    public void dispose() {
        devTools_.close(true);	// 关闭的时候触发此方法，关闭 DevTools
        super.dispose();
    }


}
