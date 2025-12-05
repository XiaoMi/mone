package run.mone.mcp.git.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.git.model.GitResponse;
import run.mone.mcp.git.service.GitService;

/**
 * Git Create Merge Request工具
 *
 * 用于创建Merge Request（合并请求），供开发人员进行代码审查
 *
 * @author generated
 * @date 2025-12-03
 */
@Slf4j
@Component
public class GitCreateMergeRequestTool implements ITool {

    public static final String name = "git_create_merge_request";

    @Autowired
    private GitService gitService;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                创建GitLab Merge Request（合并请求），用于代码审查。创建成功后返回MR的URL地址。

                **使用场景：**
                - 代码开发完成后提交审查
                - 将特性分支合并到主分支
                - 将修复分支合并到发布分支
                - 需要团队成员进行代码审查

                **功能特性：**
                - 支持从源分支到目标分支创建MR
                - 自动生成或自定义MR标题和描述
                - 返回MR的URL供开发人员访问和审查
                - 支持GitLab认证

                **注意事项：**
                - 需要有效的GitLab认证Token
                - 源分支和目标分支必须存在于远程仓库
                - 源分支需要已经推送到远程仓库
                - 目前仅支持GitLab平台
                """;
    }

    @Override
    public String parameters() {
        return """
                - gitUrl: (必填) Git仓库URL，例如: https://gitlab.com/group/project.git 或 git@gitlab.com:group/project.git
                - sourceBranch: (必填) 源分支名称（要合并的分支）
                - targetBranch: (必填) 目标分支名称（合并到的分支）
                - title: (可选) MR标题，不提供则自动生成
                - description: (可选) MR描述，不提供则使用默认描述
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
                <task_progress>
                任务进度记录（可选）
                </task_progress>
                """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
                <git_create_merge_request>
                <gitUrl>仓库URL</gitUrl>
                <sourceBranch>源分支名称</sourceBranch>
                <targetBranch>目标分支名称</targetBranch>
                <title>MR标题（可选）</title>
                <description>MR描述（可选）</description>
                %s
                </git_create_merge_request>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 创建基本的MR（使用默认认证）
                <git_create_merge_request>
                <gitUrl>https://gitlab.com/mygroup/myproject.git</gitUrl>
                <sourceBranch>code-fix-20251203</sourceBranch>
                <targetBranch>master</targetBranch>
                </git_create_merge_request>

                示例 2: 创建带自定义标题和描述的MR
                <git_create_merge_request>
                <gitUrl>git@gitlab.company.com:team/service.git</gitUrl>
                <sourceBranch>feature/user-auth</sourceBranch>
                <targetBranch>develop</targetBranch>
                <title>feat: Add user authentication module</title>
                <description>This MR implements the user authentication feature including login, logout, and session management.</description>
                </git_create_merge_request>

                示例 3: 创建MR并指定认证信息
                <git_create_merge_request>
                <gitUrl>https://gitlab.example.com/org/repo.git</gitUrl>
                <sourceBranch>bugfix/login-error</sourceBranch>
                <targetBranch>release/v1.0</targetBranch>
                <title>fix: Resolve login error on mobile devices</title>
                </git_create_merge_request>

                成功后会返回MR的URL，例如: https://gitlab.com/mygroup/myproject/-/merge_requests/123
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("gitUrl") || StringUtils.isBlank(inputJson.get("gitUrl").getAsString())) {
                log.error("git_create_merge_request 操作缺少必填参数 gitUrl");
                result.addProperty("error", "缺少必填参数 'gitUrl'");
                result.addProperty("success", false);
                return result;
            }

            if (!inputJson.has("sourceBranch") || StringUtils.isBlank(inputJson.get("sourceBranch").getAsString())) {
                log.error("git_create_merge_request 操作缺少必填参数 sourceBranch");
                result.addProperty("error", "缺少必填参数 'sourceBranch'");
                result.addProperty("success", false);
                return result;
            }

            if (!inputJson.has("targetBranch") || StringUtils.isBlank(inputJson.get("targetBranch").getAsString())) {
                log.error("git_create_merge_request 操作缺少必填参数 targetBranch");
                result.addProperty("error", "缺少必填参数 'targetBranch'");
                result.addProperty("success", false);
                return result;
            }

            // 获取参数
            String gitUrl = inputJson.get("gitUrl").getAsString().trim();
            String sourceBranch = inputJson.get("sourceBranch").getAsString().trim();
            String targetBranch = inputJson.get("targetBranch").getAsString().trim();

            // 可选参数
            String title = inputJson.has("title") && !StringUtils.isBlank(inputJson.get("title").getAsString())
                    ? inputJson.get("title").getAsString().trim()
                    : null;

            String description = inputJson.has("description") && !StringUtils.isBlank(inputJson.get("description").getAsString())
                    ? inputJson.get("description").getAsString().trim()
                    : null;

            String username = inputJson.has("username") && !StringUtils.isBlank(inputJson.get("username").getAsString())
                    ? inputJson.get("username").getAsString().trim()
                    : null;

            String token = inputJson.has("token") && !StringUtils.isBlank(inputJson.get("token").getAsString())
                    ? inputJson.get("token").getAsString().trim()
                    : null;

            log.info("开始创建Merge Request，gitUrl: {}, sourceBranch: {}, targetBranch: {}",
                    gitUrl, sourceBranch, targetBranch);

            // 执行创建MR操作
            GitResponse response = gitService.createMergeRequest(
                    gitUrl, sourceBranch, targetBranch, title, description);

            // 设置响应
            if (response.getSuccess()) {
                result.addProperty("success", true);
                result.addProperty("message", response.getMessage());
                result.addProperty("mergeRequestUrl", response.getData().toString());
                log.info("成功创建Merge Request: {}", response.getData());
            } else {
                result.addProperty("success", false);
                result.addProperty("error", response.getError());
                log.error("创建Merge Request失败: {}", response.getError());
            }

            return result;

        } catch (Exception e) {
            log.error("执行 git_create_merge_request 操作时发生异常", e);
            result.addProperty("error", "创建Merge Request失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
