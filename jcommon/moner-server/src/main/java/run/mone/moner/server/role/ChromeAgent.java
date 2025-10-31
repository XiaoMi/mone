package run.mone.moner.server.role;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.schema.Message;
import run.mone.hive.utils.NetUtils;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.common.RoleType;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.moner.server.bo.ChatWebSocketResp;
import run.mone.moner.server.constant.ResultType;
import run.mone.moner.server.role.tool.*;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 14:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ChromeAgent extends ReactorRole {

    private WebSocketSession session;

    public ChromeAgent(WebSocketSession session, RoleMeta roleMeta) {
        // ReactorRole(String name, String group, String version, String profile, String goal, String constraints, Integer port, LLM llm, List<ITool> tools, List<McpSchema.Tool> mcpTools, String ip)
        super(roleMeta.getName(), "local", "v1", roleMeta.getProfile(), roleMeta.getGoal(), roleMeta.getConstraints(), -1, roleMeta.getLlm(), new ArrayList<>(), new ArrayList<>(), NetUtils.getLocalHost());
        this.session = session;
        // 标识归属，供某些工具使用
        this.setOwner("chrome");
        // 注册内部工具（用于在系统提示中展示并指导LLM选择合适的MCP调用）
        this.addTool(new ChatTool());
        this.addTool(new OpenTabActionTool());
        this.addTool(new OperationActionTool());
        this.addTool(new ScrollActionTool());
        this.addTool(new GetContentActionTool());
        this.addTool(new FullPageActionTool());
        this.addTool(new CodeActionTool());
        this.addTool(new ClickAfterRefreshTool());
        this.addTool(new MemoryActionTool());
        this.addTool(new ProcessActionTool());
        applyRoleMeta(this, roleMeta);
    }

    @Override
    public void sendMessage(Message message) {
        // 将LLM的流式事件或最终输出转发到前端
        String content = StringUtils.defaultString(message.getContent(), "");
        String messageType = StringUtils.defaultString(message.getType(), "");
        try {
            ChatWebSocketResp resp = ChatWebSocketResp.builder()
                    .roleName(this.getName())
                    .roleType("ASSISTANT")
                    .content(content)
                    .messageType(messageType)
                    .type(ResultType.CHAT)
                    .build();
            log.info("send message:{}", resp);
            session.sendMessage(new TextMessage(GsonUtils.gson.toJson(resp)));
        } catch (Exception e) {
            log.error("send message error", e);
        }
    }

    @Override
    public void putMessage(Message message) {
        super.putMessage(message);
        // 额外地将工具执行的XML内容转发到前端（供浏览器插件执行）
        try {
            if (message != null
                    && RoleType.assistant.name().equalsIgnoreCase(StringUtils.defaultString(message.getRole(), ""))
                    && StringUtils.isNotBlank(message.getContent())) {
                String c = message.getContent();
                boolean looksLikeAction = c.contains("<action") || c.contains("</use_mcp_tool>") || c.contains("<use_mcp_tool>")
                        || c.startsWith("<chat>") || c.contains("<attempt_completion>") || c.contains("<ask_followup_question>")
                        || c.contains("<memory>");
                if (looksLikeAction) {
                    ChatWebSocketResp resp = ChatWebSocketResp.builder()
                            .roleName(this.getName())
                            .roleType("ASSISTANT")
                            .content(c)
                            .type(ResultType.ACTION)
                            .build();
                    session.sendMessage(new TextMessage(run.mone.moner.server.common.GsonUtils.gson.toJson(resp)));
                }
            }
        } catch (Exception e) {
            log.warn("forward action content error", e);
        }
    }

    private void applyRoleMeta(ReactorRole role, RoleMeta roleMeta) {
        role.setRoleMeta(roleMeta);
        role.setProfile(roleMeta.getProfile());
        role.setGoal(roleMeta.getGoal());
        role.setConstraints(roleMeta.getConstraints());
        role.setWorkflow(roleMeta.getWorkflow());
        role.setOutputFormat(roleMeta.getOutputFormat());
        role.setActions(roleMeta.getActions());
        role.setType(roleMeta.getRoleType());
        if (null != roleMeta.getLlm()) {
            role.setLlm(roleMeta.getLlm());
        }
        if (null != roleMeta.getReactMode()) {
            role.getRc().setReactMode(roleMeta.getReactMode());
        }
    }
}
