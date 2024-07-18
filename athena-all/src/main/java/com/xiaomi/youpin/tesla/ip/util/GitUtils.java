package com.xiaomi.youpin.tesla.ip.util;

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
import java.util.StringJoiner;
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
        try {
            String path = project.getBasePath();
            path = Joiner.on(File.separator).join(path, ".git", "config");
            return Files.readAllLines(Paths.get(path)).stream().filter(it -> it.contains("url")).map(it -> (it.split("=")[1].trim())).findAny().get();
        } catch (Throwable ignore) {

        }
        return "";
    }

    //获取最后一条commit记录
    @SneakyThrows
    public static List<String> getLastCommit(Project project) {
        List<String> res = new ArrayList<>();
        try {
            String path = project.getBasePath();
            path = Joiner.on(File.separator).join(path, ".git");
            Process process = Runtime.getRuntime().exec("git --git-dir=" + path + " log -3 --pretty=format:\"%H\"");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                int commitCount = 0;
                while ((line = reader.readLine()) != null) {
                    if (commitCount < 3) {
                        res.add(line);
                    } else {
                        break;
                    }
                    commitCount++;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String getCommitDiff(Project project) {
        StringJoiner res = new StringJoiner(System.lineSeparator());
        try {
            // 获取diff内容
            String path = project.getBasePath();
            path = Joiner.on(File.separator).join(path, ".git");
            Process diffProcess = Runtime.getRuntime().exec("git --git-dir=" + path + " --work-tree=" + project.getBasePath() + " diff HEAD");
            try (BufferedReader diffReader = new BufferedReader(new InputStreamReader(diffProcess.getInputStream()))) {
                String line;
                while ((line = diffReader.readLine()) != null) {
                    res.add(line);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    public static String getCurrentBranch(Project project) {
        String branch = "";
        try {
            // 执行git命令
            String path = project.getBasePath();
            path = Joiner.on(File.separator).join(path, ".git");
            Process process = Runtime.getRuntime().exec("git --git-dir=" + path + " --work-tree=" + project.getBasePath() + " rev-parse --abbrev-ref HEAD");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            branch = reader.readLine();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return branch;
    }

    public static String getProjectNameFromGitUrl(String gitUrl) {
        String urlWithoutProtocol = gitUrl.replaceFirst("^\\w+://|^\\w+@", "");
        String urlWithoutExtension = urlWithoutProtocol.replaceFirst("\\.git$", "");
        String[] parts = urlWithoutExtension.split("/");
        String projectName = parts[parts.length - 1];
        return projectName;
    }

}
