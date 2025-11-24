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
 * Git Push工具
 *
 * 用于推送本地提交到远程仓库
 *
 * @author generated
 * @date 2025-11-13
 */
@Slf4j
@Component
public class GitPushTool implements ITool {

    public static final String name = "git_push";

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
                推送本地提交到远程Git仓库。该工具用于同步本地更改到远程服务器。

                **使用场景：**
                - 推送本地提交到远程仓库
                - 同步代码到团队共享仓库
                - 备份代码到远程服务器

                **功能特性：**
                - 支持指定远程仓库名称（默认origin）
                - 可选择推送特定分支或所有分支
                - 支持用户名/Token认证

                **注意事项：**
                - 本地路径必须是有效的Git仓库
                - 推送前确保已完成commit
                - 对于私有仓库需要提供认证信息
                - 如果不指定分支，将推送所有分支
                """;
    }

    @Override
    public String parameters() {
        return """
                - localPath: (必填) Git仓库本地路径
                - remote: (可选) 远程仓库名称，默认为origin
                - branch: (可选) 要推送的分支名称，不指定则推送所有分支
                - username: (可选) 认证用户名
                - token: (可选) 认证Token
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
                <git_push>
                <localPath>本地仓库路径</localPath>
                <remote>远程仓库名称（可选，默认origin）</remote>
                <branch>分支名称（可选）</branch>
                %s
                </git_push>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 推送到默认远程仓库
                <git_push>
                <localPath>/tmp/git-workspace/my-project</localPath>
                </git_push>

                示例 2: 推送指定分支
                <git_push>
                <localPath>/tmp/git-workspace/my-project</localPath>
                <branch>main</branch>
                </git_push>

                示例 3: 推送到私有仓库
                <git_push>
                <localPath>/home/user/projects/app</localPath>
                <remote>origin</remote>
                <branch>develop</branch>
                </git_push>

                示例 4: 推送所有分支
                <git_push>
                <localPath>/workspace/project</localPath>
                <remote>origin</remote>
                </git_push>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("localPath") || StringUtils.isBlank(inputJson.get("localPath").getAsString())) {
                log.error("git_push 操作缺少必填参数 localPath");
                result.addProperty("error", "缺少必填参数 'localPath'");
                result.addProperty("success", false);
                return result;
            }

            // 获取参数
            String localPath = inputJson.get("localPath").getAsString().trim();
            String remote = inputJson.has("remote") ? inputJson.get("remote").getAsString().trim() : "origin";
            String branch = inputJson.has("branch") ? inputJson.get("branch").getAsString().trim() : null;
            String username = inputJson.has("username") ? inputJson.get("username").getAsString().trim() : null;
            String token = inputJson.has("token") ? inputJson.get("token").getAsString().trim() : null;

            log.info("开始推送到远程仓库，localPath: {}, remote: {}, branch: {}", localPath, remote, branch);

            // 执行推送操作
            GitResponse response = gitService.gitPush(localPath, remote, branch, username, token);

            // 设置响应
            if (response.getSuccess()) {
                result.addProperty("success", true);
                result.addProperty("message", response.getMessage());
                result.addProperty("remote", remote);
                if (branch != null) {
                    result.addProperty("branch", branch);
                }
                log.info("成功推送到远程仓库: {}", remote);
            } else {
                result.addProperty("success", false);
                result.addProperty("error", response.getError());
                log.error("推送失败: {}", response.getError());
            }

            return result;

        } catch (Exception e) {
            log.error("执行 git_push 操作时发生异常", e);
            result.addProperty("error", "推送失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
