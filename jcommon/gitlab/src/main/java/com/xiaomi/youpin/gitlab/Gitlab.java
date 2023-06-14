/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gitlab;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.data.push.client.HttpClientV6;
import com.xiaomi.data.push.client.bo.HttpResult;
import com.xiaomi.youpin.gitlab.bo.*;
import com.xiaomi.youpin.gitlab.exception.InvalidTokenException;
import com.xiaomi.youpin.gitlab.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.xiaomi.youpin.gitlab.GitlabConst.*;

/**
 * @author gaoyibo
 */
@Slf4j
public class Gitlab {

    private static final String version = "0.0.2:2020-01-15";
    private static final String GIT_PUSH_PATH = "/tmp/git/push/";
    private static final String username = "";
    private static final String password = "";

    public Gitlab(String gitlabBaseUrl) {
        if (StringUtils.isBlank(gitlabBaseUrl)) {
            this.gitlabApiUrl = GIT_API_URL;
        } else {
            this.gitlabApiUrl = gitlabBaseUrl;
        }
    }

    public Gitlab() {
        this("");
    }

    private String gitlabApiUrl;

    private String encodePath(String path) {
        String result = null;

        try {
            result = URLEncoder.encode(path, "UTF-8")
                    .replaceAll("\\.", "%2E")
                    .replaceAll("\\/", "%2F");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            result = path;
        }

        return result;
    }

    @Deprecated
    public String fetchFile(String projectId, String token, String path, String branch) {
        String encodePath = encodePath(path);
        String url = this.gitlabApiUrl + "projects/" + projectId + "/repository/files/" + encodePath + "/raw" + "?ref=" + branch;

        Map<String, String> headers = new HashMap<>(1);
        headers.put("PRIVATE-TOKEN", token);

        return HttpClientV2.get(url, headers, 10000);
    }

    @Deprecated
    public List<GitlabCommit> fetchFileVersion(String projectId, String token, String path, String branch) {
        String encodePath = encodePath(path);
        String url = this.gitlabApiUrl + "projects/" + projectId + "/repository/commits?ref_name=" + branch + "&path=" + encodePath;

        Map<String, String> headers = new HashMap<>(1);
        headers.put("PRIVATE-TOKEN", token);

        String versionListText = HttpClientV2.get(url, headers, 10000);

        Gson gson = new Gson();
        List<GitlabCommit> res = gson.fromJson(versionListText, new TypeToken<List<GitlabCommit>>() {
        }.getType());

        return res;
    }

    @Deprecated
    public List<GitlabProject> fetchProjects(String token, Map<String, String> params) {
        List<GitlabProject> list;
        StringBuilder sb = new StringBuilder(gitlabApiUrl + "projects");
        setParams(params, sb);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            list = new Gson().fromJson(HttpClientV2.get(sb.toString(), headers, 10000), new TypeToken<List<GitlabProject>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Deprecated
    public GitlabProject fetchProject(String projectId, String token, Map<String, String> params) {
        GitlabProject gitlabProject;
        StringBuilder sb = new StringBuilder(gitlabApiUrl + "projects/" + projectId);
        setParams(params, sb);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            gitlabProject = new Gson().fromJson(HttpClientV2.get(sb.toString(), headers, 10000), new TypeToken<GitlabProject>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            gitlabProject = null;
        }
        return gitlabProject;
    }

    @Deprecated
    public List<GitlabBranch> fetchBranches(String projectId, String token, Map<String, String> params) {
        List<GitlabBranch> list;
        StringBuilder url = new StringBuilder(gitlabApiUrl + "projects/" + projectId + "/repository/branches");
        setParams(params, url);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            list = new Gson().fromJson(HttpClientV2.get(url.toString(), headers, 10000), new TypeToken<List<GitlabBranch>>() {
            }.getType());
            return list;
        } catch (JsonSyntaxException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Deprecated
    public List<GitlabGroup> getGroups(String token, Map<String, String> params) throws UnsupportedEncodingException {
        List<GitlabGroup> list;
        StringBuilder url = new StringBuilder(gitlabApiUrl + "groups");
        setParams(params, url);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            list = new Gson().fromJson(HttpClientV2.get(url.toString(), headers, 10000), new TypeToken<List<GitlabGroup>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Deprecated
    public List<GitlabCommit> fetchCommits(String projectId, String branch, String token, Map<String, String> params) {
        List<GitlabCommit> list;
        StringBuilder url = new StringBuilder(gitlabApiUrl + "projects/" + projectId + "/repository/commits?ref_name=" + branch);
        setParams(params, url);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            list = new Gson().fromJson(HttpClientV2.get(url.toString(), headers, 10000), new TypeToken<List<GitlabCommit>>() {
            }.getType());
            list.stream().parallel().forEach(it -> {
                it.setCommitted_date(conversionTime(it.getCommitted_date()));
            });
        } catch (JsonSyntaxException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Deprecated
    public GitlabCommit fetchCommit(String projectId, String sha, String token) {
        GitlabCommit gitlabCommit;
        String url = gitlabApiUrl + "projects/" + projectId + "/repository/commits/" + sha;
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            String res = HttpClientV2.get(url, headers, 10000);
            gitlabCommit = new Gson().fromJson(res, GitlabCommit.class);
            if (StringUtils.isEmpty(gitlabCommit.getId())) {
                return null;
            } else {
                gitlabCommit.setCommitted_date(conversionTime(gitlabCommit.getCommitted_date()));
            }
        } catch (JsonSyntaxException e) {
            gitlabCommit = null;
        }
        return gitlabCommit;
    }

    @Deprecated
    public GroupInfo getNamespacesById(String id, String token) {
        GroupInfo groupInfo;
        String url = gitlabApiUrl + "namespaces/" + id;
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            String res = HttpClientV2.get(url, headers, 10000);
            groupInfo = new Gson().fromJson(res, GroupInfo.class);
        } catch (JsonSyntaxException e) {
            groupInfo = null;
        }
        return groupInfo;
    }

    public String fetchFile(String gitHost, String projectId, String token, String path, String branch) {
        String encodePath = encodePath(path);
        String url = gitHost + GIT_API_URI + "projects/" + projectId + "/repository/files/" + encodePath + "/raw" + "?ref=" + branch;

        Map<String, String> headers = new HashMap<>(1);
        headers.put("PRIVATE-TOKEN", token);

        return HttpClientV2.get(url, headers, 10000);
    }

    public List<GitlabCommit> fetchFileVersion(String gitHost, String projectId, String token, String path, String branch) {
        String encodePath = encodePath(path);
        String url = gitHost + GIT_API_URI + "projects/" + projectId + "/repository/commits?ref_name=" + branch + "&path=" + encodePath;

        Map<String, String> headers = new HashMap<>(1);
        headers.put("PRIVATE-TOKEN", token);

        String versionListText = HttpClientV2.get(url, headers, 10000);

        Gson gson = new Gson();
        List<GitlabCommit> res = gson.fromJson(versionListText, new TypeToken<List<GitlabCommit>>() {
        }.getType());

        return res;
    }

    public List<GitlabProject> fetchProjects(String gitHost, String token, Map<String, String> params) {
        List<GitlabProject> list;
        StringBuilder sb = new StringBuilder(gitHost + GIT_API_URI + "projects");
        setParams(params, sb);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            list = new Gson().fromJson(HttpClientV2.get(sb.toString(), headers, 10000), new TypeToken<List<GitlabProject>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    public GitlabProject fetchProject(String gitHost, String projectId, String token, Map<String, String> params) {
        GitlabProject gitlabProject;
        StringBuilder sb = new StringBuilder(gitHost + GIT_API_URI + "projects/" + projectId);
        setParams(params, sb);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            gitlabProject = new Gson().fromJson(HttpClientV2.get(sb.toString(), headers, 10000), new TypeToken<GitlabProject>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            gitlabProject = null;
        }
        return gitlabProject;
    }

    public List<GitlabBranch> fetchBranches(String gitHost, String projectId, String token, Map<String, String> params) throws InvalidTokenException, NotFoundException {
        StringBuilder url = new StringBuilder(gitHost + GIT_API_URI + "projects/" + projectId + "/repository/branches");
        setParams(params, url);
        Map<String, String> headers = new HashMap<>(1);
        headers.put("PRIVATE-TOKEN", token);
        String httpResponse = null;
        try {
            httpResponse = HttpClientV2.get(url.toString(), headers, 10000);
            return new Gson().fromJson(httpResponse, new TypeToken<List<GitlabBranch>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            log.warn("fetchBranches token = {}, result = {}", token, httpResponse);
            ErrorResponse errorResponse;
            try {
                errorResponse = new Gson().fromJson(httpResponse, new TypeToken<ErrorResponse>() {
                }.getType());
            } catch (Exception e2) {
                return new ArrayList<>();
            }
            if (errorResponse == null) {
                return new ArrayList<>();
            }
            if ("invalid_token".equalsIgnoreCase(errorResponse.getError())) {
                throw new InvalidTokenException(errorResponse.getError_description());
            }
            if (errorResponse.getMessage() != null) {
                throw new NotFoundException(errorResponse.getMessage());
            }

        }
        return new ArrayList<>();
    }

    public List<GitlabGroup> getGroups(String gitHost, String token, Map<String, String> params) throws UnsupportedEncodingException {
        List<GitlabGroup> list;
        StringBuilder url = new StringBuilder(gitHost + GIT_API_URI + "groups");
        setParams(params, url);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            list = new Gson().fromJson(HttpClientV2.get(url.toString(), headers, 10000), new TypeToken<List<GitlabGroup>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    public List<GitlabCommit> fetchCommits(String gitHost, String projectId, String branch, String token, Map<String, String> params) throws InvalidTokenException, NotFoundException {
        StringBuilder url = new StringBuilder(gitHost + GIT_API_URI + "projects/" + projectId + "/repository/commits?ref_name=" + branch);
        setParams(params, url);
        Map<String, String> headers = new HashMap<>(1);
        headers.put("PRIVATE-TOKEN", token);
        String httpResponse = null;
        try {
            httpResponse = HttpClientV2.get(url.toString(), headers, 10000);
            List<GitlabCommit> list = new Gson().fromJson(httpResponse, new TypeToken<List<GitlabCommit>>() {
            }.getType());
            list.stream().parallel().forEach(it -> {
                it.setCommitted_date(conversionTime(it.getCommitted_date()));
            });
            return list;
        } catch (JsonSyntaxException e) {
            log.warn("fetchCommits token = {}, result = {}", token, httpResponse);
            ErrorResponse errorResponse;
            try {
                errorResponse = new Gson().fromJson(httpResponse, new TypeToken<ErrorResponse>() {
                }.getType());
            } catch (Exception e2) {
                log.error("fetchCommits error", e2);
                return new ArrayList<>();
            }
            if (errorResponse == null) {
                log.error("fetchCommits errorResponse is null");
                return new ArrayList<>();
            }
            if ("invalid_token".equalsIgnoreCase(errorResponse.getError())) {
                throw new InvalidTokenException(errorResponse.getError_description());
            }
            if (errorResponse.getMessage() != null) {
                throw new NotFoundException(errorResponse.getMessage());
            }
        }
        log.error("fetchCommits error. return empty");
        return new ArrayList<>();
    }


    public GitlabCommit fetchCommit(String gitHost, String projectId, String sha, String token) {
        GitlabCommit gitlabCommit;
        String url = gitHost + GIT_API_URI + "projects/" + projectId + "/repository/commits/" + sha;
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            String res = HttpClientV2.get(url, headers, 10000);
            gitlabCommit = new Gson().fromJson(res, GitlabCommit.class);
            if (StringUtils.isEmpty(gitlabCommit.getId())) {
                return null;
            } else {
                gitlabCommit.setCommitted_date(conversionTime(gitlabCommit.getCommitted_date()));
            }
        } catch (JsonSyntaxException e) {
            gitlabCommit = null;
        }
        return gitlabCommit;
    }

    public GroupInfo getNamespacesById(String gitHost, String id, String token) {
        GroupInfo groupInfo;
        String url = gitHost + GIT_API_URI + "namespaces/" + id;
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            String res = HttpClientV2.get(url, headers, 10000);
            log.info("getNamespacesById res = {}", res);
            groupInfo = new Gson().fromJson(res, GroupInfo.class);
        } catch (JsonSyntaxException e) {
            groupInfo = null;
        }
        return groupInfo;
    }

    public static boolean push(String gitUrl, String branch, String changedBody, String filePathToReplace, String commitMessage) {
        return push(gitUrl, branch, username, password, changedBody, filePathToReplace, commitMessage);
    }

    public static boolean push(String gitUrl, String branch, String username, String token, String changedBody, String filePathToReplace, String commitMessage) {
        String gitPath = GIT_PUSH_PATH + getProjectName(gitUrl) + "/";
        if (!clone(gitUrl, branch, username, token, gitPath)) {
            return false;
        }
        if (!modifyFile(changedBody, filePathToReplace, gitPath)) {
            return false;
        }

        if (!push(username, token, commitMessage, gitPath)) {
            return false;
        }
        return true;
    }

    private static boolean push(String username, String token, String commitMessage, String gitPath) {
        try {
            Git git = Git.init().setDirectory(new File(gitPath)).call();
            //add
            AddCommand addCommand = git.add();
            addCommand.addFilepattern(".");
            addCommand.call();
            // commit
            CommitCommand commit = git.commit();
            commit.setMessage(commitMessage);
            commit.call();
            // push
            PushCommand pushCommand = git.push();
            pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, token)).setForce(true).call();
        } catch (GitAPIException e) {
            clearIfPresent(gitPath);
            log.error(e.getMessage());
            return false;
        }
        clearIfPresent(gitPath);
        return true;
    }

    private static boolean modifyFile(String changedBody, String filePathToReplace, String gitPath) {
        File changedFile = new File(gitPath + filePathToReplace);
        if (!changedFile.exists()) {
            log.error("filePathToReplace error: " + filePathToReplace);
            clearIfPresent(gitPath);
            return false;
        }

        BufferedWriter bw = null;
        try {
            // 根据文件路径创建缓冲输出流
            bw = new BufferedWriter(new FileWriter(changedFile));
            // 将内容写入文件中
            bw.write(changedBody);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    bw = null;
                }
            }
        }
        return true;
    }

    private static boolean clone(String gitUrl, String branch, String username, String token, String gitPath) {
        clearIfPresent(gitPath);
        CloneCommand cloneCommand = Git.cloneRepository().setURI(gitUrl)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, token))
                .setDirectory(new File(gitPath))
                .setBranch(branch);
        try {
            cloneCommand.call();
        } catch (GitAPIException e) {
            log.error("params error: " + e.getMessage());
            return false;
        }
        return true;
    }


    private static void clearIfPresent(String gitPath) {
        File file = new File(gitPath);
        if (file.exists()) {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                log.error("delete failed");
            }
        }
    }

    private static String getProjectName(String gitUrl) {
        String[] strs = gitUrl.split("/");
        String project = strs[strs.length - 1];
        return project.substring(0, project.length() - 4);
    }

    public static void main(String[] args) throws IOException, GitAPIException {
        String str = "{\"message\":\"404 Project Not Found\"}";
        ErrorResponse response = new Gson().fromJson(str, new TypeToken<ErrorResponse>() {
        }.getType());

    }

    @Deprecated
    private static void createProject(int groupId, String projectName, String token) {
        String createRepositoryUrl = GIT_API_URL + "projects?private_token=" + token;
        String body = "{\"name\": \"%s\",\"namespace_id\": %d}";
        body = String.format(body, projectName, groupId);
        Map<String, String> headers = new HashMap<>();
        headers.put("host", GIT_BASE);
        headers.put("Content-Type", "application/json");
        HttpClientV2.post(createRepositoryUrl, body, headers, 10000);
    }

    private static void createProject(String gitHost, int groupId, String projectName, String token) {
        String createRepositoryUrl = gitHost + GIT_API_URI + "projects?private_token=" + token;
        String body = "{\"name\": \"%s\",\"namespace_id\": %d}";
        body = String.format(body, projectName, groupId);
        Map<String, String> headers = new HashMap<>();
        headers.put("host", gitHost);
        headers.put("Content-Type", "application/json");
        HttpClientV2.post(createRepositoryUrl, body, headers, 10000);
    }

    @Deprecated
    private static Integer getGroupIdIfExist(String token, String groupName) {
        String url = GIT_API_URL + "namespaces?private_token=" + token + "&search=" + groupName;
        List<GroupInfo> groups;
        try {
            String response = HttpClientV2.get(url, new HashMap<>(), 10000);
            groups = new Gson().fromJson(response, new TypeToken<List<GroupInfo>>() {
            }.getType());
        } catch (Exception e) {
            log.error("error for fetching group, " + e);
            return null;
        }
        if (groups == null || groups.size() == 0) {
            log.error("user is not in this group " + groupName);
            return null;
        }
        GroupInfo groupInfo = groups.stream().filter(e -> e.getPath().equals(groupName)).findAny().orElse(null);
        if (groupInfo == null) {
            log.error("user is not in this group " + groupName);
            return null;
        }
        return groupInfo.getId();
    }

    private static Integer getGroupIdIfExist(String apiURL, String token, String groupName) {
        String url = null;
        if (StringUtils.isEmpty(groupName)) {
            return null;
        }
        try {
            url = apiURL + "namespaces/" + URLEncoder.encode(groupName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException:", e);
            return null;
        }
        GroupInfo group = null;
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("PRIVATE-TOKEN", token);
            String response = HttpClientV2.get(url, headers, 10000);
            group = new Gson().fromJson(response, GroupInfo.class);
        } catch (Exception e) {
            log.error("error for fetching group, " + e);
            return null;
        }
        if (group == null || group.getId() == null || group.getId() == 0) {
            log.error("user is not in this group " + groupName);
            return null;
        }
        return group.getId();
    }

    public static boolean createNewProject(String gitUrl, String path, String username, String token) throws IOException, GitAPIException {

        log.info("createProject version:{} url:{} path:{}", version, gitUrl, path);

        if (StringUtils.isEmpty(gitUrl) || StringUtils.isEmpty(path) || StringUtils.isEmpty(username) || StringUtils.isEmpty(token)) {
            return false;
        }
        String[] strArr = gitUrl.split("/");
        String groupName = parseGitGroup(gitUrl);
        String projectName = strArr[strArr.length - 1];
        String gitHost = parseGitHostUrl(gitUrl);

        Integer groupId = getGroupIdIfExist(gitHost + GIT_API_URI, token, groupName);
        if (groupId == null) {
            return false;
        }

        try {
            createProject(gitHost, groupId, projectName, token);
        } catch (Exception e) {
            log.error("createNewProject error:{}", e.getMessage());
            return false;
        }


        String tmpDir = System.getProperty("java.io.tmpdir") + File.separator + "gitlab";
        File tmpFilDir = new File(tmpDir);
        if (!tmpFilDir.exists()) {
            tmpFilDir.mkdirs();
        }
        unZipFiles(new File(path), tmpDir);
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username, token);
        String[] strs = path.split(Matcher.quoteReplacement(File.separator));
        String fileName = strs[strs.length - 1].replace(".jar", "").replace(".zip", "");

        String gitPath = tmpDir + File.separator + fileName;

        log.info("createNewProject gitPath:{}", gitPath);

        Git git = Git.init().setDirectory(new File(gitPath)).call();
        StoredConfig config = git.getRepository().getConfig();
        config.setString("remote", "origin", "url", gitUrl + ".git");
        config.save();

        //add
        AddCommand addCommand = git.add();
        addCommand.addFilepattern(".");
        addCommand.call();

        // commit
        CommitCommand commit = git.commit();
        commit.setCommitter("bot", "bot@xiaomi.com").setMessage("Add all project template files ...");
        commit.call();
        // push
        PushCommand pushCommand = git.push();
        pushCommand.setCredentialsProvider(credentialsProvider).setForce(true).setPushAll();

        Iterator<PushResult> it = pushCommand.call().iterator();
        if (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        // cleanup
        //dir.deleteOnExit();
        log.info("push finish");
        return true;
    }

    public static boolean createEmptyProject(String gitUrl, String username, String token) throws IOException, GitAPIException {

        log.info("createEmptyProject version:{} url:{} ", version, gitUrl);

        if (StringUtils.isEmpty(gitUrl) || StringUtils.isEmpty(username) || StringUtils.isEmpty(token)) {
            return false;
        }
        String[] strArr = gitUrl.split("/");
        String groupName = parseGitGroup(gitUrl);
        String projectName = strArr[strArr.length - 1];
        String gitHost = parseGitHostUrl(gitUrl);

        Integer groupId = getGroupIdIfExist(gitHost + GIT_API_URI, token, groupName);
        if (groupId == null) {
            return false;
        }

        try {
            createProject(gitHost, groupId, projectName, token);
        } catch (Exception e) {
            log.error("createNewProject error:{}", e.getMessage());
            return false;
        }

        return true;
    }

    public static void unZipFiles(File zipFile, String descDir) throws IOException {

        ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));
        String zipName = zip.getName();
        int startIndex = zipName.lastIndexOf("\\") == -1 ? zipName.lastIndexOf("/") : zipName.lastIndexOf("\\");
        String name = zipName.substring(startIndex + 1, zip.getName().lastIndexOf('.'));

        File pathFile = new File(descDir + File.separator + name);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        log.info("unZipFiles====>{} {}", descDir, pathFile);
        System.out.println("--------->" + pathFile);

        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + File.separator + zipEntryName);

            // 判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }

            FileOutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
    }

    private void setParams(Map<String, String> params, StringBuilder sb) {
        if (null != params && 0 != params.size()) {
            boolean isFirst = true;
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> it = iterator.next();
                try {
                    sb.append((isFirst ? "?" : "&") + it.getKey() + "=" + URLEncoder.encode(it.getValue(), "UTF-8"));
                    isFirst = false;
                } catch (UnsupportedEncodingException e) {
                    log.error("Gitlab#setParams {} {}: {}", it.getKey(), it.getValue(), e.getMessage());
                }
            }
        }
    }

    /**
     * 格式：2016-09-03T00:00:00.000+08:00
     *
     * @param time
     * @return
     */
    private String conversionTime(String time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(time);
            Date date1 = df1.parse(date.toString());
            return df2.format(date1);
        } catch (ParseException e) {
            log.warn("Gitlab#conversionByTimeZone exception: {}", e.getMessage());
            return time;
        }
    }

    @Deprecated
    private static UserInfo getUserByToken(String token) {
        String url = GIT_API_URL + "user?private_token=" + token;
        UserInfo userInfo;
        try {
            String response = HttpClientV2.get(url, new HashMap<>(), 10000);
            userInfo = new Gson().fromJson(response, UserInfo.class);
        } catch (Exception e) {
            return null;
        }
        return userInfo;
    }

    private static UserInfo getUserByToken(String apiURL, String token) {
        String url = apiURL + "user?private_token=" + token;
        UserInfo userInfo;
        try {
            String response = HttpClientV2.get(url, new HashMap<>(), 10000);
            userInfo = new Gson().fromJson(response, UserInfo.class);
        } catch (Exception e) {
            return null;
        }
        return userInfo;
    }

    @Deprecated
    public static boolean accessLevelMoreThan(AccessLevel accessLevel, String userToken, String groupName) {
        int level = accessLevel.getLevel();
        if (level <= 0 || StringUtils.isEmpty(userToken) || StringUtils.isEmpty(groupName)) {
            return false;
        }
        Integer groupId = getGroupIdIfExist(userToken, groupName);
        if (groupId == null) {
            return false;
        }
        UserInfo user = getUserByToken(userToken);
        if (user == null) {
            log.error("token error: " + userToken);
        }

        int userAccessLevel = getAccessLevel(groupId, user.getId(), userToken);
        return userAccessLevel >= level ? true : false;
    }

    /**
     * 10 => Guest access
     * 20 => Reporter access
     * 30 => Developer access
     * 40 => Maintainer access
     * 50 => Owner access # Only valid for groups
     *
     * @param accessLevel
     * @return
     */
    public static boolean accessLevelMoreThan(String gitHost, AccessLevel accessLevel, String userToken, String groupName) {
        int level = accessLevel.getLevel();
        if (level <= 0 || StringUtils.isEmpty(userToken) || StringUtils.isEmpty(groupName)) {
            return false;
        }
        Integer groupId = getGroupIdIfExist(gitHost + GIT_API_URI, userToken, groupName);
        if (groupId == null) {
            return false;
        }
        UserInfo user = getUserByToken(gitHost + GIT_API_URI, userToken);
        if (user == null) {
            log.error("token error: " + userToken);
        }

        int userAccessLevel = getAccessLevel(gitHost + GIT_API_URI, groupId, user.getId(), userToken);
        return userAccessLevel >= level ? true : false;
    }

    public static List<GitlabMember> getGitlabMembers(String gitHost, String groupId, String userToken, String username) {
        String url = gitHost + GIT_API_URI + "groups/" + groupId + "/members/all?query=" + username;
        Map<String, String> headers = new HashMap<>();
        headers.put("PRIVATE-TOKEN", userToken);
        String response = HttpClientV2.get(url, headers, 10000);
        List<GitlabMember> gitlabMembers = new Gson().fromJson(response, new TypeToken<List<GitlabMember>>() {
        }.getType());
        return gitlabMembers;
    }

    @Deprecated
    private static int getAccessLevel(Integer groupId, Long userId, String token) {
        if (groupId == null || userId == null || StringUtils.isEmpty(token)) {
            return 0;
        }
        String url = GIT_API_URL + "groups/" + groupId + "/members/" + userId + "?private_token=" + token;
        UserInfo userInfo;
        try {
            String response = HttpClientV2.get(url, new HashMap<>(), 10000);
            userInfo = new Gson().fromJson(response, UserInfo.class);
        } catch (Exception e) {
            log.error("error for query user access level, " + e);
            return 0;
        }
        if (userInfo == null) {
            return 0;
        }
        return userInfo.getAccess_level();
    }

    public List<GitlabTag> getProjectTag(Long projectId, String token) {
        if (projectId == null || StringUtils.isEmpty(token)) {
            return null;
        }
        List<GitlabTag> tagList = new ArrayList<>();
        String url = GIT_API_URL + "projects/" + projectId + "/repository/tags?private_token=" + token;
        try {
            String response = HttpClientV2.get(url, new HashMap<>(), 10000);
            tagList = new Gson().fromJson(response, new TypeToken<List<GitlabTag>>() {
            }.getType());
        } catch (Exception e) {
            log.error("error for query tag" + e);
            return null;
        }
        return tagList;
    }

    public GitlabTag getProjectTagByCommitId(Long projectId, String commitId, String token) {
        if (StringUtils.isEmpty(commitId)) {
            return null;
        }
        List<GitlabTag> projectTagList = getProjectTag(projectId, token);
        if (projectTagList == null || projectTagList.size() == 0) {
            return null;
        }
        // 循环比对tag的commitId
        for (GitlabTag tag : projectTagList) {
            if (tag.getCommit().getId().equals(commitId)) {
                return tag;
            }
        }
        return null;
    }

    private static int getAccessLevel(String aptURL, Integer groupId, Long userId, String token) {
        if (groupId == null || userId == null || StringUtils.isEmpty(token)) {
            return 0;
        }
        String url = aptURL + "groups/" + groupId + "/members/" + userId + "?private_token=" + token;
        UserInfo userInfo;
        try {
            String response = HttpClientV2.get(url, new HashMap<>(), 10000);
            userInfo = new Gson().fromJson(response, UserInfo.class);
        } catch (Exception e) {
            log.error("error for query user access level, " + e);
            return 0;
        }
        if (userInfo == null) {
            return 0;
        }
        return userInfo.getAccess_level();
    }

    private static String parseGitHostUrl(String gitUrl) {
        try {
            URL u = new URL(gitUrl);
            return u.getProtocol() + "://" + u.getHost() + "/";
        } catch (Exception e) {
            return GIT_BASE;
        }
    }

    private static String parseGitGroup(String gitUrl) {
        try {
            URL u = new URL(gitUrl);
            String path = u.getPath();
            if (StringUtils.isEmpty(path) || path.split("/").length < 3) {
                return "";
            }
            return path.substring(1, path.lastIndexOf("/"));
        } catch (Exception e) {
            return GIT_BASE;
        }
    }

    public BaseResponse createBranch(String projectId, String branchName, String ref, String token) {
        if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(branchName) || StringUtils.isEmpty(ref)
                || StringUtils.isEmpty(token)) {
            return new BaseResponse(-1, "createBranch参数无效");
        }
        //    GitlabBranch branch = new GitlabBranch();
        String url = GIT_API_URL + "projects/" + projectId + "/repository/branches";
        try {
            String body = "{\"id\": \"%s\",\"branch\": \"%s\",\"ref\": \"%s\"}";
            body = String.format(body, projectId, branchName, ref);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            HttpResult response = HttpClientV6.httpPost(url, headers, body, "UTF-8", 10000);
            return new BaseResponse(response.code, response.content);
        } catch (Exception e) {
            log.error("createBranch {}", e.getMessage());
            return new BaseResponse(-1, "createBranch异常");
        }
    }

    public BaseResponse createBranch(String gitHost,String projectId, String branchName, String ref, String token) {
        if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(branchName) || StringUtils.isEmpty(ref)
                || StringUtils.isEmpty(token) || StringUtils.isEmpty(gitHost)) {
            return new BaseResponse(-1, "createBranch参数无效");
        }
        //    GitlabBranch branch = new GitlabBranch();
        String url = gitHost + GIT_API_URI + "projects/" + projectId + "/repository/branches";
        log.info("createBranch url:{}",url);
        try {
            String body = "{\"id\": \"%s\",\"branch\": \"%s\",\"ref\": \"%s\"}";
            body = String.format(body, projectId, branchName, ref);
            log.info("createBranch body:{}",body);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            HttpResult response = HttpClientV6.httpPost(url, headers, body, "UTF-8", 10000);
            return new BaseResponse(response.code, response.content);
        } catch (Exception e) {
            log.error("createBranch {}", e.getMessage());
            return new BaseResponse(-1, "createBranch异常");
        }
    }

    public BaseResponse deleteBranch(String projectId, String branchName, String token) {
        if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(branchName) || StringUtils.isEmpty(token)) {
            return new BaseResponse(-1, "response.content");
        }
        String url = GIT_API_URL + "projects/" + projectId + "/repository/branches/" + branchName;
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("PRIVATE-TOKEN", token);
            HttpResult response = HttpClientV6.httpDelete(url, headers, Maps.newHashMap(), "UTF-8", 10000);
            return new BaseResponse(response.code, response.content);
        } catch (Exception e) {
            log.error("deleteBranch {}", e.getMessage());
            return new BaseResponse(-1, "response.content");
        }
    }

    public BaseResponse createMerge(String projectId, String sourceBranch, String targetBranch, String title, String token) {
        if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(sourceBranch) || StringUtils.isEmpty(targetBranch)) {
            return null;
        }
        //  GitlabMerge merge = new GitlabMerge();
        String url = GIT_API_URL + "projects/" + projectId + "/merge_requests";
        try {
            String body = "{\"id\": \"%s\",\"source_branch\": \"%s\",\"target_branch\": \"%s\",\"title\": \"%s\"}";
            body = String.format(body, projectId, sourceBranch, targetBranch, title);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            HttpResult response = HttpClientV6.httpPost(url, headers, body, "UTF-8", 10000);
            return new BaseResponse(response.code, response.content);
        } catch (Exception e) {
            log.error("createMerge {}", e.getMessage());
            return null;
        }
    }

    public BaseResponse createMerge(String gitHost, String projectId, String sourceBranch, String targetBranch, String title, String token) {
        if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(sourceBranch) || StringUtils.isEmpty(targetBranch) ||
                StringUtils.isEmpty(gitHost)) {
            return null;
        }
        //  GitlabMerge merge = new GitlabMerge();
        String url = gitHost + GIT_API_URI + "projects/" + projectId + "/merge_requests";
        log.info("createMerge url:{}", url);
        try {
            String body = "{\"id\": \"%s\",\"source_branch\": \"%s\",\"target_branch\": \"%s\",\"title\": \"%s\"}";
            body = String.format(body, projectId, sourceBranch, targetBranch, title);
            log.info("createMerge body:{}", body);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            HttpResult response = HttpClientV6.httpPost(url, headers, body, "UTF-8", 10000);
            return new BaseResponse(response.code, response.content);
        } catch (Exception e) {
            log.error("createMerge {}", e.getMessage());
            return null;
        }
    }

    public BaseResponse acceptMerge(String projectId, String iid, String token) {
        if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(iid) || StringUtils.isEmpty(token)) {
            return null;
        }
        //     GitlabMerge merge = new GitlabMerge();
        String url = GIT_API_URL + "projects/" + projectId + "/merge_requests/" + iid + "/merge";
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            HttpResult response = HttpClientV6.httpPut(url, headers, "", "UTF-8", 10000);
            return new BaseResponse(response.code, response.content);
        } catch (Exception e) {
            log.error("error acceptMerge{}", e.getMessage());
            return null;
        }
    }

    public BaseResponse acceptMerge(String gitHost, String projectId, String iid, String token) {
        if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(iid) || StringUtils.isEmpty(token) ||
                StringUtils.isEmpty(gitHost)) {
            return null;
        }
        //     GitlabMerge merge = new GitlabMerge();
        String url = gitHost + GIT_API_URI + "projects/" + projectId + "/merge_requests/" + iid + "/merge";
        log.info("acceptMerge url:{}", url);
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            HttpResult response = HttpClientV6.httpPut(url, headers, "", "UTF-8", 10000);
            return new BaseResponse(response.code, response.content);
        } catch (Exception e) {
            log.error("error acceptMerge{}", e.getMessage());
            return null;
        }
    }

    public BaseResponse closeMerge(String gitHost, String projectId, String iid, String token) {
        if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(iid) || StringUtils.isEmpty(gitHost)
                || StringUtils.isEmpty(token)) {
            return null;
        }
        String url = gitHost + GIT_API_URI + "projects/" + projectId + "/merge_requests/" + iid;
        log.info("closeMerge url:{}", url);
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            String body = "{\"id\": \"%s\",\"merge_request_iid\": \"%s\",\"state_event\": \"%s\"}";
            body = String.format(body, projectId, iid, "close");
            log.info("createMerge body:{}", body);
            HttpResult response = HttpClientV6.httpPut(url, headers,body, "UTF-8", 10000);
            return new BaseResponse(response.code, response.content);
        } catch (Exception e) {
            log.error("error closeMerge{}", e.getMessage());
            return null;
        }
    }

    public BaseResponse getMerge(String gitHost, String projectId, String iid, String token) {
        if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(iid) || StringUtils.isEmpty(gitHost)
                || StringUtils.isEmpty(token)) {
            return null;
        }
        String url = gitHost + GIT_API_URI + "projects/" + projectId + "/merge_requests/" + iid;
        log.info("getMerge url:{}", url);
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            HttpResult response = HttpClientV6.httpGet(url, headers);
            return new BaseResponse(response.code, response.content);
        } catch (Exception e) {
            log.error("error getMerge{}", e.getMessage());
            return null;
        }
    }

    public GitWebhook addHook(GitWebhook gitWebhook, String token) {
        if (gitWebhook == null || StringUtils.isEmpty(gitWebhook.getId())) {
            return null;
        }
        String url = GIT_API_URL + "projects/" + gitWebhook.getId() + "/hooks";
        try {
            String body = new Gson().toJson(gitWebhook);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            HttpResult response = HttpClientV6.httpPost(url, headers, body, "UTF-8", 5000);
            log.info("addHook rsp {} {}", response.code, response.content);
            return new Gson().fromJson(response.content, GitWebhook.class);
        } catch (Exception e) {
            log.error("addHook {}", e.getMessage());
            return null;
        }
    }

    public GitWebhook editHook(GitWebhook gitWebhook, String token) {
        if (gitWebhook == null || StringUtils.isEmpty(gitWebhook.getId()) || StringUtils.isEmpty(gitWebhook.getHook_id())) {
            return null;
        }
        String url = GIT_API_URL + "projects/" + gitWebhook.getId() + "/hooks/" + gitWebhook.getHook_id();
        try {
            String body = JSON.toJSONString(gitWebhook);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            HttpResult response = HttpClientV6.httpPut(url, headers, body, "UTF-8", 5000);
            log.info("editHook rsp {} {}", response.code, response.content);
            return new Gson().fromJson(response.content, GitWebhook.class);
        } catch (Exception e) {
            log.error("editHook {}", e.getMessage());
            return null;
        }
    }

    public Boolean deleteHook(GitWebhook gitWebhook, String token) {
        if (gitWebhook == null || StringUtils.isEmpty(gitWebhook.getId()) || StringUtils.isEmpty(gitWebhook.getHook_id())) {
            return null;
        }
        String url = GIT_API_URL + "projects/" + gitWebhook.getId() + "/hooks/" + gitWebhook.getHook_id();
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("PRIVATE-TOKEN", token);
            HttpResult response = HttpClientV6.httpDelete(url, headers, Maps.newHashMap(), "UTF-8", 5000);
            log.info("deleteHook rsp {} {}", response.code, response.content);
            return true;
        } catch (Exception e) {
            log.error("deleteHook {}", e.getMessage());
            return false;
        }
    }

    public String getProjectByAddress(String gitHost, String groupName, String projectName, String token) {
        groupName = groupName.trim();
        projectName = projectName.trim();
        String url = gitHost + GIT_API_URI + "projects?search=" + groupName + "/" + projectName + "&search_namespaces=true";
        log.info("getProjectByAddress url:{}", url);
        Map<String, String> headers = new HashMap<>(1);
        headers.put("PRIVATE-TOKEN", token);

        return HttpClientV2.get(url, headers, 10000);
    }
}
