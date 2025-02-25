import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.git.function.GitHubFunction;

import java.util.Map;


public class GitHubTest {

    private static final Gson gson = new Gson();

    // 测试创建仓库
    @Test
    public void testCreateRepository() {
        GitHubFunction gitHubFunction = new GitHubFunction();
        gitHubFunction.apply(Map.of(
            "type", "create_repository",
            "repository_name", "test-repo-0220",
            "organization", "wangmin9"
        ));
    }

    // 测试搜索仓库
    @Test
    public void testSearchRepository() {
        GitHubFunction gitHubFunction = new GitHubFunction();
        McpSchema.CallToolResult result = gitHubFunction.apply(Map.of(
            "type", "search_repositories",
            "repository_name", "test-repo-0220"
        ));

        System.out.println("Create Event Result: " + gson.toJson(result));
    }

    // 测试获取分支
    @Test
    public void testGetBranch() {
        GitHubFunction gitHubFunction = new GitHubFunction();
        gitHubFunction.apply(Map.of(
            "type", "get_branch",
            "repository_name", "test-repo",
            "organization", "your-org-name",
            "branch", "main"
        ));
    }

    // 测试创建分支
    @Test
    public void testCreateBranch() {
        GitHubFunction gitHubFunction = new GitHubFunction();
        gitHubFunction.apply(Map.of(
            "type", "create_branch",
            "repository_name", "test-repo",
            "organization", "your-org-name",
            "branch", "feature-branch",
            "base_branch", "main"
        ));
    }

    // 测试删除分支
    @Test
    public void testDeleteBranch() {
        GitHubFunction gitHubFunction = new GitHubFunction();
        gitHubFunction.apply(Map.of(
            "type", "delete_branch",
            "repository_name", "test-repo",
            "organization", "your-org-name",
            "branch", "feature-branch"
        ));
    }

    // 测试push
    @Test
    public void testPush() {
        GitHubFunction gitHubFunction = new GitHubFunction();
        gitHubFunction.apply(Map.of(
            "type", "push",
            "repository_name", "test-repo",
            "organization", "your-org-name",
            "branch", "main",
            "commit_message", "test commit"
        ));
    }

    // 测试创建PR
    @Test
    public void testCreatePullRequest() {
        GitHubFunction gitHubFunction = new GitHubFunction();
        gitHubFunction.apply(Map.of(
            "type", "create_pull_request",
            "repository_name", "test-repo",
            "organization", "your-org-name",
            "branch", "feature-branch",
            "base_branch", "main",
            "pull_request_title", "Test PR"
        ));
    }

    // 测试获取PR
    @Test
    public void testGetPullRequest() {
        GitHubFunction gitHubFunction = new GitHubFunction();
        gitHubFunction.apply(Map.of(
            "type", "get_pull_request",
            "repository_name", "test-repo",
            "organization", "your-org-name",
            "pull_request_number", 1
        ));
    }

    // 测试合并PR
    @Test
    public void testMergePullRequest() {
        GitHubFunction gitHubFunction = new GitHubFunction();
        gitHubFunction.apply(Map.of(
            "type", "merge_pull_request",
            "repository_name", "test-repo",
            "organization", "your-org-name",
            "pull_request_number", 1
        ));
    }

    // 测试关闭PR
    @Test
    public void testClosePullRequest() {
        GitHubFunction gitHubFunction = new GitHubFunction();
        gitHubFunction.apply(Map.of(
            "type", "close_pull_request",
            "repository_name", "test-repo",
            "organization", "your-org-name",
            "pull_request_number", 1
        ));
    }

    // 测试clone
    @Test
    public void testClone() {
        GitHubFunction gitHubFunction = new GitHubFunction();
        gitHubFunction.apply(Map.of(
            "type", "clone",
            "clone_url", "https://github.com/wangmin9/test",
            "branch", "master",
            "git_path", "./target/test-repo"
        ));
    }
} 