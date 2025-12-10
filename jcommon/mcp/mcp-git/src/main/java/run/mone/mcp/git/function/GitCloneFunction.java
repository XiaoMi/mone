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
 * Git Clone Function
 *
 * 用于克隆Git仓库到本地
 *
 * @author generated
 * @date 2025-12-08
 */
@Data
@Slf4j
@Component
public class GitCloneFunction implements McpFunction {

    @Autowired
    private GitService gitService;

    /**
     * Function名称
     */
    private String name = "git_clone";

    /**
     * Function描述
     */
    private String desc = "克隆Git仓库到本地";

    /**
     * Function参数Schema定义
     */
    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "repositoryUrl": {
                        "type": "string",
                        "description": "Git仓库URL地址"
                    },
                    "branchName": {
                        "type": "string",
                        "description": "分支名称，默认为main"
                    },
                    "username": {
                        "type": "string",
                        "description": "认证用户名（可选）"
                    },
                    "token": {
                        "type": "string",
                        "description": "认证Token（可选）"
                    },
                    "workspacePath": {
                        "type": "string",
                        "description": "工作空间路径（可选）"
                    }
                },
                "required": ["repositoryUrl"]
            }
            """;

    @Override
    public String getToolScheme() {
        return this.chaosToolSchema;
    }

    @Override
    @ReportCallCount(businessName = "git-clone", description = "克隆Git仓库到本地")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                // 获取参数
                String repositoryUrl = getStringParam(args, "repositoryUrl");
                String branchName = getStringParam(args, "branchName");
                String username = getStringParam(args, "username");
                String token = getStringParam(args, "token");
                String workspacePath = getStringParam(args, "workspacePath");

                // 验证必填参数
                if (repositoryUrl.isEmpty()) {
                    log.warn("repositoryUrl 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：repositoryUrl不能为空")), true));
                }

                // 设置默认值
                if (branchName.isEmpty()) {
                    branchName = "main";
                }

                if (workspacePath.isEmpty()) {
                    workspacePath = System.getProperty("user.dir");
                    log.debug("workspacePath未设置，使用默认路径: {}", workspacePath);
                }

                log.info("开始克隆仓库，repositoryUrl: {}, branchName: {}", repositoryUrl, branchName);

                // 执行克隆操作
                GitResponse response = gitService.gitClone(repositoryUrl, branchName,
                        username.isEmpty() ? null : username,
                        token.isEmpty() ? null : token,
                        workspacePath);

                // 构建结果消息
                if (response.getSuccess()) {
                    String resultMsg = String.format("成功克隆仓库: %s, 分支: %s%s",
                            repositoryUrl,
                            branchName,
                            response.getData() != null ? ", 本地路径: " + response.getData() : "");
                    log.info("成功克隆仓库: {}", repositoryUrl);
                    return createSuccessFlux(resultMsg);
                } else {
                    log.error("克隆仓库失败: {}", response.getError());
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("克隆仓库失败：" + response.getError())), true));
                }

            } catch (Exception e) {
                log.error("克隆仓库操作失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("克隆仓库失败：" + e.getMessage())), true));
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
