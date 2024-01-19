package run.mone.ultraman.service;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import run.mone.m78.ip.util.LabelUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wmin
 * @date 2023/11/21
 */
@Slf4j
public class AutoFlushBizService {

    final static ConcurrentHashMap<String, Stack<String>> fileChangeStatus = new ConcurrentHashMap<>();

    static String CHANGED = "changed";

    public static void notifyDocumentChanged(VirtualFile file){
    }

    public static void notifyDocumentClosed(VirtualFile file, Project project){

    }

    public static void autoFlushBiz(VirtualFile file, Project project){
        Module module = ModuleUtil.findModuleForFile(file, project);
        ModuleService.uploadFileText(project, module, file);
    }

}
