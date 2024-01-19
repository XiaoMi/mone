package run.mone.m78.ip.util;

import com.google.common.base.Joiner;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/7/8 23:19
 */
public class GitUtils {


    /**
     * 获取项目中受影响的文件名列表。
     *
     * @param project 项目对象
     * @return 受影响的文件名列表
     */
    public static List<String> getAffectedFileNames(Project project) {
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        return changeListManager.getAffectedFiles().stream().map(it -> it.toString()).collect(Collectors.toList());
    }

    public static String getAffectedFileNamesStr(Project project) {
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        List<String> list = changeListManager.getAffectedFiles().stream().map(it -> it.toString()).collect(Collectors.toList());
        return list.stream().collect(Collectors.joining("\n"));
    }


    //获取git地址
    @SneakyThrows
    public static String getGitAddress(Project project) {
        return null;
    }

    //获取最后一条commit记录
    @SneakyThrows
    public static List<String> getLastCommit(Project project) {
        return null;
    }

}
