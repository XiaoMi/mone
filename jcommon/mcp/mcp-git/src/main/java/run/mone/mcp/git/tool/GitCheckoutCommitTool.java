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
 * Git Checkout Commit工具
 *
 * 用于切换到指定的commit
 *
 * @author generated
 * @date 2025-11-13
 */
@Slf4j
@Component
public class GitCheckoutCommitTool implements ITool {

    public static final String name = "git_checkout_commit";

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
                切换Git仓库到指定的commit。该工具用于检出特定的提交版本。

                **使用场景：**
                - 回退到历史版本
                - 查看特定提交的代码状态
                - 进行版本对比和调试

                **功能特性：**
                - 精确定位到指定commit
                - 支持完整或短commit ID
                - 自动切换工作区状态

                **注意事项：**
                - commitId不能为空
                - 本地路径必须是有效的Git仓库
                - 执行前确保工作区没有未提交的更改
                - 切换后将处于detached HEAD状态
                """;
    }

    @Override
    public String parameters() {
        return """
                - localPath: (必填) Git仓库本地路径
                - commitId: (必填) 提交ID（完整或短SHA）
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
                <git_checkout_commit>
                <localPath>本地仓库路径</localPath>
                <commitId>提交ID</commitId>
                %s
                </git_checkout_commit>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 切换到指定commit（完整SHA）
                <git_checkout_commit>
                <localPath>/tmp/git-workspace/my-project</localPath>
                <commitId>a1b2c3d4e5f6789012345678901234567890abcd</commitId>
                </git_checkout_commit>

                示例 2: 切换到指定commit（短SHA）
                <git_checkout_commit>
                <localPath>/tmp/git-workspace/my-project</localPath>
                <commitId>a1b2c3d</commitId>
                </git_checkout_commit>

                示例 3: 回退到历史版本
                <git_checkout_commit>
                <localPath>/home/user/projects/app</localPath>
                <commitId>1234567</commitId>
                </git_checkout_commit>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("localPath") || StringUtils.isBlank(inputJson.get("localPath").getAsString())) {
                log.error("git_checkout_commit 操作缺少必填参数 localPath");
                result.addProperty("error", "缺少必填参数 'localPath'");
                result.addProperty("success", false);
                return result;
            }

            if (!inputJson.has("commitId") || StringUtils.isBlank(inputJson.get("commitId").getAsString())) {
                log.error("git_checkout_commit 操作缺少必填参数 commitId");
                result.addProperty("error", "缺少必填参数 'commitId'");
                result.addProperty("success", false);
                return result;
            }

            // 获取参数
            String localPath = inputJson.get("localPath").getAsString().trim();
            String commitId = inputJson.get("commitId").getAsString().trim();

            log.info("开始切换到commit，localPath: {}, commitId: {}", localPath, commitId);

            // 执行切换操作
            GitResponse response = gitService.checkoutCommit(localPath, commitId);

            // 设置响应
            if (response.getSuccess()) {
                result.addProperty("success", true);
                result.addProperty("message", response.getMessage());
                result.addProperty("commitId", commitId);
                log.info("成功切换到commit: {}", commitId);
            } else {
                result.addProperty("success", false);
                result.addProperty("error", response.getError());
                log.error("切换commit失败: {}", response.getError());
            }

            return result;

        } catch (Exception e) {
            log.error("执行 git_checkout_commit 操作时发生异常", e);
            result.addProperty("error", "切换commit失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
