package run.mone.mcp.chat.service;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfo;
import run.mone.hive.configs.Const;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.hub.McpHubHolder;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.schema.Message;
import run.mone.mcp.chat.task.MinZaiTask;
import run.mone.mcp.chat.tool.DocumentProcessingTool;
import run.mone.mcp.chat.tool.SystemInfoTool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 09:49
 */
@Service
public class RoleService {

    @Resource
    private LLM llm;

    @Resource
    private GrpcServerTransport grpcServerTransport;

    @Value("${mcp.hub.path:}")
    private String mcpPath;

    private ConcurrentHashMap<String, ReactorRole> roleMap = new ConcurrentHashMap<>();

    @PostConstruct
    @SneakyThrows
    public void init() {
        //启用mcp (这个Agent也可以使用mcp)
        if (StringUtils.isNotEmpty(mcpPath)) {
            McpHubHolder.put(Const.DEFAULT, new McpHub(Paths.get(mcpPath)));
        }
    }

    public ReactorRole createRole(String owner, String clientId) {
        List<ITool> tools = Lists.newArrayList(new ChatTool(), new AskTool(), new AttemptCompletionTool(), new DocumentProcessingTool(), new SystemInfoTool());
        ReactorRole minzai = new ReactorRole("minzai", "staging", "0.0.1", new CountDownLatch(1), llm, tools) {
            @Override
            public void reg(RegInfo info) {
            }

            @Override
            public void unreg(RegInfo regInfo) {
            }

            @Override
            public void health(HealthInfo healthInfo) {
            }
        };
        minzai.setScheduledTaskHandler(role -> new MinZaiTask(minzai, grpcServerTransport).run());
        minzai.setOwner(owner);
        minzai.setClientId(clientId);
        //一直执行不会停下来
        minzai.run();
        return minzai;
    }


    //根据from进行隔离(比如Athena 不同 的project就是不同的from)
    public Flux<String> receiveMsg(Message message) {
        String from = message.getSentFrom().toString();
        if (!roleMap.containsKey(from)) {
            roleMap.putIfAbsent(from, createRole(from, message.getClientId()));
        }
        ReactorRole minzai = roleMap.get(from);
        return Flux.create(sink -> {
            message.setSink(sink);
            minzai.putMessage(message);
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
