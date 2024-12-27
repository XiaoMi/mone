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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 * 生成代码
 */
public class GenerateAction extends AnAction {

    /**
     * 刷新项目
     */
    private void refreshProject(AnActionEvent event) {
        event.getProject().getBaseDir().refresh(false, true);
    }


    private void build(String filePath, String fileName) {
        File floder = new File(filePath);
        if (!floder.exists()) {
            floder.mkdirs();
        }

        File file = new File(filePath + "/" + fileName);
        if (file.exists()) {
            return;
        }

        try {
            Files.write(Paths.get(filePath + File.separator + fileName), "abc--->zzy".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        String projectBasePath = event.getProject().getBasePath();

        build(projectBasePath+File.separator+"a","init.data");
        build(projectBasePath+File.separator+"b","init.data");


//        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        VirtualFile file = null;
        String filePath = file.getPath();


        build(filePath, "zzy.java");

        refreshProject(event);
    }
}
