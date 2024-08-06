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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * @author goodjava@qq.com
 */
public class OpenImageConsumer implements Consumer<DataContext> {

    private static final Logger LOG = Logger.getInstance(OpenImageConsumer.class);

    private String imgUrl = "xxx";

    public OpenImageConsumer() {
    }

    public OpenImageConsumer(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public void accept(DataContext dataContext) {
        Project currentProject = dataContext.getData(PlatformDataKeys.PROJECT);
        if (currentProject == null) {
            LOG.warn("currentProject cannot be null");
            return;
        }
        LOG.info("image url:" + this.imgUrl);
        String imagePath = imagePath();
        copyImg(imagePath, null);
        VirtualFile image = LocalFileSystem.getInstance().refreshAndFindFileByPath(imagePath);
        if (image == null) {
            LOG.info("image is null");
            return;
        }

        FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(currentProject);
        fileEditorManager.openFile(image, false);
    }

    private void copyImg(String path, InputStream is) {
        try {
            if (Files.exists(Paths.get(path))) {
                Files.delete(Paths.get(path));
            }
            Files.copy(is, Paths.get(path));
        } catch (IOException e) {
            LOG.info("copy img error:" + e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String imagePath() {
        String name = UUID.randomUUID().toString() + ".jpg";
        String defaultBaseDir = System.getProperty("java.io.tmpdir");
        String path = defaultBaseDir + File.separator + name;
        return path;
    }

}
