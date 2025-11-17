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
 * Git Clone工具
 *
 * 用于克隆Git仓库到本地
 *
 * @author generated
 * @date 2025-11-13
 */
@Slf4j
@Component
public class GitCloneTool implements ITool {

    public static final String name = "git_clone";

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
                克隆Git仓库到本地。该工具支持指定分支克隆，并可配置认证信息。

                **使用场景：**
                - 克隆远程Git仓库到本地
                - 指定分支进行克隆
                - 支持私有仓库认证

                **功能特性：**
                - 支持HTTP/HTTPS协议
                - 可指定克隆分支（默认为main）
                - 支持用户名/Token认证
                - 可自定义本地存储路径

                **注意事项：**
                - repositoryUrl必须是有效的Git仓库地址
                - 如果本地路径已存在，克隆将失败
                - 对于私有仓库，需要提供认证信息
                """;
    }

    @Override
    public String parameters() {
        return """
                - repositoryUrl: (必填) Git仓库URL地址
                - branchName: (可选) 分支名称，默认为main
                - localPath: (可选) 本地存储路径
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
                <git_clone>
                <repositoryUrl>仓库URL</repositoryUrl>
                <branchName>分支名称（可选，默认main）</branchName>
                <localPath>本地路径（可选）</localPath>
                <username>用户名（可选）</username>
                <token>Token（可选）</token>
                %s
                </git_clone>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 克隆公开仓库
                <git_clone>
                <repositoryUrl>https://github.com/username/repo.git</repositoryUrl>
                </git_clone>

                示例 2: 克隆指定分支
                <git_clone>
                <repositoryUrl>https://github.com/username/repo.git</repositoryUrl>
                <branchName>develop</branchName>
                </git_clone>

                示例 3: 克隆私有仓库
                <git_clone>
                <repositoryUrl>https://gitlab.com/username/private-repo.git</repositoryUrl>
                <branchName>main</branchName>
                <username>your-username</username>
                <token>your-access-token</token>
                </git_clone>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("repositoryUrl") || StringUtils.isBlank(inputJson.get("repositoryUrl").getAsString())) {
                log.error("git_clone 操作缺少必填参数 repositoryUrl");
                result.addProperty("error", "缺少必填参数 'repositoryUrl'");
                result.addProperty("success", false);
                return result;
            }

            // 获取参数
            String repositoryUrl = inputJson.get("repositoryUrl").getAsString().trim();
            String branchName = inputJson.has("branchName") ? inputJson.get("branchName").getAsString().trim() : "main";
            String localPath = inputJson.has("localPath") ? inputJson.get("localPath").getAsString().trim() : null;
            String username = inputJson.has("username") ? inputJson.get("username").getAsString().trim() : null;
            String token = inputJson.has("token") ? inputJson.get("token").getAsString().trim() : null;

            log.info("开始克隆仓库，repositoryUrl: {}, branchName: {}", repositoryUrl, branchName);

            // 从ReactorRole获取workspacePath，如果为空则使用当前工作目录
            String workspacePath;
            if (role != null && StringUtils.isNotBlank(role.getWorkspacePath())) {
                workspacePath = role.getWorkspacePath();
            } else {
                workspacePath = System.getProperty("user.dir");
                log.debug("Role为空或workspacePath未设置，使用默认路径: {}", workspacePath);
            }
            log.info("使用workspacePath: {}", workspacePath);

            // 执行克隆操作
            GitResponse response = gitService.gitClone(repositoryUrl, branchName, localPath, username, token, workspacePath);

            // 设置响应
            if (response.getSuccess()) {
                result.addProperty("success", true);
                result.addProperty("message", response.getMessage());
                if (response.getData() != null) {
                    result.addProperty("localPath", response.getData().toString());
                }
                log.info("成功克隆仓库: {}", repositoryUrl);
            } else {
                result.addProperty("success", false);
                result.addProperty("error", response.getError());
                log.error("克隆仓库失败: {}", response.getError());
            }

            return result;

        } catch (Exception e) {
            log.error("执行 git_clone 操作时发生异常", e);
            result.addProperty("error", "克隆仓库失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
