package run.mone.mcp.chat.service;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.hub.McpHubHolder;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.schema.Message;
import run.mone.mcp.chat.task.MinZaiTask;
import run.mone.mcp.chat.tool.DocumentProcessingTool;
import run.mone.mcp.chat.tool.SystemInfoTool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

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
        ReactorRole minzai = new ReactorRole("minzai", llm);
        minzai.setScheduledTaskHandler(role -> new MinZaiTask(minzai, grpcServerTransport).run());
        //支持使用聊天工具(聊天工具是比mcp要轻量级的存在,如果tool能支持,优先使用tool)
        minzai.addTool(new ChatTool());
        minzai.addTool(new AskTool());
        minzai.addTool(new AttemptCompletionTool());
        minzai.addTool(new DocumentProcessingTool());
        minzai.addTool(new SystemInfoTool());

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


}
