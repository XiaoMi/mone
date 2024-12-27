package run.mone.ultraman.service;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xiaomi.youpin.tesla.ip.util.LabelUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author wmin
 * @date 2023/11/21
 */
@Slf4j
public class AutoFlushBizService {

    final static ConcurrentHashMap<String, Stack<String>> fileChangeStatus = new ConcurrentHashMap<>();

    static String CHANGED = "changed";

    public static void notifyDocumentChanged(VirtualFile file){

        if (null == file) {
            return;
        }

        try {
            if (LabelUtils.getLabelValue(null, "auto_biz_write", "false").equals("true") && file.getFileType() == JavaFileType.INSTANCE) {
                String filePath = file.getPath();
                if (fileChangeStatus.containsKey(filePath)){
                    Stack<String> oldStack = fileChangeStatus.get(filePath);
                    if (null==oldStack || oldStack.empty()){
                        Stack<String> stack = new Stack<>();
                        stack.push(CHANGED);
                        fileChangeStatus.put(filePath, stack);
                    }
                } else {
                    Stack<String> stack = new Stack<>();
                    stack.push(CHANGED);
                    fileChangeStatus.put(filePath, stack);
                }
            }
        } catch (Exception e){
            log.error("notifyDocumentChanged error."+file.getPath(), e);
        }
    }

    public static void notifyDocumentClosed(VirtualFile file, Project project){
        try {
            if (LabelUtils.getLabelValue(null, "auto_biz_write", "false").equals("true") && file.getFileType() == JavaFileType.INSTANCE) {
                String filePath = file.getPath();
                if (fileChangeStatus.containsKey(filePath)){
                    Stack<String> oldStack = fileChangeStatus.get(filePath);
                    if (null!=oldStack && !oldStack.empty() && CHANGED.equals(oldStack.pop())){
                        log.info("file closed after changed, filePath:{}", filePath);
                        autoFlushBiz(file, project);
                    }
                }
            }
        } catch (Exception e){
            log.error("notifyDocumentClosed error."+file.getPath(), e);
        }
    }

    public static void autoFlushBiz(VirtualFile file, Project project){
        Module module = ModuleUtil.findModuleForFile(file, project);
        ModuleService.uploadFileText(project, module, file);
    }

//    public void init(){
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//            if (LabelUtils.getLabelValue(null, "auto_biz_write", "false").equals("true")){
//                fileChangeStatus.entrySet().forEach(i ->{
//
//                });
//            }
//        }, 5, 10, TimeUnit.MINUTES);
//    }
}
