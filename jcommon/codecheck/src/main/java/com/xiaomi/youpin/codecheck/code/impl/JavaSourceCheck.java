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
