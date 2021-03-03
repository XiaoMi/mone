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


import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * 打开文本文件
 * @author goodjava@qq.com
 */
public class OpenTextConsumer implements Consumer<DataContext> {

    private static final Logger LOG = Logger.getInstance(OpenTextConsumer.class);

    private String content;
    private String fileName;

    public OpenTextConsumer() {
    }

    public OpenTextConsumer(String content,String fileName) {
        this.content = content;
        this.fileName = fileName;
    }

    @Override
    public void accept(DataContext dataContext) {
        Project currentProject = dataContext.getData(PlatformDataKeys.PROJECT);
        if (currentProject == null) {
            LOG.warn("currentProject cannot be null");
            return;
        }

        String name =  fileName;

        try {
            Files.write(Paths.get("/tmp/" + name), this.content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }


        VirtualFile textFile = LocalFileSystem.getInstance().refreshAndFindFileByPath("/tmp/" + name);
        if (textFile == null) {
            return;
        }

        FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(currentProject);
        fileEditorManager.openFile(textFile, false);
        LOG.info("image has been opened");
    }

}
