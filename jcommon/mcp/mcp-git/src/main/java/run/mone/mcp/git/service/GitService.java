package run.mone.mcp.git.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.mcp.git.model.GitResponse;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Git操作服务类
 *
 * 提供Git基础操作功能
 *
 * @author generated
 * @date 2025-11-13
 */
@Service
@Slf4j
public class GitService {

    @Value("${git.default.username:}")
    private String defaultUsername;

    @Value("${git.default.token:}")
    private String defaultToken;

    @Value("${git.email.suffix}")
    private String emailSuffix;

    /**
     * Git Clone操作
     *
     * @param repositoryUrl Git仓库URL
     * @param branchName 分支名称，默认为main
     * @param username 用户名，可选
     * @param token 认证Token，可选
     * @param workspacePath 工作区路径，从ReactorRole获取
     * @return GitResponse
     */
    public GitResponse gitClone(String repositoryUrl, String branchName, String username, String token, String workspacePath) {
        log.info("Git clone operation - repositoryUrl: {}, branchName: {}, workspacePath: {}",
                repositoryUrl, branchName, workspacePath);

        if (StringUtils.isBlank(repositoryUrl)) {
            return GitResponse.error("Repository URL is required");
        }

        // 设置默认分支
        if (StringUtils.isBlank(branchName)) {
            branchName = "main";
        }

        // 设置本地路径
        String projectName = extractProjectName(repositoryUrl);
        // 使用传入的workspacePath，如果为空则使用当前目录
        String workspace = StringUtils.isNotBlank(workspacePath) ? workspacePath : System.getProperty("user.dir");
        String localPath = workspace + File.separator + projectName;

        File localDir = new File(localPath);
        if (localDir.exists()) {
            return GitResponse.error("Local path already exists: " + localPath);
        }

        try {
            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(repositoryUrl)
                    .setDirectory(localDir)
                    .setBranch("refs/heads/" + branchName);

            // 设置认证
            if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(token)) {
                cloneCommand.setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(username, token));
            } else if (StringUtils.isNotBlank(defaultUsername) && StringUtils.isNotBlank(defaultToken)) {
                cloneCommand.setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(defaultUsername, defaultToken));
            }

            Git git = cloneCommand.call();
            git.close();

            log.info("Successfully cloned repository to: {}", localPath);
            return GitResponse.success("Repository cloned successfully", localPath);

        } catch (GitAPIException e) {
            log.error("Failed to clone repository", e);
            return GitResponse.error("Failed to clone repository: " + e.getMessage());
        }
    }

    /**
     * 切换到指定的commit
     *
     * @param localPath Git仓库本地路径
     * @param commitId 提交ID
     * @return GitResponse
     */
    public GitResponse checkoutCommit(String localPath, String commitId) {
        log.info("Checkout commit operation - localPath: {}, commitId: {}", localPath, commitId);

        if (StringUtils.isBlank(localPath)) {
            return GitResponse.error("Local path is required");
        }

        if (StringUtils.isBlank(commitId)) {
            return GitResponse.error("Commit ID is required");
        }

        try {
            Repository repository = openRepository(localPath);
            Git git = new Git(repository);

            CheckoutCommand checkoutCommand = git.checkout()
                    .setName(commitId);

            checkoutCommand.call();
            git.close();

            log.info("Successfully checked out to commit: {}", commitId);
            return GitResponse.success("Checked out to commit: " + commitId);

        } catch (IOException e) {
            log.error("Failed to open repository", e);
            return GitResponse.error("Failed to open repository: " + e.getMessage());
        } catch (GitAPIException e) {
            log.error("Failed to checkout commit", e);
            return GitResponse.error("Failed to checkout commit: " + e.getMessage());
        }
    }

    /**
     * Git Commit操作
     *
     * @param localPath Git仓库本地路径
     * @param message 提交消息
     * @param authorName author名称，可选
     * @param authorEmail author邮箱，可选
     * @return GitResponse
     */
    public GitResponse gitCommit(String localPath, String message, String authorName, String authorEmail) {
        log.info("Git commit operation - localPath: {}, message: {}, authorName: {}, authorEmail: {}",
                localPath, message, authorName, authorEmail);

        if (StringUtils.isBlank(localPath)) {
            return GitResponse.error("Local path is required");
        }

        if (StringUtils.isBlank(message)) {
            message = "Automated commit";
        }

        try {
            Repository repository = openRepository(localPath);
            Git git = new Git(repository);

            // 添加所有更改
            git.add()
                    .addFilepattern(".")
                    .call();

            // 提交
            CommitCommand commitCommand = git.commit()
                    .setMessage(message);

            // 设置 author 信息
            if (StringUtils.isNotBlank(authorName) && StringUtils.isNotBlank(authorEmail)) {
                commitCommand.setAuthor(authorName, authorEmail);
                commitCommand.setCommitter(authorName, authorEmail);
                log.info("Set commit author: {} <{}>", authorName, authorEmail);
            } else {
                log.warn("Author information not provided, will use git config or system user");
            }

            commitCommand.call();
            git.close();

            log.info("Successfully committed changes with message: {}", message);
            return GitResponse.success("Changes committed successfully");

        } catch (IOException e) {
            log.error("Failed to open repository", e);
            return GitResponse.error("Failed to open repository: " + e.getMessage());
        } catch (GitAPIException e) {
            log.error("Failed to commit changes", e);
            return GitResponse.error("Failed to commit changes: " + e.getMessage());
        }
    }

    /**
     * Git Push操作
     *
     * @param localPath Git仓库本地路径
     * @param remote 远程仓库名称，默认为origin
     * @param branch 分支名称，可选
     * @param username 用户名，可选
     * @param token 认证Token，可选
     * @return GitResponse
     */
    public GitResponse gitPush(String localPath, String remote, String branch, String username, String token) {
        log.info("Git push operation - localPath: {}, remote: {}, branch: {}", localPath, remote, branch);

        if (StringUtils.isBlank(localPath)) {
            return GitResponse.error("Local path is required");
        }

        if (StringUtils.isBlank(remote)) {
            remote = "origin";
        }

        try {
            Repository repository = openRepository(localPath);
            Git git = new Git(repository);

            PushCommand pushCommand = git.push()
                    .setRemote(remote);

            if (StringUtils.isNotBlank(branch)) {
                pushCommand.add(branch);
            } else {
                pushCommand.setPushAll();
            }

            // 设置认证
            if (StringUtils.isNotBlank(defaultUsername) && StringUtils.isNotBlank(defaultToken)) {
                pushCommand.setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(defaultUsername, defaultToken));
            }

            pushCommand.call();
            git.close();

            log.info("Successfully pushed to remote: {}", remote);
            return GitResponse.success("Pushed to remote successfully");

        } catch (IOException e) {
            log.error("Failed to open repository", e);
            return GitResponse.error("Failed to open repository: " + e.getMessage());
        } catch (GitAPIException e) {
            log.error("Failed to push to remote", e);
            return GitResponse.error("Failed to push to remote: " + e.getMessage());
        }
    }

    /**
     * 基于已有分支checkout一个新分支
     *
     * @param localPath Git仓库本地路径
     * @param sourceBranch 源分支名称
     * @return GitResponse，包含新分支名称
     */
    public GitResponse checkoutNewBranch(String localPath, String sourceBranch) {
        log.info("Checkout new branch operation - localPath: {}, sourceBranch: {}", localPath, sourceBranch);

        if (StringUtils.isBlank(localPath)) {
            return GitResponse.error("Local path is required");
        }

        if (StringUtils.isBlank(sourceBranch)) {
            return GitResponse.error("Source branch is required");
        }

        try {
            Repository repository = openRepository(localPath);
            Git git = new Git(repository);

            // 生成新分支名称: code-fix-yyyyMMdd-{uuid-short}
            // 添加UUID短格式（取前8位）以防止同一天创建多个分支时冲突
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uuidShort = UUID.randomUUID().toString().substring(0, 5);
            String newBranchName = "code-fix-" + dateStr + "-" + uuidShort;

            // 切换到源分支
            git.checkout()
                    .setName(sourceBranch)
                    .call();

            // 基于源分支创建并切换到新分支
            git.checkout()
                    .setCreateBranch(true)
                    .setName(newBranchName)
                    .setStartPoint(sourceBranch)
                    .call();

            git.close();

            log.info("Successfully created and checked out new branch: {}", newBranchName);
            return GitResponse.success("Created new branch: " + newBranchName, newBranchName);

        } catch (IOException e) {
            log.error("Failed to open repository", e);
            return GitResponse.error("Failed to open repository: " + e.getMessage());
        } catch (GitAPIException e) {
            log.error("Failed to checkout new branch", e);
            return GitResponse.error("Failed to checkout new branch: " + e.getMessage());
        }
    }

    /**
     * 创建Merge Request
     *
     * @param repositoryUrl Git仓库URL
     * @param sourceBranch 源分支
     * @param targetBranch 目标分支
     * @param title MR标题
     * @param description MR描述
     * @return GitResponse，包含MR的URL
     */
    public GitResponse createMergeRequest(String repositoryUrl, String sourceBranch, String targetBranch,
                                          String title, String description) {
        log.info("Create merge request operation - repositoryUrl: {}, sourceBranch: {}, targetBranch: {}",
                repositoryUrl, sourceBranch, targetBranch);

        if (StringUtils.isBlank(repositoryUrl)) {
            return GitResponse.error("Repository URL is required");
        }

        if (StringUtils.isBlank(sourceBranch)) {
            return GitResponse.error("Source branch is required");
        }

        if (StringUtils.isBlank(targetBranch)) {
            return GitResponse.error("Target branch is required");
        }

        try {
            // 解析仓库信息
            String gitlabUrl = extractGitLabUrl(repositoryUrl);
            String projectPath = extractProjectPath(repositoryUrl);

            if (StringUtils.isBlank(gitlabUrl) || StringUtils.isBlank(projectPath)) {
                return GitResponse.error("Invalid repository URL format");
            }

            // 构建GitLab API URL
            String encodedProjectPath = projectPath.replace("/", "%2F");
            String apiUrl = gitlabUrl + "/api/v4/projects/" + encodedProjectPath + "/merge_requests";

            // 构建MR请求体
            String mrTitle = StringUtils.isNotBlank(title) ? title : "Merge " + sourceBranch + " into " + targetBranch;
            String mrDescription = StringUtils.isNotBlank(description) ? description : "Automated merge request";

            String requestBody = String.format(
                    "{\"source_branch\":\"%s\",\"target_branch\":\"%s\",\"title\":\"%s\",\"description\":\"%s\"}",
                    sourceBranch, targetBranch, mrTitle, mrDescription
            );

            // 调用GitLab API创建MR
            String mrUrl = createMergeRequestViaApi(apiUrl, requestBody, defaultToken);

            if (StringUtils.isNotBlank(mrUrl)) {
                log.info("Successfully created merge request: {}", mrUrl);
                return GitResponse.success("Merge request created successfully", mrUrl);
            } else {
                return GitResponse.error("Failed to create merge request");
            }

        } catch (Exception e) {
            log.error("Failed to create merge request", e);
            return GitResponse.error("Failed to create merge request: " + e.getMessage());
        }
    }

    /**
     * 通过API创建Merge Request
     */
    private String createMergeRequestViaApi(String apiUrl, String requestBody, String token) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("PRIVATE-TOKEN", token)
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                // 使用Gson解析响应获取MR的web_url
                String responseBody = response.body();
                log.info("GitLab API response: {}", responseBody);

                try {
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

                    // 获取根对象的web_url字段（第二个web_url，第一个在author对象中）
                    if (jsonObject != null && jsonObject.has("web_url")) {
                        String webUrl = jsonObject.get("web_url").getAsString();
                        log.info("Extracted merge request URL: {}", webUrl);
                        return webUrl;
                    } else {
                        log.error("Response JSON does not contain web_url field");
                    }
                } catch (Exception jsonException) {
                    log.error("Failed to parse JSON response", jsonException);
                }
            } else {
                log.error("Failed to create merge request, status code: {}, response: {}",
                        response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Exception when calling GitLab API", e);
        }
        return null;
    }

    /**
     * 从仓库URL中提取GitLab服务器地址
     */
    private String extractGitLabUrl(String repositoryUrl) {
        try {
            // 移除.git后缀
            String url = repositoryUrl.endsWith(".git") ?
                    repositoryUrl.substring(0, repositoryUrl.length() - 4) : repositoryUrl;

            // 处理SSH和HTTPS格式
            if (url.startsWith("git@")) {
                // git@gitlab.com:group/project.git -> https://gitlab.com
                int colonIndex = url.indexOf(":");
                String host = url.substring(4, colonIndex);
                return "https://" + host;
            } else if (url.startsWith("http")) {
                // https://gitlab.com/group/project -> https://gitlab.com
                java.net.URI uri = new java.net.URI(url);
                return uri.getScheme() + "://" + uri.getHost();
            }
        } catch (Exception e) {
            log.error("Failed to extract GitLab URL", e);
        }
        return null;
    }

    /**
     * 从仓库URL中提取项目路径
     */
    private String extractProjectPath(String repositoryUrl) {
        try {
            // 移除.git后缀
            String url = repositoryUrl.endsWith(".git") ?
                    repositoryUrl.substring(0, repositoryUrl.length() - 4) : repositoryUrl;

            // 处理SSH和HTTPS格式
            if (url.startsWith("git@")) {
                // git@gitlab.com:group/project -> group/project
                int colonIndex = url.indexOf(":");
                return url.substring(colonIndex + 1);
            } else if (url.startsWith("http")) {
                // https://gitlab.com/group/project -> group/project
                java.net.URI uri = new java.net.URI(url);
                String path = uri.getPath();
                return path.startsWith("/") ? path.substring(1) : path;
            }
        } catch (Exception e) {
            log.error("Failed to extract project path", e);
        }
        return null;
    }

    /**
     * 打开Git仓库
     */
    private Repository openRepository(String repoPath) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder.setGitDir(new File(repoPath + "/.git"))
                .readEnvironment()
                .findGitDir()
                .build();
    }

    /**
     * 从URL中提取项目名称
     */
    private String extractProjectName(String url) {
        String name = url;
        if (name.endsWith(".git")) {
            name = name.substring(0, name.length() - 4);
        }
        int lastSlash = name.lastIndexOf('/');
        if (lastSlash >= 0) {
            name = name.substring(lastSlash + 1);
        }
        return name;
    }

    /**
     * 获取邮箱后缀配置
     */
    public String getEmailSuffix() {
        return emailSuffix;
    }
}
