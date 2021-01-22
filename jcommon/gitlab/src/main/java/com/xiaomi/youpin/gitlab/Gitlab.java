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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.youpin.gitlab.bo.*;
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
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.xiaomi.youpin.gitlab.GitlabConst.GIT_API_URL;
import static com.xiaomi.youpin.gitlab.GitlabConst.GIT_BASE;

/**
 * @author gaoyibo
 */
@Slf4j
public class Gitlab {

    private static final String version = "0.0.2:2020-01-15";
    private static final String GIT_PUSH_PATH = "/tmp/git/push/";
    private static final String username="username";
    private static final String password=".";

    public Gitlab(String gitlabBaseUrl) {
        if (StringUtils.isBlank(gitlabBaseUrl)) {
            this.gitlabApiUrl = GIT_API_URL;
        } else {
            this.gitlabApiUrl = gitlabBaseUrl;
        }
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

    public String fetchFile(String projectId, String token, String path, String branch) {
        String encodePath = encodePath(path);
        String url = this.gitlabApiUrl + "projects/" + projectId + "/repository/files/" + encodePath + "/raw" + "?ref=" + branch;

        Map<String, String> headers = new HashMap<>(1);
        headers.put("PRIVATE-TOKEN", token);

        return HttpClientV2.get(url, headers, 10000);
    }

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

    public List<GitlabProject> fetchProjects(String token, Map<String, String> params) {
        List<GitlabProject> list;
        StringBuilder sb = new StringBuilder(gitlabApiUrl + "projects");
        setParams(params, sb);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            list = new Gson().fromJson(HttpClientV2.get(sb.toString(), headers, 10000), new TypeToken<List<GitlabProject>>(){}.getType());
        } catch (JsonSyntaxException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    public GitlabProject fetchProject(String projectId, String token, Map<String, String> params) {
        GitlabProject gitlabProject;
        StringBuilder sb = new StringBuilder(gitlabApiUrl + "projects/" + projectId);
        setParams(params, sb);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            gitlabProject = new Gson().fromJson(HttpClientV2.get(sb.toString(), headers, 10000), new TypeToken<GitlabProject>(){}.getType());
        } catch (JsonSyntaxException e) {
            gitlabProject = null;
        }
        return gitlabProject;
    }

    public List<GitlabBranch> fetchBranches(String projectId, String token, Map<String, String> params) {
        List<GitlabBranch> list;
        StringBuilder url = new StringBuilder(gitlabApiUrl + "projects/" + projectId + "/repository/branches");
        setParams(params, url);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            list = new Gson().fromJson(HttpClientV2.get(url.toString(), headers, 10000), new TypeToken<List<GitlabBranch>>(){}.getType());
            return list;
        } catch (JsonSyntaxException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    public List<GitlabGroup> getGroups (String token, Map<String, String> params) throws UnsupportedEncodingException {
        List<GitlabGroup> list;
        StringBuilder url = new StringBuilder(gitlabApiUrl + "groups");
        setParams(params, url);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            list = new Gson().fromJson(HttpClientV2.get(url.toString(), headers, 10000), new TypeToken<List<GitlabGroup>>(){}.getType());
        } catch (JsonSyntaxException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    public List<GitlabCommit> fetchCommits (String projectId, String branch, String token, Map<String, String> params) {
        List<GitlabCommit> list;
        StringBuilder url = new StringBuilder(gitlabApiUrl + "projects/" + projectId + "/repository/commits?ref_name=" + branch);
        setParams(params, url);
        Map<String, String> headers = new HashMap(1);
        headers.put("PRIVATE-TOKEN", token);
        try {
            list = new Gson().fromJson(HttpClientV2.get(url.toString(), headers, 10000), new TypeToken<List<GitlabCommit>>(){}.getType());
            list.stream().parallel().forEach(it -> {
                it.setCommitted_date(conversionTime(it.getCommitted_date()));
            });
        } catch (JsonSyntaxException e) {
            list = new ArrayList<>();
        }
        return list;
    }

    public GitlabCommit fetchCommit (String projectId, String sha, String token) {
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

    public GroupInfo getNamespacesById (String id, String token) {
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
        createNewProject("https://git.n.xiaomi.com/youpin-gateway/test3","fdfd","test","UxJjhx5VMG9e6TmZdcXE");
//        new Gitlab("").getUserByToken("UxJjhx5VMG9e6TmZdcXE");
//        checkPermissionMoreThan(40, "UxJjhx5VMG9e6TmZdcXE", "youpin-gateway");
    }

    private static void createProject(int groupId, String projectName, String token) {
        String createRepositoryUrl = GIT_API_URL + "projects?private_token=" + token;
        String body = "{\"name\": \"%s\",\"namespace_id\": %d}";
        body = String.format(body, projectName, groupId);
        Map<String, String> headers = new HashMap<>();
        headers.put("host", GIT_BASE);
        headers.put("Content-Type", "application/json");
        HttpClientV2.post(createRepositoryUrl, body, headers, 10000);
    }


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
            log.error("user is not in this group "+ groupName);
            return null;
        }
        GroupInfo groupInfo = groups.stream().filter(e -> e.getPath().equals(groupName)).findAny().orElse(null);
        if (groupInfo == null) {
            log.error("user is not in this group "+ groupName);
            return null;
        }
        return groupInfo.getId();
    }

    public static boolean createNewProject(String gitUrl, String path, String username, String token) throws IOException, GitAPIException {

        log.info("createProject version:{} url:{} path:{}", version, gitUrl, path);

        if (StringUtils.isEmpty(gitUrl)|| StringUtils.isEmpty(path)||StringUtils.isEmpty(username)||StringUtils.isEmpty(token)) {
            return false;
        }
        String[] strArr = gitUrl.split("/");
        String groupName = strArr[strArr.length - 2];
        String projectName = strArr[strArr.length - 1];

        Integer groupId = getGroupIdIfExist(token,groupName);
        if (groupId == null) {
            return false;
        }

        try {
            createProject(groupId, projectName, token);
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
        commit.setCommitter("bot", "bot@xxxxx.com").setMessage("Add all project template files ...");
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
     *
     * 格式：2016-09-03T00:00:00.000+08:00
     *
     * @param time
     * @return
     */
    private String conversionTime(String time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat df1 = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(time);
            Date date1 =  df1.parse(date.toString());
            return df2.format(date1);
        } catch (ParseException e) {
            log.warn("Gitlab#conversionByTimeZone exception: {}", e.getMessage());
            return time;
        }
    }

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
            log.error("token error: "+userToken);
        }

        int userAccessLevel = getAccessLevel(groupId, user.getId(), userToken);
        return userAccessLevel >= level ? true : false;
    }

    private static int getAccessLevel(Integer groupId, Long userId, String token) {
        if (groupId == null || userId == null||StringUtils.isEmpty(token)) {
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
}
