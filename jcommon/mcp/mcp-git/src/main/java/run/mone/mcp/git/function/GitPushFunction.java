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
 * Git Push Function
 *
 * 用于推送本地提交到远程仓库
 *
 * @author generated
 * @date 2025-12-08
 */
@Data
@Slf4j
@Component
public class GitPushFunction implements McpFunction {

    @Autowired
    private GitService gitService;

    /**
     * Function名称
     */
    private String name = "git_push";

    /**
     * Function描述
     */
    private String desc = "推送本地提交到远程Git仓库";

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
                    "remote": {
                        "type": "string",
                        "description": "远程仓库名称，默认为origin"
                    },
                    "branch": {
                        "type": "string",
                        "description": "要推送的分支名称，不指定则推送所有分支"
                    },
                    "username": {
                        "type": "string",
                        "description": "认证用户名（可选）"
                    },
                    "token": {
                        "type": "string",
                        "description": "认证Token（可选）"
                    }
                },
                "required": ["localPath"]
            }
            """;

    @Override
    public String getToolScheme() {
        return this.chaosToolSchema;
    }

    @Override
    @ReportCallCount(businessName = "git-push", description = "推送本地提交到远程Git仓库")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                // 获取参数
                String localPath = getStringParam(args, "localPath");
                String remote = getStringParam(args, "remote");
                String branch = getStringParam(args, "branch");
                String username = getStringParam(args, "username");
                String token = getStringParam(args, "token");

                // 验证必填参数
                if (localPath.isEmpty()) {
                    log.warn("localPath 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：localPath不能为空")), true));
                }

                // 设置默认值
                if (remote.isEmpty()) {
                    remote = "origin";
                }

                log.info("开始推送到远程仓库，localPath: {}, remote: {}, branch: {}", localPath, remote, branch);

                // 执行推送操作
                GitResponse response = gitService.gitPush(localPath, remote,
                        branch.isEmpty() ? null : branch,
                        username.isEmpty() ? null : username,
                        token.isEmpty() ? null : token);

                // 构建结果消息
                if (response.getSuccess()) {
                    String resultMsg = String.format("成功推送到远程仓库: %s%s",
                            remote,
                            branch.isEmpty() ? "" : ", 分支: " + branch);
                    log.info("成功推送到远程仓库: {}", remote);
                    return createSuccessFlux(resultMsg);
                } else {
                    log.error("推送失败: {}", response.getError());
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("推送失败：" + response.getError())), true));
                }

            } catch (Exception e) {
                log.error("推送操作失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("推送失败：" + e.getMessage())), true));
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
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false),
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("[DONE]")), false)
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
