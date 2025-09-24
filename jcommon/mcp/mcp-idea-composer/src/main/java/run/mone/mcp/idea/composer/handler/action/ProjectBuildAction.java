package run.mone.mcp.idea.composer.handler.action;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.Action;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;

/**
 * 项目构建动作
 * 负责生成项目代码和配置文件
 */
@Slf4j
public class ProjectBuildAction extends Action {

    @Override
    public CompletableFuture<Message> run(ActionReq req, ActionContext context) {
        log.info("ProjectBuildAction executing...");
        if (this.function == null) {
            log.error("ProjectBuildAction function is not set!");
            throw new IllegalStateException("ProjectBuildAction function is not set!");
        }
        return CompletableFuture.supplyAsync(() -> {
            Message result = this.function.apply(req, this, context);
            result.setRole(req.getRole().getName());
            return result;
        });
    }
} 