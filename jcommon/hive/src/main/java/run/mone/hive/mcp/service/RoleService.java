package run.mone.hive.mcp.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfo;
import run.mone.hive.configs.Const;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.hub.McpHubHolder;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.schema.Message;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 09:49
 */
@RequiredArgsConstructor
public class RoleService {

    private final LLM llm;

    private final GrpcServerTransport grpcServerTransport;

    private final List<ITool> toolList;

    private final IHiveManagerService hiveManagerService;

    @Value("${mcp.hub.path:}")
    private String mcpPath;

    private ConcurrentHashMap<String, ReactorRole> roleMap = new ConcurrentHashMap<>();

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;

    @PostConstruct
    @SneakyThrows
    public void init() {
        //启用mcp (这个Agent也可以使用mcp)
        if (StringUtils.isNotEmpty(mcpPath)) {
            McpHubHolder.put(Const.DEFAULT, new McpHub(Paths.get(mcpPath)));
        }
    }

    public ReactorRole createRole(String owner, String clientId) {
        ReactorRole role = new ReactorRole("minzai", "staging", "0.0.1", grpcPort, new CountDownLatch(1), llm, this.toolList) {
            @Override
            public void reg(RegInfo info) {
                // 直接传递传入的RegInfo对象
                hiveManagerService.register(info);
            }

            @Override
            public void unreg(RegInfo regInfo) {
                // 直接传递传入的RegInfo对象
                hiveManagerService.unregister(regInfo);
            }

            @Override
            public void health(HealthInfo healthInfo) {
                // 直接传递传入的HealthInfo对象
                hiveManagerService.heartbeat(healthInfo);
            }
        };
        role.setOwner(owner);
        role.setClientId(clientId);
        //一直执行不会停下来
        role.run();
        return role;
    }


    //根据from进行隔离(比如Athena 不同 的project就是不同的from)
    public Flux<String> receiveMsg(Message message) {
        String from = message.getSentFrom().toString();
        if (!roleMap.containsKey(from)) {
            roleMap.putIfAbsent(from, createRole(from, message.getClientId()));
        }
        ReactorRole role = roleMap.get(from);
        return Flux.create(sink -> {
            message.setSink(sink);
            role.putMessage(message);
        });
    }

    public void clearHistory(Message message) {
        // Clear the role's memory
        String from = message.getSentFrom().toString();
        if (roleMap.containsKey(from)) {
            ReactorRole minzai = roleMap.get(from);
            minzai.clearMemory();
        }
    }

}
