package run.mone.ultraman.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import run.mone.m78.ip.common.Const;
import run.mone.m78.ip.common.NotificationCenter;
import run.mone.m78.ip.service.TextService;
import run.mone.m78.ip.util.LabelUtils;
import run.mone.m78.ip.util.PsiClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.background.AthenaTask;
import run.mone.ultraman.bo.CodeReq;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/11/2 16:03
 */
@Slf4j
public class ModuleService {


    /**
     * 上传单个java file 到 code server
     *
     * @param project
     * @param module
     * @param file
     */
    public static void uploadFileText(Project project, Module module, VirtualFile file) {

    }


    /**
     * 获取整个model的文本描述,并上传到codeserver(主要是接口内容)
     *
     * @param project
     * @param module
     */
    public static void uploadModelText(Project project, Module module) {

    }

    private static void sendToCodeServer(List<String> list, Project project, Module module, String type) {

    }


    public static String javaFileText(Project project, Module module, VirtualFile virtualFile) {
        return "";
    }


    public static String classText(Project project, PsiClass psiClass) {
        return "";
    }

}
