package run.mone.mcp.git.function;

import com.google.gson.Gson;
import com.xiaomi.youpin.gitlab.Gitlab;
import com.xiaomi.youpin.gitlab.bo.BaseResponse;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private static final Gson gson = new Gson();

    private String name = "gitlab_executor";

    private String desc = "Execute GitLab operations (MR, push, pull,create branch...)";

    private String defaultGroupName = "your-default-GroupName";

    private String gitLabToolSchema = """
            {
                "type": "object",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": ["create_repository", "search_repositories","get_branch","create_branch","delete_branch","push","create_merge","get_merge","accept_merge","close_merge"],
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
                    },
                    "git_path": {
                         "type": "string",
                         "description": "Which git path to operate example: push,pull..."
                    },
                    "git_commit_message": {
                         "type": "string",
                         "description": "Which git commit message"
                    },
                    "source_branch": {
                         "type": "string",
                         "description": "source branch when merge used"
                    },
                     "target_branch": {
                         "type": "string",
                         "description": "target branch when merge used"
                    },
                    "merge_title": {
                         "type": "string",
                         "description": "merge title when merge used"
                    },
                    "merge_id": {
                         "type": "string",
                         "description": "merge id after create merged"
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
                case "push":
                    return executePush((String) args.get("git_path"), (String) args.get("git_commit_message"));
                case "create_merge":
                    return executeCreateMerge((String) args.get("source_branch"), (String) args.get("target_branch"), (String) args.get("merge_title"), (String) args.get("project_id"));
                case "get_merge":
                    return executeGetMerge((String) args.get("project_id"), (String) args.get("merge_id"));
                case "accept_merge":
                    return executeAcceptMerge((String) args.get("project_id"), (String) args.get("merge_id"));
                case "close_merge":
                    return executeCloseMerge((String) args.get("project_id"), (String) args.get("merge_id"));
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

    public McpSchema.CallToolResult executePush(String gitPath, String gitCommitMessage) {
        try {
            // 如果gitPath为空，则默认使用使用当前目录
            if (gitPath == null) {
                gitPath = ".";
            }
            // 如果gitCommitMessage为空，则默认使用使用当前时间
            if (gitCommitMessage == null) {
                gitCommitMessage = "git mcp commit :" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            String username = System.getenv().getOrDefault("GIT_USERNAME", "");
            String token = System.getenv().getOrDefault("GIT_TOKEN", "");
            boolean pushRes = gitlab.addCommitPush(username, token, gitCommitMessage, gitPath);
            if (pushRes) {
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Push successful")),
                        false);
            } else {
                throw new RuntimeException("Push failed");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public McpSchema.CallToolResult executeCreateMerge(String sourceBranch, String targetBranch, String mergeTitle, String projectId) {
        try {
            if (projectId == null) {
                throw new IllegalArgumentException("projectId is required");
            }
            if (sourceBranch == null) {
                throw new IllegalArgumentException("sourceBranch is required");
            }
            if (targetBranch == null) {
                throw new IllegalArgumentException("targetBranch is required");
            }
            if (mergeTitle == null) {
                mergeTitle = "git mcp merge :" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            String token = System.getenv().getOrDefault("GIT_TOKEN", "");
            BaseResponse mergeRes = gitlab.createMerge(gitLabUrl, projectId, sourceBranch, targetBranch, mergeTitle, token);
            if (mergeRes.getCode() / 100 == 2) {
                // 获取mergeId,
                /*
                {"id":2616238,"iid":10,"project_id":61851,"title":"Merge zxw_mcp into staging-jdk21","description":null,"state":"opened","created_at":"2025-02-08T16:56:09.798+08:00","updated_at":"2025-02-08T16:56:09.798+08:00","merged_by":null,"merged_at":null,"closed_by":null,"closed_at":null,"target_branch":"staging-jdk21","source_branch":"zxw_mcp","user_notes_count":0,"upvotes":0,"downvotes":0,"author":{"id":19628,"username":"mione-xm","name":"mione-xm","state":"active","avatar_url":"https://git.n.xiaomi.com/api/user/getAvatarurlnocheck?user_name=mione-xm\u0026checkSign=50480f6a66800d493b9c288bf8c43dd5","web_url":"https://git.n.xiaomi.com/mione-xm"},"assignees":[],"assignee":null,"reviewers":[],"source_project_id":61851,"target_project_id":61851,"labels":[],"draft":false,"work_in_progress":false,"milestone":null,"merge_when_pipeline_succeeds":false,"merge_status":"checking","sha":"8fcd0a31ba368916507482921d8831f29258ad3d","merge_commit_sha":null,"squash_commit_sha":null,"discussion_locked":null,"should_remove_source_branch":null,"force_remove_source_branch":null,"reference":"!10","references":{"short":"!10","relative":"!10","full":"youpin-gateway/zxw_test2!10"},"web_url":"https://git.n.xiaomi.com/youpin-gateway/zxw_test2/-/merge_requests/10","time_stats":{"time_estimate":0,"total_time_spent":0,"human_time_estimate":null,"human_total_time_spent":null},"squash":false,"task_completion_status":{"count":0,"completed_count":0},"has_conflicts":false,"blocking_discussions_resolved":true,"subscribed":true,"changes_count":"1","latest_build_started_at":null,"latest_build_finished_at":null,"first_deployed_to_production_at":null,"pipeline":null,"head_pipeline":null,"diff_refs":{"base_sha":"3593691ff2178ad1db8195954f367e4d88f5c01c","head_sha":"8fcd0a31ba368916507482921d8831f29258ad3d","start_sha":"6a6d2032181c3c3ab277fd27bcb670a0b2378775"},"merge_error":null,"user":{"can_merge":true}}
                */
                // mergeRes.getMessage()转为gson 获取iid为mergeId
                
                Map<String, Object> mergeMap = gson.fromJson(mergeRes.getMessage(), Map.class);
                String mergeId = mergeMap.get("iid").toString();
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("mergeId:" + mergeId)),
                        false);
            } else {
                throw new RuntimeException(mergeRes.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public McpSchema.CallToolResult executeGetMerge(String projectId, String mergeId) {
        try {
            if (projectId == null) {
                throw new IllegalArgumentException("projectId is required");
            }
            if (mergeId == null) {
                throw new IllegalArgumentException("mergeId is required");
            }
            String token = System.getenv().getOrDefault("GIT_TOKEN", "");
            BaseResponse mergeRes = gitlab.getMerge(gitLabUrl, projectId, mergeId, token);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(mergeRes.getMessage())),
                    false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public McpSchema.CallToolResult executeAcceptMerge(String projectId, String mergeId) {
        try {
            if (projectId == null) {
                throw new IllegalArgumentException("projectId is required");
            }
            if (mergeId == null) {
                throw new IllegalArgumentException("mergeId is required");
            }
            String token = System.getenv().getOrDefault("GIT_TOKEN", "");
            BaseResponse mergeRes = gitlab.acceptMerge(gitLabUrl, projectId, mergeId, token);
            if (mergeRes.getCode() / 100 == 2) {
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Merge accepted successfully")),
                        false);
            } else {
                throw new RuntimeException(mergeRes.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public McpSchema.CallToolResult executeCloseMerge(String projectId, String mergeId) {
        try {
            if (projectId == null) {
                throw new IllegalArgumentException("projectId is required");
            }
            if (mergeId == null) {
                throw new IllegalArgumentException("mergeId is required");
            }
            String token = System.getenv().getOrDefault("GIT_TOKEN", "");
            BaseResponse mergeRes = gitlab.closeMerge(gitLabUrl, projectId, mergeId, token);
            if (mergeRes.getCode() / 100 == 2) {
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Merge closed successfully")),
                        false);
            } else {
                throw new RuntimeException(mergeRes.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
