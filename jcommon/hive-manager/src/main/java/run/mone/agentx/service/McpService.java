package run.mone.agentx.service;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentInstance;
import run.mone.agentx.interceptor.CustomMcpInterceptor;
import run.mone.agentx.utils.GsonUtils;
import run.mone.agentx.utils.McpConfigUtils;
import run.mone.hive.common.Result;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.client.MonerMcpClient;
import run.mone.hive.mcp.client.MonerMcpInterceptor;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.hub.McpHubHolder;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@Service
public class McpService {

    private MonerMcpInterceptor mcpInterceptor = new CustomMcpInterceptor();

    @Value("${mcp.hub.path:}")
    private String mcpPath;

    @Autowired
    private AgentService agentService;

    @PostConstruct
    @SneakyThrows
    public void init() {
        //启用mcp (这个Agent也可以使用mcp)
        if (StringUtils.isNotEmpty(mcpPath)) {
            McpHubHolder.put(Const.DEFAULT, new McpHub(Paths.get(mcpPath)));
        }
    }

    public void callMcp(Long agentId, Result it, FluxSink sink) {

        // 获取agent详情
        agentService.findAgentWithInstances(agentId).subscribe(agentDto -> {
            if (agentDto.getInstances() == null || agentDto.getInstances().size() == 0) {
                return;
            }

            // 构建serverConfig
            Map<String, Object> serverConfig = new HashMap<>();
            serverConfig.put("type", "grpc");
            serverConfig.put("sseRemote", true);

            // 从mcpToolMap中获取env配置
            Map<String, String> env = new HashMap<>();
            AgentInstance instance = agentDto.getInstances().get(0);
            env.put("host", instance.getIp());
            env.put("port", String.valueOf(instance.getPort()));
            env.put("clientId", "ceshi");
            env.put("token", "token");
            serverConfig.put("env", env);

            // 更新MCP配置
            if (McpConfigUtils.updateMcpConfig(mcpPath, getAgentKey(agentDto.getAgent()), serverConfig)) {
                if (McpHubHolder.containsKey(Const.DEFAULT)) {
                    McpHubHolder.get(Const.DEFAULT).refreshMcpServer(getAgentKey(agentDto.getAgent()));
                } else {
                    try {
                        McpHubHolder.put(Const.DEFAULT, new McpHub(Paths.get(mcpPath)));
                    } catch (Exception e) {
                        log.error("init mcp hub error");
                    }
                }
            }

            // 调用MCP
            MonerMcpClient.mcpCall(it, Const.DEFAULT, this.mcpInterceptor, sink);
        });
    }


    private String getAgentKey(Agent agent) {
        return agent.getName() + ":" + agent.getGroup() + ":" + agent.getVersion();
    }


}
