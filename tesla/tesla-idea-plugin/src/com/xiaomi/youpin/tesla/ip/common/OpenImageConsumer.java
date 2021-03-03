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
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.impl.http.HttpFileSystemImpl;
import com.intellij.openapi.vfs.impl.http.HttpsFileSystem;
import com.intellij.testFramework.BinaryLightVirtualFile;
import org.apache.commons.io.FileUtils;
import org.nutz.http.Http;
import org.nutz.http.Response;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;


public class OpenImageConsumer implements Consumer<DataContext> {

    private static final Logger LOG = Logger.getInstance(OpenImageConsumer.class);


    private String imgUrl;

    public OpenImageConsumer() {
    }

    public OpenImageConsumer(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public void accept(DataContext dataContext) {
        // 1. 获取 IDEA 正在使用的 Project
        Project currentProject = dataContext.getData(PlatformDataKeys.PROJECT);
        if (currentProject == null) {
            LOG.warn("currentProject cannot be null");
            return;
        }

        // 2. 获取即将用于展示的图片
        String imageUrlStr = "https://www.baidu.com/img/superlogo_c4d7df0a003d3db9b65e9ef0fe6da1ec.png?where=super";

        if (null != imgUrl) {
            imageUrlStr = imgUrl;
        }


        URL imageUrl;
        try {
            imageUrl = new URL(imageUrlStr);
        } catch (MalformedURLException e) {
            LOG.error("parse the image URL \"" + imageUrlStr + "\" error", e);
            return;
        }
//        VirtualFile image = new BinaryLightVirtualFile("tmp/a",PlainTextFileType.INSTANCE,"ggogogo".getBytes());
//        VirtualFile image = HttpsFileSystem.getInstance().refreshAndFindFileByPath(imageUrlStr);、
        String f = "/tmp/a.png";


        Response res = Http.get(imageUrlStr);
        InputStream is = res.getStream();


        try {

            if (Files.exists(Paths.get("/tmp/b.jpg"))) {
                Files.delete(Paths.get("/tmp/b.jpg"));
            }

            Files.copy(is,Paths.get("/tmp/b.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        VirtualFile image = LocalFileSystem.getInstance().refreshAndFindFileByPath("/tmp/b.jpg");
//        try {
//            image.setWritable(false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        VirtualFile image = VfsUtil.findFileByURL("/tmp/a.txt");

//        try {
//            VirtualFile d = VfsUtil.createDirectoryIfMissing("/tmp/a");
//            d.createChildData()
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        VirtualFile image =VirtualFile.createChildData();
        if (image == null) {
//            LOG.error("cannot find the image by URL: " + imageUrl.toString());
            return;
        }

        // 3. 获取当前 Project 中，正在使用的 EditorWindow
        FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(currentProject);
        EditorWindow currentWindow = fileEditorManager.getCurrentWindow();
//        if (currentWindow == null || currentWindow.getTabCount() == 0) {
            // 3.1 如果没有打开 EditorWindow 或者 EditorWindow 打开的 tab 为零，那就直接打开图片
            fileEditorManager.openFile(image, false);
//        } else {
//            // 4 获取下一个 EditorWindow
//            EditorWindow nextWindow = fileEditorManager.getNextWindow(currentWindow);
//            if (nextWindow == currentWindow) {
//                // 4.1 如果下一个 EditorWindow 还是它自己，表示 IDEA 只打开了一个 EditorWindow
//                // 4.1 那则需要创建一个垂直分屏，再打开图片
//                currentWindow.split(SwingConstants.VERTICAL, true, image, true);
//            } else {
//                // 4.2 在下一个 EditorWindow 打开图片
//                fileEditorManager.openFileWithProviders(image, false, nextWindow);
//            }
//        }
        LOG.info("image has been opened");
    }

}
