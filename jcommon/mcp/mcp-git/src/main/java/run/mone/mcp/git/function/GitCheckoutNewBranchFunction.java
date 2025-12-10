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
 * Git Checkout New Branch Function
 *
 * 用于基于已有分支创建新分支，新分支名称格式为code-fix-yyyyMMdd-{uuid}
 *
 * @author generated
 * @date 2025-12-08
 */
@Data
@Slf4j
@Component
public class GitCheckoutNewBranchFunction implements McpFunction {

    @Autowired
    private GitService gitService;

    /**
     * Function名称
     */
    private String name = "git_checkout_new_branch";

    /**
     * Function描述
     */
    private String desc = "基于已有分支创建并切换到新分支";

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
                    "sourceBranch": {
                        "type": "string",
                        "description": "源分支名称，新分支将基于此分支创建"
                    }
                },
                "required": ["localPath", "sourceBranch"]
            }
            """;

    @Override
    public String getToolScheme() {
        return this.chaosToolSchema;
    }

    @Override
    @ReportCallCount(businessName = "git-checkout-new-branch", description = "基于已有分支创建并切换到新分支")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                // 获取参数
                String localPath = getStringParam(args, "localPath");
                String sourceBranch = getStringParam(args, "sourceBranch");

                // 验证必填参数
                if (localPath.isEmpty()) {
                    log.warn("localPath 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：localPath不能为空")), true));
                }

                if (sourceBranch.isEmpty()) {
                    log.warn("sourceBranch 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：sourceBranch不能为空")), true));
                }

                log.info("开始创建新分支，localPath: {}, sourceBranch: {}", localPath, sourceBranch);

                // 执行创建分支操作
                GitResponse response = gitService.checkoutNewBranch(localPath, sourceBranch);

                // 构建结果消息
                if (response.getSuccess()) {
                    String newBranchName = response.getData() != null ? response.getData().toString() : "未知";
                    String resultMsg = String.format("成功创建新分支: %s (基于 %s)", newBranchName, sourceBranch);
                    log.info("成功创建新分支: {}", newBranchName);
                    return createSuccessFlux(resultMsg);
                } else {
                    log.error("创建新分支失败: {}", response.getError());
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("创建新分支失败：" + response.getError())), true));
                }

            } catch (Exception e) {
                log.error("创建新分支操作失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("创建新分支失败：" + e.getMessage())), true));
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
