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
 * Git Commit工具
 *
 * 用于提交Git仓库的更改
 *
 * @author generated
 * @date 2025-11-13
 */
@Slf4j
@Component
public class GitCommitTool implements ITool {

    public static final String name = "git_commit";

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
                提交Git仓库的所有更改。该工具会自动添加所有更改到暂存区并提交。

                **使用场景：**
                - 保存代码更改
                - 记录开发进度
                - 创建版本历史记录

                **功能特性：**
                - 自动添加所有更改（git add .）
                - 支持自定义提交消息
                - 如果未提供消息，使用默认消息

                **注意事项：**
                - 本地路径必须是有效的Git仓库
                - 会提交工作区的所有更改
                - 提交消息应该清晰描述更改内容
                - 建议在提交前检查更改内容
                """;
    }

    @Override
    public String parameters() {
        return """
                - localPath: (必填) Git仓库本地路径
                - message: (必填) 提交消息，描述本次更改内容
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
                <git_commit>
                <localPath>本地仓库路径</localPath>
                <message>提交消息</message>
                %s
                </git_commit>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 提交代码更改
                <git_commit>
                <localPath>/tmp/git-workspace/my-project</localPath>
                <message>feat: add new feature for user authentication</message>
                </git_commit>

                示例 2: 修复bug提交
                <git_commit>
                <localPath>/home/user/projects/app</localPath>
                <message>fix: resolve null pointer exception in login module</message>
                </git_commit>

                示例 3: 文档更新提交
                <git_commit>
                <localPath>/workspace/docs</localPath>
                <message>docs: update API documentation</message>
                </git_commit>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("localPath") || StringUtils.isBlank(inputJson.get("localPath").getAsString())) {
                log.error("git_commit 操作缺少必填参数 localPath");
                result.addProperty("error", "缺少必填参数 'localPath'");
                result.addProperty("success", false);
                return result;
            }

            if (!inputJson.has("message") || StringUtils.isBlank(inputJson.get("message").getAsString())) {
                log.error("git_commit 操作缺少必填参数 message");
                result.addProperty("error", "缺少必填参数 'message'");
                result.addProperty("success", false);
                return result;
            }

            // 获取参数
            String localPath = inputJson.get("localPath").getAsString().trim();
            String message = inputJson.get("message").getAsString().trim();

            log.info("开始提交更改，localPath: {}, message: {}", localPath, message);

            // 执行提交操作
            GitResponse response = gitService.gitCommit(localPath, message);

            // 设置响应
            if (response.getSuccess()) {
                result.addProperty("success", true);
                result.addProperty("message", response.getMessage());
                result.addProperty("commitMessage", message);
                log.info("成功提交更改，message: {}", message);
            } else {
                result.addProperty("success", false);
                result.addProperty("error", response.getError());
                log.error("提交更改失败: {}", response.getError());
            }

            return result;

        } catch (Exception e) {
            log.error("执行 git_commit 操作时发生异常", e);
            result.addProperty("error", "提交更改失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
