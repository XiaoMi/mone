package run.mone.z.desensitization.service.common;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTool;

import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * @author wmin
 * @date 2023/6/5
 * 解析class语法树
 */
public class ClassUtils {

    public static CompilationUnitTree getUnitTreeWithClassName(String sourceCode, String className) throws IOException {
        JavaCompiler compiler = JavacTool.create();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        JavaFileObject sourceFile = new JavaSourceFromString(className, sourceCode);
        // 设置虚拟源文件的存储位置
        fileManager.setLocation(StandardLocation.CLASS_PATH, Arrays.asList());
        // 编译源代码
        JavacTask task = (JavacTask) compiler.getTask(null, fileManager, null, null, null, Arrays.asList(sourceFile));
        // 获取编译单元树
        Trees trees = Trees.instance(task);
        Iterable<? extends CompilationUnitTree> compilationUnits = task.parse();
        CompilationUnitTree compilationUnit = compilationUnits.iterator().next();
        // 关闭文件管理器
        fileManager.close();
        return compilationUnit;
    }

    private static CompilationUnit getUnitTree(String sourceCode){
        JavaParser javaParser = new JavaParser();
        CompilationUnit compilationUnit = javaParser.parse(sourceCode).getResult().get();
        return compilationUnit;
    }

    // 自定义Java源文件对象
    static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;
        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
