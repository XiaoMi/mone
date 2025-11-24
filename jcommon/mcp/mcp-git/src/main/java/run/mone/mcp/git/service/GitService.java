package run.mone.mcp.git.service;

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
     * @param localPath 本地路径，可选
     * @param username 用户名，可选
     * @param token 认证Token，可选
     * @param workspacePath 工作区路径，从ReactorRole获取
     * @return GitResponse
     */
    public GitResponse gitClone(String repositoryUrl, String branchName, String localPath, String username, String token, String workspacePath) {
        log.info("Git clone operation - repositoryUrl: {}, branchName: {}, localPath: {}, workspacePath: {}",
                repositoryUrl, branchName, localPath, workspacePath);

        if (StringUtils.isBlank(repositoryUrl)) {
            return GitResponse.error("Repository URL is required");
        }

        // 设置默认分支
        if (StringUtils.isBlank(branchName)) {
            branchName = "main";
        }

        // 设置本地路径
        if (StringUtils.isBlank(localPath)) {
            String projectName = extractProjectName(repositoryUrl);
            // 使用传入的workspacePath，如果为空则使用当前目录
            String workspace = StringUtils.isNotBlank(workspacePath) ? workspacePath : System.getProperty("user.dir");
            localPath = workspace + File.separator + projectName;
        }

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
