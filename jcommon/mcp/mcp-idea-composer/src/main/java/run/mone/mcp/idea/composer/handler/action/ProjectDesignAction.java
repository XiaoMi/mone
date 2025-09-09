package run.mone.mcp.idea.composer.handler.action;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.Action;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;

/**
 * 项目设计动作
 * 负责生成项目结构并创建目录
 */
@Slf4j
public class ProjectDesignAction extends Action {

    @Override
    public CompletableFuture<Message> run(ActionReq req, ActionContext context) {
        log.info("ProjectDesignAction executing...");
        if (this.function == null) {
            log.error("ProjectDesignAction function is not set!");
            throw new IllegalStateException("ProjectDesignAction function is not set!");
        }
        return CompletableFuture.supplyAsync(() -> {
            Message result = this.function.apply(req, this, context);
            result.setRole(req.getRole().getName());
            return result;
        });
    }
} 