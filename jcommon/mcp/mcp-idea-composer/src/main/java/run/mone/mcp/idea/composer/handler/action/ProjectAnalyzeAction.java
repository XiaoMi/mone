package run.mone.mcp.idea.composer.handler.action;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.Action;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;

/**
 * 项目需求分析动作
 * 负责分析用户需求并生成项目基本信息
 */
@Slf4j
public class ProjectAnalyzeAction extends Action {

    @Override
    public CompletableFuture<Message> run(ActionReq req, ActionContext context) {
        log.info("ProjectAnalyzeAction executing...");
        if (this.function == null) {
            log.error("ProjectAnalyzeAction function is not set!");
            throw new IllegalStateException("ProjectAnalyzeAction function is not set!");
        }
        return CompletableFuture.supplyAsync(() -> {
            Message result = this.function.apply(req, this, context);
            result.setRole(req.getRole().getName());
            return result;
        });
    }
} 