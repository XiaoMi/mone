package run.mone.agentx.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import run.mone.agentx.dto.AgentWithInstancesDTO;
import run.mone.agentx.entity.AgentInstance;
import run.mone.agentx.interceptor.CustomMcpInterceptor;
import run.mone.agentx.utils.AgentKeyUtils;
import run.mone.hive.common.McpResult;
import run.mone.hive.common.ToolDataInfo;
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


    public McpResult callMcp(String userName, Long agentId, AgentInstance instance, ToolDataInfo it, FluxSink sink) {
        log.info("user:{} call mcp tool", userName);
        AgentWithInstancesDTO agentDto = agentService.findAgentWithInstances(agentId).block();

        //这个需要那个用户就传他的id (需要从前端拿过来)
        String clientId = AgentKeyUtils.getAgentKey(agentDto.getAgent());

        //对面的ip和port 服务端的
        String key = AgentKeyUtils.key(agentDto, instance);

        try {
            lock.lock();
            McpHub hub = McpHubHolder.get(key);
            if (Optional.ofNullable(hub).isEmpty()) {
                connectMcp(instance, key, key);
            }
        } catch (Exception e) {
            log.error("callMcp error.", e);
        } finally {
            lock.unlock();
        }

        // 调用MCP
        return MonerMcpClient.mcpCall(it, key, this.mcpInterceptor, sink, (name) -> null);
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


}
