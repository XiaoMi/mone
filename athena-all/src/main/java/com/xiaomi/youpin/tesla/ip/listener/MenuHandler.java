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
import org.cef.browser.CefFrame;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.handler.CefContextMenuHandlerAdapter;

import java.awt.*;

/**
 * @author goodjava@qq.com
 * @date 2021/11/20
 */
public class MenuHandler extends CefContextMenuHandlerAdapter {
    private final Frame owner;

    public MenuHandler(Frame owner) {
        this.owner = owner;
    }

    private final static int MENU_ID_SHOW_DEV_TOOLS = 10000;

    @Override
    public void onBeforeContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params, CefMenuModel model) {
        //清除菜单项
        model.clear();
        model.addItem(CefMenuModel.MenuId.MENU_ID_VIEW_SOURCE, "view source");
        model.addItem(MENU_ID_SHOW_DEV_TOOLS, "inspect");
    }

    @Override
    public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame, CefContextMenuParams params, int commandId, int eventFlags) {
        switch (commandId) {
            case MENU_ID_SHOW_DEV_TOOLS:
                // 打开开发者选项
                DevToolsDialog devToolsDlg = new DevToolsDialog(owner, "开发者选项", browser);
                devToolsDlg.setLocationRelativeTo(null);
                devToolsDlg.setVisible(true);
                devToolsDlg.setResizable(false);
                return true;
        }
        return false;
    }
}
