package run.mone.ultraman.common;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

public class GitProjectOpener {

    public static void openGitProject(String projectPath) {
        // 获取本地文件系统的实例
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

        // 使用项目路径找到对应的 VirtualFile 实例
        VirtualFile projectDirectory = localFileSystem.refreshAndFindFileByIoFile(new File(projectPath));

        // 检查项目目录是否存在
        if (projectDirectory == null) {
            System.err.println("Project directory not found: " + projectPath);
            return;
        }

        Project project = ProjectUtil.openOrImport(projectDirectory.getPath(), null, true);

        // 如果项目打开成功，则 project 不为 null
        if (project != null) {
            System.out.println("Project opened successfully: " + project.getName());
        } else {
            System.err.println("Failed to open project: " + projectPath);
        }
    }
}
