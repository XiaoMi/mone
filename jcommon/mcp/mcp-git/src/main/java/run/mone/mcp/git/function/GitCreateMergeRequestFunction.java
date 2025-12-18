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
 * Git Create Merge Request Function
 *
 * 用于创建Merge Request（合并请求），供开发人员进行代码审查
 *
 * @author generated
 * @date 2025-12-08
 */
@Data
@Slf4j
@Component
public class GitCreateMergeRequestFunction implements McpFunction {

    @Autowired
    private GitService gitService;

    /**
     * Function名称
     */
    private String name = "git_create_merge_request";

    /**
     * Function描述
     */
    private String desc = "创建GitLab Merge Request（合并请求）";

    /**
     * Function参数Schema定义
     */
    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "gitUrl": {
                        "type": "string",
                        "description": "Git仓库URL，例如: https://gitlab.com/group/project.git 或 git@gitlab.com:group/project.git"
                    },
                    "sourceBranch": {
                        "type": "string",
                        "description": "源分支名称（要合并的分支）"
                    },
                    "targetBranch": {
                        "type": "string",
                        "description": "目标分支名称（合并到的分支）"
                    },
                    "title": {
                        "type": "string",
                        "description": "MR标题（可选），不提供则自动生成"
                    },
                    "description": {
                        "type": "string",
                        "description": "MR描述（可选），不提供则使用默认描述"
                    }
                },
                "required": ["gitUrl", "sourceBranch", "targetBranch"]
            }
            """;

    @Override
    public String getToolScheme() {
        return this.chaosToolSchema;
    }

    @Override
    @ReportCallCount(businessName = "git-create-merge-request", description = "创建GitLab Merge Request")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                // 获取参数
                String gitUrl = getStringParam(args, "gitUrl");
                String sourceBranch = getStringParam(args, "sourceBranch");
                String targetBranch = getStringParam(args, "targetBranch");
                String title = getStringParam(args, "title");
                String description = getStringParam(args, "description");

                // 验证必填参数
                if (gitUrl.isEmpty()) {
                    log.warn("gitUrl 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：gitUrl不能为空")), true));
                }

                if (sourceBranch.isEmpty()) {
                    log.warn("sourceBranch 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：sourceBranch不能为空")), true));
                }

                if (targetBranch.isEmpty()) {
                    log.warn("targetBranch 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：targetBranch不能为空")), true));
                }

                log.info("开始创建Merge Request，gitUrl: {}, sourceBranch: {}, targetBranch: {}",
                        gitUrl, sourceBranch, targetBranch);

                // 执行创建MR操作
                GitResponse response = gitService.createMergeRequest(
                        gitUrl, sourceBranch, targetBranch,
                        title.isEmpty() ? null : title,
                        description.isEmpty() ? null : description);

                // 构建结果消息
                if (response.getSuccess()) {
                    String mergeRequestUrl = response.getData() != null ? response.getData().toString() : "未知";
                    String resultMsg = String.format("成功创建Merge Request\n源分支: %s\n目标分支: %s\nMR URL: %s",
                            sourceBranch, targetBranch, mergeRequestUrl);
                    log.info("成功创建Merge Request: {}", mergeRequestUrl);
                    return createSuccessFlux(resultMsg);
                } else {
                    log.error("创建Merge Request失败: {}", response.getError());
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("创建Merge Request失败：" + response.getError())), true));
                }

            } catch (Exception e) {
                log.error("创建Merge Request操作失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("创建Merge Request失败：" + e.getMessage())), true));
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
