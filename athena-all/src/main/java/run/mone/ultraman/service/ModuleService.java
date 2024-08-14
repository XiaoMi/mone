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
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.NotificationCenter;
import com.xiaomi.youpin.tesla.ip.service.TextService;
import com.xiaomi.youpin.tesla.ip.util.LabelUtils;
import com.xiaomi.youpin.tesla.ip.util.PsiClassUtils;
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
     * @param project
     * @param module
     * @param file
     */
    public static void uploadFileText(Project project, Module module, VirtualFile file) {
        Task.Backgroundable task = new Task.Backgroundable(project, "Upload code to code server.") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().invokeLater(() -> sendToCodeServer(Lists.newArrayList(javaFileText(project, module, file)), project, module, "virtual file"));
            }
        };
        AthenaTask.start(task);
    }


    /**
     * 获取整个model的文本描述,并上传到codeserver(主要是接口内容)
     *
     * @param project
     * @param module
     */
    public static void uploadModelText(Project project, Module module) {
        Task.Backgroundable task = new Task.Backgroundable(project, "Upload code to code server.") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    List<String> list = new ArrayList<>();
                    ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
                    VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
                    for (VirtualFile contentRoot : contentRoots) {
                        projectFileIndex.iterateContentUnderDirectory(contentRoot, virtualFile -> {
                            if (virtualFile.getFileType() == JavaFileType.INSTANCE) {
                                String str = javaFileText(project, module, virtualFile);
                                list.add(str);
                            }
                            return true;
                        });
                    }
                    sendToCodeServer(list, project, module, "module");
                });

            }
        };
        AthenaTask.start(task);
    }

    private static void sendToCodeServer(List<String> list, Project project, Module module, String type) {
        String str = Joiner.on("\n\n").join(list);

        //上传到code 服务器
        new Thread(() -> {
            log.info("upload code {} type:{}", list.size(), type);
            AthenaCodeService.uploadCode(CodeReq.builder().projectName(project.getName())
                    .moduleName(module.getName())
                    .code(str)
                    .build());
        }).start();


        NotificationCenter.notice(project, "biz code size:" + str.length() + " module name:" + module.getName() + " type:" + type, true);

        //一次性写入
        if (LabelUtils.getLabelValue(null, "biz_write", "false").equals("true")) {
            TextService.writeContent(project, Const.MODULE_FILE_NAME, module.getName(), () -> {
            }, "```\n" + str + "\n```");
        }
    }


    public static String javaFileText(Project project, Module module, VirtualFile virtualFile) {
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile psiFile = psiManager.findFile(virtualFile);

        List<String> list = new ArrayList<>();
        if (psiFile instanceof PsiJavaFile) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
            //跳过测试类
            if (psiJavaFile.getName().endsWith("Test.java")) {
                return "";
            }
            PsiClass[] psiClasses = psiJavaFile.getClasses();
            for (PsiClass psiClass : psiClasses) {
                //跳过接口
                if (psiClass.isInterface()) {
                    continue;
                }
                list.add(classText(project, psiClass));
            }
        }
        return Joiner.on("\n\n").join(list);
    }


    public static String classText(Project project, PsiClass psiClass) {
        String text = PsiClassUtils.getInterfaceText(project, psiClass);
        return text;
    }

}
