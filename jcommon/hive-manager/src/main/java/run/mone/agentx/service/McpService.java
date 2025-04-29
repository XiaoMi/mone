package run.mone.agentx.service;

import com.google.common.base.Joiner;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import run.mone.agentx.dto.AgentWithInstancesDTO;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentInstance;
import run.mone.agentx.interceptor.CustomMcpInterceptor;
import run.mone.hive.common.Result;
import run.mone.hive.mcp.client.MonerMcpClient;
import run.mone.hive.mcp.client.MonerMcpInterceptor;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.hub.McpHubHolder;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Data
@Service
public class McpService {

    private MonerMcpInterceptor mcpInterceptor = new CustomMcpInterceptor();

    @Autowired
    private AgentService agentService;

    private ReentrantLock lock = new ReentrantLock();


    public void callMcp(Long agentId, AgentInstance instance, Result it, FluxSink sink) {
        AgentWithInstancesDTO agentDto = agentService.findAgentWithInstances(agentId).block();

        if (instance == null) {
            // 获取agent详情
            if (agentDto.getInstances() == null || agentDto.getInstances().isEmpty()) {
                return;
            }
            instance = agentDto.getInstances().get(0);
        }

        //这个需要那个用户就传他的id (需要从前端拿过来)
        String clientId = getAgentKey(agentDto.getAgent());

        String groupKey = Joiner.on(":").join(clientId, instance.getIp(), instance.getPort());

        try {
            lock.lock();
            McpHub hub = McpHubHolder.get(groupKey);
            if (Optional.ofNullable(hub).isEmpty()) {
                connectMcp(instance, clientId, groupKey);
            }
        } catch (Exception e) {
            log.error("callMcp error.", e);
        } finally {
            lock.unlock();
        }

        // 调用MCP
        MonerMcpClient.mcpCall(it, groupKey, this.mcpInterceptor, sink, (name) -> null);
    }

    private static void connectMcp(AgentInstance instance, String clientId, String groupKey) {
        McpHub mcpHub = new McpHub(null, (msg) -> {
        }, true);
        ServerParameters parameters = new ServerParameters();
        parameters.setType("grpc");
        parameters.getEnv().put("host", instance.getIp());
        parameters.getEnv().put("port", String.valueOf(instance.getPort()));
        parameters.getEnv().put("token", "token");
        parameters.getEnv().put("clientId", clientId);
        //底下有断线从连机制,先解决连一个的问题
        mcpHub.connectToServer(groupKey, parameters);
        McpHubHolder.put(groupKey, mcpHub);
    }


    private String getAgentKey(Agent agent) {
        return agent.getName() + ":"
                + (agent.getGroup() == null ? "" : agent.getGroup()) + ":"
                + (agent.getVersion() == null ? "" : agent.getVersion());
    }


}
