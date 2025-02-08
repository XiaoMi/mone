package run.mone.mcp.git.function;

import com.xiaomi.youpin.gitlab.Gitlab;
import com.xiaomi.youpin.gitlab.bo.BaseResponse;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class GitLabFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private Gitlab gitlab;

    private static final String gitLabUrl = "https://git.xxx.com";

    private static final String createReposotoryPath = "/projects";

    private static final String searchRepositoriesPath = "/search/repositories";

    private String name = "gitlab_executor";

    private String desc = "Execute GitLab operations (MR, push, pull,create branch...)";

    private String defaultGroupName = "your-default-GroupName";

    private String gitLabToolSchema = """
            {
                "type": "object",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": ["create_repository", "search_repositories","get_branch","create_branch","delete_branch"],
                        "description": "Type of gitlab operation to execute"
                     },
                     "create_repository_name": {
                         "type": "string",
                         "description": "Repository name"
                    },
                    "create_repository_groupName": {
                         "type": "string",
                         "description": "Whether the repository groupName default is ${defaultGroupName}"
                    },
                    "search_repositories_groupName": {
                         "type": "string",
                         "description": "Whether the repository groupName default is ${defaultGroupName}"
                    },
                    "search_repositories_name": {
                         "type": "string",
                         "description": "search Repository name"
                    },
                    "branch": {
                         "type": "string",
                         "description": "which git branch"
                    },
                    "project_id": {
                         "type": "string",
                         "description": "which git project id"
                    },
                    "branch_ref": {
                         "type": "string",
                         "description": "Which branch should be checked out when creating a branch?"
                    }
                },
                "required": ["type"]
            }
            """;

    public GitLabFunction() {
        gitlab = new Gitlab();
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String type = (String) args.get("type");

        try {
            switch (type.toLowerCase()) {
                case "create_repository":
                    return executeCreateRepository((String) args.get("create_repository_name"),
                            (String) args.get("create_repository_groupName"));
                case "search_repositories":
                    return executeSearchRepositories((String) args.get("search_repositories_groupName"),
                            (String) args.get("search_repositories_name"));
                case "get_branch":
                    return executeGetBranch((String) args.get("branch"), (String) args.get("project_id"));
                case "create_branch":
                    return executeCreateBranch((String) args.get("branch"), (String) args.get("project_id"), (String) args.get("branch_ref"));
                case "delete_branch":
                    return executeDeleteBranch((String) args.get("branch"), (String) args.get("project_id"));
                default:
                    throw new IllegalArgumentException("Unsupported operation type: " + type);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public McpSchema.CallToolResult executeCreateRepository(String createRepositoryName,
            String createRepositoryGroupName) {
        if (createRepositoryGroupName == null) {
            createRepositoryGroupName = defaultGroupName;
        }
        String url = gitLabUrl + "/" + createRepositoryGroupName + "/" + createRepositoryName;
        try {
            String username = System.getenv().getOrDefault("GIT_USERNAME", "");
            String token = System.getenv().getOrDefault("GIT_TOKEN", "");
            boolean projectRes = gitlab.createEmptyProject(url, username, token);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(String.valueOf(projectRes))),
                    false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public McpSchema.CallToolResult executeSearchRepositories(String searchRepositoriesGroupName,
            String searchRepositoriesName) {
        if (searchRepositoriesGroupName == null) {
            searchRepositoriesGroupName = defaultGroupName;
        }
        try {
            String token = System.getenv().getOrDefault("GIT_TOKEN", "");
            String projectByAddress = gitlab.getProjectByAddress(gitLabUrl, searchRepositoriesGroupName,
                    searchRepositoriesName, token);
            // System.out.println(projectByAddress);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(projectByAddress)),
                    false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public McpSchema.CallToolResult executeGetBranch(String branchName, String projectId) {
        try {
            if (projectId == null) {
                throw new IllegalArgumentException("projectId is required");
            }
            if (branchName == null) {
                throw new IllegalArgumentException("branchName is required");
            }
            String token = System.getenv().getOrDefault("GIT_TOKEN", "");
            String branchRes = gitlab.getBranchInfo(gitLabUrl, projectId, branchName, token);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(branchRes)),
                    false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public McpSchema.CallToolResult executeCreateBranch(String branchName, String projectId, String branchRef) {
        try {
            String token = System.getenv().getOrDefault("GIT_TOKEN", "");
            BaseResponse branchRes = gitlab.createBranch(gitLabUrl, projectId, branchName, branchRef, token);
            if (branchRes.getCode() / 100 == 2) {
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(branchRes.getMessage())),
                        false);
            } else {
                throw new RuntimeException(branchRes.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public McpSchema.CallToolResult executeDeleteBranch(String branchName, String projectId) {
        try {
            if (projectId == null) {
                throw new IllegalArgumentException("projectId is required");
            }
            if (branchName == null) {
                throw new IllegalArgumentException("branchName is required");
            }
            String token = System.getenv().getOrDefault("GIT_TOKEN", "");
            BaseResponse branchRes = gitlab.deleteBranch(gitLabUrl, projectId, branchName, token);
            if (branchRes.getCode() / 100 == 2) {
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(branchRes.getMessage())),
                        false);
            } else {
                throw new RuntimeException(branchRes.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
