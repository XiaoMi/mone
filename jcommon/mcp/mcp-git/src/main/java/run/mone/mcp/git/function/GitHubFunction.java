package run.mone.mcp.git.function;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class GitHubFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private GitHub github;
    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final Gson gson = new Gson();
    
    private String name = "github_executor";
    private String desc = "Execute GitHub operations (PR, push, pull, create branch...)";
    private String defaultOrganization = "your-default-organization";

    private String githubToolSchema = """
            {
                "type": "object",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": ["create_repository", "search_repositories", "get_branch", "create_branch", 
                                "delete_branch", "push", "create_pull_request", "get_pull_request", 
                                "merge_pull_request", "close_pull_request", "clone"],
                        "description": "Type of GitHub operation to execute"
                    },
                    "repository_name": {
                        "type": "string",
                        "description": "Repository name"
                    },
                    "organization": {
                        "type": "string",
                        "description": "Organization name, defaults to ${defaultOrganization}"
                    },
                    "branch": {
                        "type": "string",
                        "description": "Branch name"
                    },
                    "base_branch": {
                        "type": "string",
                        "description": "Base branch for operations"
                    },
                    "pull_request_title": {
                        "type": "string",
                        "description": "Title for pull request"
                    },
                    "pull_request_number": {
                        "type": "integer",
                        "description": "Pull request number"
                    },
                    "commit_message": {
                        "type": "string",
                        "description": "Commit message"
                    },
                    "clone_url": {
                        "type": "string",
                        "description": "URL for clone operation"
                    },
                    "git_path": {
                        "type": "string",
                        "description": "Target path for clone operation"
                    }
                },
                "required": ["type"]
            }
            """;

    public GitHubFunction() {
        try {
            String token = System.getenv().getOrDefault("GITHUB_TOKEN", "");
            if (token.isEmpty()) {
                throw new IllegalStateException("GITHUB_TOKEN environment variable is required");
            }
            github = new GitHubBuilder().withOAuthToken(token).build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize GitHub client", e);
        }
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String type = (String) args.get("type");

        try {
            return switch (type.toLowerCase()) {
                case "create_repository" -> executeCreateRepository(
                        (String) args.get("repository_name"),
                        (String) args.get("organization"));
                case "search_repositories" -> executeSearchRepositories(
                        (String) args.get("repository_name"));
                case "get_branch" -> executeGetBranch(
                        (String) args.get("repository_name"),
                        (String) args.get("organization"),
                        (String) args.get("branch"));
                case "create_branch" -> executeCreateBranch(
                        (String) args.get("repository_name"),
                        (String) args.get("organization"),
                        (String) args.get("branch"),
                        (String) args.get("base_branch"));
                case "create_pull_request" -> executeCreatePullRequest(
                        (String) args.get("repository_name"),
                        (String) args.get("organization"),
                        (String) args.get("branch"),
                        (String) args.get("base_branch"),
                        (String) args.get("pull_request_title"));
                case "delete_branch" -> executeDeleteBranch(
                        (String) args.get("repository_name"),
                        (String) args.get("organization"),
                        (String) args.get("branch"));
                case "get_pull_request" -> executeGetPullRequest(
                        (String) args.get("repository_name"),
                        (String) args.get("organization"),
                        (Integer) args.get("pull_request_number"));
                case "merge_pull_request" -> executeMergePullRequest(
                        (String) args.get("repository_name"),
                        (String) args.get("organization"),
                        (Integer) args.get("pull_request_number"));
                case "close_pull_request" -> executeClosePullRequest(
                        (String) args.get("repository_name"),
                        (String) args.get("organization"),
                        (Integer) args.get("pull_request_number"));
                case "push" -> executePush(
                        (String) args.get("repository_name"),
                        (String) args.get("organization"),
                        (String) args.get("branch"),
                        (String) args.get("commit_message"));
                case "clone" -> executeClone(
                        (String) args.get("clone_url"),
                        (String) args.get("branch"),
                        (String) args.get("git_path"));
                default -> throw new IllegalArgumentException("Unsupported operation type: " + type);
            };
        } catch (Exception e) {
            throw new RuntimeException("GitHub operation failed: " + e.getMessage(), e);
        }
    }

    private McpSchema.CallToolResult executeCreateRepository(String repoName, String organization) throws IOException {
        if (repoName == null) {
            throw new IllegalArgumentException("repository_name is required");
        }
        
        organization = organization != null ? organization : defaultOrganization;
        
        GHRepository repository;
        try {
            // 首先尝试作为组织创建
            GHOrganization org = github.getOrganization(organization);
            repository = org.createRepository(repoName)
                    .private_(true)
                    .create();
        } catch (GHFileNotFoundException e) {
            // 如果不是组织，则在个人账号下创建
            repository = github.createRepository(repoName)
                    .private_(true)
                    .create();
        }
        
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Repository created: " + repository.getHtmlUrl())),
                false);
    }

    private McpSchema.CallToolResult executeSearchRepositories(String repoName) throws IOException {
        GHRepositorySearchBuilder search = github.searchRepositories()
                .q(repoName);
        
        StringBuilder result = new StringBuilder();
        int count = 0;
        final int MAX_RESULTS = 20;
        
        try {
            PagedSearchIterable<GHRepository> searchResults = search.list();
            for (GHRepository repo : searchResults.withPageSize(10)) {
                try {
                    result.append(repo.getFullName())
                          .append(" - ")
                          .append(repo.getHtmlUrl())
                          .append("\n");
                          
                    count++;
                    if (count >= MAX_RESULTS) {
                        break;
                    }
                } catch (Exception e) {
                    log.warn("Error processing repository: " + e.getMessage());
                }
            }
            
            if (searchResults.getTotalCount() > MAX_RESULTS) {
                result.append("\n... and ").append(searchResults.getTotalCount() - MAX_RESULTS)
                      .append(" more repositories");
            }
        } catch (Exception e) {
            if (e.getMessage().contains("secondary rate limit")) {
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(
                                "Rate limit exceeded. Please try again in a few minutes. " +
                                "Results so far:\n" + result)),
                        false);
            }
            throw e;
        }
        
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(result.toString())),
                false);
    }

    private McpSchema.CallToolResult executeCreateBranch(
            String repoName, String organization, String branchName, String baseBranch) throws IOException {
        if (repoName == null || branchName == null) {
            throw new IllegalArgumentException("repository_name and branch are required");
        }
        
        organization = organization != null ? organization : defaultOrganization;
        baseBranch = baseBranch != null ? baseBranch : "main";
        
        GHRepository repository = github.getRepository(organization + "/" + repoName);
        GHRef baseRef = repository.getRef("heads/" + baseBranch);
        repository.createRef("refs/heads/" + branchName, baseRef.getObject().getSha());
        
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Branch created: " + branchName)),
                false);
    }

    private McpSchema.CallToolResult executeCreatePullRequest(
            String repoName, String organization, String head, String base, String title) throws IOException {
        if (repoName == null || head == null || base == null) {
            throw new IllegalArgumentException("repository_name, head branch, and base branch are required");
        }
        
        organization = organization != null ? organization : defaultOrganization;
        title = title != null ? title : "Pull request created at " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        GHRepository repository = github.getRepository(organization + "/" + repoName);
        GHPullRequest pr = repository.createPullRequest(title, head, base, "Automated pull request");
        
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Pull request created: #" + pr.getNumber())),
                false);
    }

    private McpSchema.CallToolResult executeDeleteBranch(
            String repoName, String organization, String branchName) throws IOException {
        if (repoName == null || branchName == null) {
            throw new IllegalArgumentException("repository_name and branch are required");
        }
        
        organization = organization != null ? organization : defaultOrganization;
        
        GHRepository repository = github.getRepository(organization + "/" + repoName);
        GHRef ref = repository.getRef("heads/" + branchName);
        ref.delete();
        
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Branch deleted: " + branchName)),
                false);
    }

    private McpSchema.CallToolResult executeGetPullRequest(
            String repoName, String organization, Integer prNumber) throws IOException {
        if (repoName == null || prNumber == null) {
            throw new IllegalArgumentException("repository_name and pull_request_number are required");
        }
        
        organization = organization != null ? organization : defaultOrganization;
        
        GHRepository repository = github.getRepository(organization + "/" + repoName);
        GHPullRequest pr = repository.getPullRequest(prNumber);
        
        Map<String, Object> prInfo = Map.of(
            "number", pr.getNumber(),
            "title", pr.getTitle(),
            "state", pr.getState().name(),
            "base", pr.getBase().getRef(),
            "head", pr.getHead().getRef(),
            "mergeable", pr.getMergeable() != null ? pr.getMergeable() : false
        );
        
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(gson.toJson(prInfo))),
                false);
    }

    private McpSchema.CallToolResult executeMergePullRequest(
            String repoName, String organization, Integer prNumber) throws IOException {
        if (repoName == null || prNumber == null) {
            throw new IllegalArgumentException("repository_name and pull_request_number are required");
        }
        
        organization = organization != null ? organization : defaultOrganization;
        
        GHRepository repository = github.getRepository(organization + "/" + repoName);
        GHPullRequest pr = repository.getPullRequest(prNumber);
        
        if (!pr.getMergeable()) {
            throw new IllegalStateException("Pull request is not mergeable");
        }
        
        pr.merge("Merged via GitHub Function");
        
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Pull request merged successfully")),
                false);
    }

    private McpSchema.CallToolResult executeClosePullRequest(
            String repoName, String organization, Integer prNumber) throws IOException {
        if (repoName == null || prNumber == null) {
            throw new IllegalArgumentException("repository_name and pull_request_number are required");
        }
        
        organization = organization != null ? organization : defaultOrganization;
        
        GHRepository repository = github.getRepository(organization + "/" + repoName);
        GHPullRequest pr = repository.getPullRequest(prNumber);
        pr.close();
        
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Pull request closed successfully")),
                false);
    }

    private McpSchema.CallToolResult executePush(
            String repoName, String organization, String branch, String commitMessage) throws IOException {
        if (repoName == null) {
            throw new IllegalArgumentException("repository_name is required");
        }
        
        organization = organization != null ? organization : defaultOrganization;
        branch = branch != null ? branch : "main";
        commitMessage = commitMessage != null ? commitMessage : 
                "Automated commit " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // 注意：这里使用JGit来实现本地git操作
        // 实际实现需要根据具体的git操作库来完成
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "git", "add", ".",
                "&&", "git", "commit", "-m", commitMessage,
                "&&", "git", "push", "origin", branch
            );
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Changes pushed successfully")),
                        false);
            } else {
                throw new RuntimeException("Push failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Push operation interrupted", e);
        }
    }

    private McpSchema.CallToolResult executeClone(String cloneUrl, String branch, String gitPath) throws IOException {
        if (cloneUrl == null) {
            throw new IllegalArgumentException("clone_url is required");
        }
        
        branch = branch != null ? branch : "main";
        gitPath = gitPath != null ? gitPath : ".";  // 如果没指定路径，默认为当前目录
        
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "git", "clone", "-b", branch, cloneUrl, gitPath  // 添加gitPath作为目标目录
            );
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Repository cloned successfully to: " + gitPath)),
                        false);
            } else {
                throw new RuntimeException("Clone failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Clone operation interrupted", e);
        }
    }

    private McpSchema.CallToolResult executeGetBranch(
            String repoName, String organization, String branchName) throws IOException {
        if (repoName == null || branchName == null) {
            throw new IllegalArgumentException("repository_name and branch are required");
        }
        
        organization = organization != null ? organization : defaultOrganization;
        
        GHRepository repository = github.getRepository(organization + "/" + repoName);
        GHBranch branch = repository.getBranch(branchName);
        
        Map<String, Object> branchInfo = Map.of(
            "name", branch.getName(),
            "sha", branch.getSHA1(),
            "protected", branch.isProtected()
        );
        
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(gson.toJson(branchInfo))),
                false);
    }
}
