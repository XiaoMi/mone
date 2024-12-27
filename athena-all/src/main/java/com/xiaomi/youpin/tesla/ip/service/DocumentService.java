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

package com.xiaomi.youpin.tesla.ip.service;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/6 12:08
 */
public class DocumentService {

    private static final Logger log = Logger.getInstance(DocumentService.class);

    /**
     * 用ide打开文件
     * @param name
     * @param text
     */
    public void open(String name, String text) {
        log.info("open file");
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(project);
        try {
            Files.write(Paths.get("/tmp/" + name), text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        (new WriteCommandAction.Simple(project) {
            @Override
            protected void run() {
                VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath("/tmp/" + name);
                fileEditorManager.openFile(file, false);
            }
        }).execute();
    }

    /**
     * 获取文件内容和文件名(content,fileName)
     * @param anActionEvent
     * @return
     */
    public Pair<String, String> getContent(AnActionEvent anActionEvent) {
        log.info("get content");
        Editor editor = null;
        if (null == anActionEvent) {
            MutableObject mo = new MutableObject();
            ApplicationManager.getApplication().invokeAndWait(()->{
                Project project = ProjectManager.getInstance().getOpenProjects()[0];
                Editor e = FileEditorManager.getInstance(project).getSelectedTextEditor();
                mo.setValue(e);
            });
            editor = (Editor) mo.getValue();
        } else {
            editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        }

        if (null == editor) {
            return null;
        }

        String name = "tmp.java";
        if (editor instanceof EditorImpl) {
            EditorImpl ei = (EditorImpl) editor;
            name = ei.getVirtualFile().getName();
            System.out.println(name);
        }

        Document document = editor.getDocument();
        String text = document.getText();
        return Pair.of(text, name);
    }

}
