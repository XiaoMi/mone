package run.mone.ultraman.service;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author goodjava@qq.com
 * @date 2024/2/27 14:31
 */
public class AgentService {


    /**
     * 关闭项目中所有打开的文件。
     *
     * @param project 当前操作的项目对象
     */
    public static void closeAllOpenFilesInProject(Project project) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        // 获取所有打开的文件
        VirtualFile[] openFiles = fileEditorManager.getOpenFiles();
        // 循环关闭每一个打开的文件
        for (VirtualFile openFile : openFiles) {
            fileEditorManager.closeFile(openFile);
        }
    }


}
