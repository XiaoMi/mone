package run.mone.mcp.git.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.git.model.GitResponse;
import run.mone.mcp.git.service.GitService;

import java.util.List;
import java.util.Map;

/**
 * Git Commit Function
 *
 * 用于提交Git仓库的更改
 *
 * @author generated
 * @date 2025-12-08
 */
@Data
@Slf4j
@Component
public class GitCommitFunction implements McpFunction {

    @Autowired
    private GitService gitService;

    /**
     * Function名称
     */
    private String name = "git_commit";

    /**
     * Function描述
     */
    private String desc = "提交Git仓库的所有更改";

    /**
     * Function参数Schema定义
     */
    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "localPath": {
                        "type": "string",
                        "description": "Git仓库本地路径"
                    },
                    "message": {
                        "type": "string",
                        "description": "提交消息，描述本次更改内容"
                    },
                    "gitName": {
                        "type": "string",
                        "description": "Git提交的作者名称（可选）"
                    }
                },
                "required": ["localPath", "message"]
            }
            """;

    @Override
    public String getToolScheme() {
        return this.chaosToolSchema;
    }

    @Override
    @ReportCallCount(businessName = "git-commit", description = "提交Git仓库的所有更改")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                // 获取参数
                String localPath = getStringParam(args, "localPath");
                String message = getStringParam(args, "message");
                String gitName = getStringParam(args, "gitName");

                // 验证必填参数
                if (localPath.isEmpty()) {
                    log.warn("localPath 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：localPath不能为空")), true));
                }

                if (message.isEmpty()) {
                    log.warn("message 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：message不能为空")), true));
                }

                // 获取 git 作者信息
                String gitEmail = null;
                if (!gitName.isEmpty()) {
                    // 拼接邮箱：gitName + 邮箱后缀
                    gitEmail = gitName + gitService.getEmailSuffix();
                    log.info("使用 gitName: {}, 拼接后的 gitEmail: {}", gitName, gitEmail);
                }

                log.info("开始提交更改，localPath: {}, message: {}, gitName: {}, gitEmail: {}",
                        localPath, message, gitName, gitEmail);

                // 执行提交操作
                GitResponse response = gitService.gitCommit(localPath, message,
                        gitName.isEmpty() ? null : gitName,
                        gitEmail);

                // 构建结果消息
                if (response.getSuccess()) {
                    String resultMsg = String.format("成功提交更改，message: %s%s",
                            message,
                            !gitName.isEmpty() ? ", author: " + gitName + " <" + gitEmail + ">" : "");
                    log.info("成功提交更改，message: {}", message);
                    return createSuccessFlux(resultMsg);
                } else {
                    log.error("提交更改失败: {}", response.getError());
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("提交更改失败：" + response.getError())), true));
                }

            } catch (Exception e) {
                log.error("提交更改操作失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("提交更改失败：" + e.getMessage())), true));
            }
        });
    }

    /**
     * 创建成功响应的Flux
     * @param result 操作结果
     * @return 包含结果和完成标记的Flux
     */
    private Flux<McpSchema.CallToolResult> createSuccessFlux(String result) {
        return Flux.just(
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false)
        );
    }

    /**
     * 安全地从参数映射中获取字符串参数
     * @param params 参数映射
     * @param key 参数键
     * @return 字符串参数值，如果不存在则返回空字符串
     */
    private String getStringParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value != null ? value.toString() : "";
    }
}
