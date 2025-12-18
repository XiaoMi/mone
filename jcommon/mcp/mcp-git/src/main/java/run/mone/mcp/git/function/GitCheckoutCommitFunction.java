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
 * Git Checkout Commit Function
 *
 * 用于切换到指定的commit
 *
 * @author generated
 * @date 2025-12-08
 */
@Data
@Slf4j
@Component
public class GitCheckoutCommitFunction implements McpFunction {

    @Autowired
    private GitService gitService;

    /**
     * Function名称
     */
    private String name = "git_checkout_commit";

    /**
     * Function描述
     */
    private String desc = "切换Git仓库到指定的commit";

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
                    "commitId": {
                        "type": "string",
                        "description": "提交ID（完整或短SHA）"
                    }
                },
                "required": ["localPath", "commitId"]
            }
            """;

    @Override
    public String getToolScheme() {
        return this.chaosToolSchema;
    }

    @Override
    @ReportCallCount(businessName = "git-checkout-commit", description = "切换Git仓库到指定的commit")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                // 获取参数
                String localPath = getStringParam(args, "localPath");
                String commitId = getStringParam(args, "commitId");

                // 验证必填参数
                if (localPath.isEmpty()) {
                    log.warn("localPath 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：localPath不能为空")), true));
                }

                if (commitId.isEmpty()) {
                    log.warn("commitId 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：commitId不能为空")), true));
                }

                log.info("开始切换到commit，localPath: {}, commitId: {}", localPath, commitId);

                // 执行切换操作
                GitResponse response = gitService.checkoutCommit(localPath, commitId);

                // 构建结果消息
                if (response.getSuccess()) {
                    String resultMsg = String.format("成功切换到commit: %s", commitId);
                    log.info("成功切换到commit: {}", commitId);
                    return createSuccessFlux(resultMsg);
                } else {
                    log.error("切换commit失败: {}", response.getError());
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("切换commit失败：" + response.getError())), true));
                }

            } catch (Exception e) {
                log.error("切换commit操作失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("切换commit失败：" + e.getMessage())), true));
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
