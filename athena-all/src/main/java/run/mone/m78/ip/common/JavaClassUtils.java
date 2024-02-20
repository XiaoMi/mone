package run.mone.m78.ip.common;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

/**
 * @author goodjava@qq.com
 * @date 2023/4/12 11:05
 */
public abstract class JavaClassUtils {

    /**
     * Create a class.
     */
    public static void createClass(AnActionEvent e) {
        createClass(e.getProject(), e.getData(PlatformDataKeys.EDITOR), "abc", "");
    }

    public static void createClass(Project project, Editor editor, String name, String code) {

    }


    public static void createClass(Project project, Editor editor, String name, String code, boolean testClass) {

    }

    public static void createClass(Project project, Editor editor, String name, String code, boolean testClass, String packagePath) {

    }


    public static void openClass(AnActionEvent e) {

    }

    public static void openClass(Project project, String className) {

    }


    public static String getClassName(String className) {
        String[] classNameParts = className.split("\\.");
        String simpleClassName = classNameParts[classNameParts.length - 1];
        return simpleClassName;
    }


}
