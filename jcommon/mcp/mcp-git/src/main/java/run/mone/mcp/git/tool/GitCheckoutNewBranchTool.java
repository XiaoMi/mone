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
 * Git Checkout New Branch工具
 *
 * 用于基于已有分支创建新分支，新分支名称格式为code-fix-yyyyMMdd-{uuid}
 * UUID短格式（8位）用于防止同一天创建多个分支时发生冲突
 *
 * @author generated
 * @date 2025-12-03
 */
@Slf4j
@Component
public class GitCheckoutNewBranchTool implements ITool {

    public static final String name = "git_checkout_new_branch";

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
                基于已有分支创建并切换到新分支。新分支的名称自动生成为code-fix-yyyyMMdd-{uuid}格式。

                **使用场景：**
                - 基于现有分支创建代码修复分支
                - 开始新的开发任务前创建工作分支
                - 从主分支派生新的特性分支

                **功能特性：**
                - 自动生成规范的分支名称（code-fix-yyyyMMdd-{uuid}）
                - UUID短格式（8位）防止同一天多次创建分支时冲突
                - 基于源分支创建新分支
                - 自动切换到新创建的分支
                - 返回新分支的名称供后续使用

                **注意事项：**
                - sourceBranch必须存在
                - 本地路径必须是有效的Git仓库
                - 每次创建的分支名称都是唯一的（包含UUID）
                """;
    }

    @Override
    public String parameters() {
        return """
                - localPath: (必填) Git仓库本地路径
                - sourceBranch: (必填) 源分支名称，新分支将基于此分支创建
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
                <git_checkout_new_branch>
                <localPath>本地仓库路径</localPath>
                <sourceBranch>源分支名称</sourceBranch>
                %s
                </git_checkout_new_branch>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 基于master分支创建修复分支
                <git_checkout_new_branch>
                <localPath>/tmp/git-workspace/my-project</localPath>
                <sourceBranch>master</sourceBranch>
                </git_checkout_new_branch>

                示例 2: 基于develop分支创建修复分支
                <git_checkout_new_branch>
                <localPath>/home/user/projects/app</localPath>
                <sourceBranch>develop</sourceBranch>
                </git_checkout_new_branch>

                示例 3: 基于release分支创建修复分支
                <git_checkout_new_branch>
                <localPath>/workspace/service</localPath>
                <sourceBranch>release/v1.0</sourceBranch>
                </git_checkout_new_branch>

                成功后会返回新分支的名称，例如: code-fix-20251203-a1b2c3d4
                注意：UUID部分每次都不同，确保分支名称唯一性
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("localPath") || StringUtils.isBlank(inputJson.get("localPath").getAsString())) {
                log.error("git_checkout_new_branch 操作缺少必填参数 localPath");
                result.addProperty("error", "缺少必填参数 'localPath'");
                result.addProperty("success", false);
                return result;
            }

            if (!inputJson.has("sourceBranch") || StringUtils.isBlank(inputJson.get("sourceBranch").getAsString())) {
                log.error("git_checkout_new_branch 操作缺少必填参数 sourceBranch");
                result.addProperty("error", "缺少必填参数 'sourceBranch'");
                result.addProperty("success", false);
                return result;
            }

            // 获取参数
            String localPath = inputJson.get("localPath").getAsString().trim();
            String sourceBranch = inputJson.get("sourceBranch").getAsString().trim();

            log.info("开始创建新分支，localPath: {}, sourceBranch: {}", localPath, sourceBranch);

            // 执行创建分支操作
            GitResponse response = gitService.checkoutNewBranch(localPath, sourceBranch);

            // 设置响应
            if (response.getSuccess()) {
                result.addProperty("success", true);
                result.addProperty("message", response.getMessage());
                result.addProperty("newBranchName", response.getData().toString());
                log.info("成功创建新分支: {}", response.getData());
            } else {
                result.addProperty("success", false);
                result.addProperty("error", response.getError());
                log.error("创建新分支失败: {}", response.getError());
            }

            return result;

        } catch (Exception e) {
            log.error("执行 git_checkout_new_branch 操作时发生异常", e);
            result.addProperty("error", "创建新分支失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
