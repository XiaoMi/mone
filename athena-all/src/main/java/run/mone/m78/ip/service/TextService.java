package run.mone.m78.ip.service;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;


/**
 * @author goodjava@qq.com
 * @date 2023/5/17 15:46
 */
@Slf4j
public class TextService {


    public static void writeContent(Project project, String fileName, String moduleName, Runnable runnable) {
        writeContent(project, fileName, moduleName, runnable, null);
    }


    public static String readContent(Project project, String module, String fileName) {
        PsiDirectory directory = getPsiDirectory(project, module);
        if (null == directory) {
            return "";
        }
        PsiFile file = directory.findFile(fileName);
        if (null == file) {
            return "";
        }
        return file.getText();
    }


    public static void writeContent(Project project, String fileName, String moduleName, Runnable runnable, String content) {

    }

    @NotNull
    private static String getFileContent(String content) {
        String fileContent = " ";
        if (StringUtils.isNotEmpty(content)) {
            fileContent = content;
        }
        return fileContent;
    }


    private static PsiDirectory getPsiDirectory(Project project, String moduleName) {
       return null;
    }


}
