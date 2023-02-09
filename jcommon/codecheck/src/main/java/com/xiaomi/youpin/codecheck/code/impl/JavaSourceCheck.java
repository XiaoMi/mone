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

package com.xiaomi.youpin.codecheck.code.impl;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author goodjava@qq.com
 */
public class JavaSourceCheck {

    private JavaCompiler.CompilationTask compilationTask;

    private TreeScanner treeScanner;


    public JavaSourceCheck(String sourcePath, TreeScanner treeScanner) {
        String path = sourcePath;
        Context context = new Context();
        JavacTool javacTool = new JavacTool();
        JavacFileManager fileManager = new JavacFileManager(context, true, Charset.defaultCharset());
        Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjects(path);
        this.treeScanner = treeScanner;
        compilationTask = javacTool.getTask(null, fileManager, null, null, null, files);

    }


    public void check() {
        JavacTask javacTask = (JavacTask) compilationTask;
        try {
            Iterable<? extends CompilationUnitTree> result = javacTask.parse();
            for (CompilationUnitTree tree : result) {
                tree.accept(treeScanner, null);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
