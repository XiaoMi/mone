package run.mone.agentx.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfo;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.schema.Message;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author goodjava@qq.com
 * @date 2025/4/18 10:12
 */
@Service
public class AgentInstanceService {

    @Resource
    private LLM llm;


    @Value("${mcp.grpc.port:9998}")
    private int grpcPort;

    private ConcurrentHashMap<String, ReactorRole> roleMap = new ConcurrentHashMap<>();

    public ReactorRole createRole(String owner, String clientId) {
        List<ITool> tools = Lists.newArrayList(new ChatTool(), new AskTool(), new AttemptCompletionTool());
        ReactorRole minzai = new ReactorRole("minzai", "staging", "0.0.1", grpcPort, new CountDownLatch(1), llm, tools) {
            @Override
            public void reg(RegInfo info) {
                // 直接传递传入的RegInfo对象
            }

            @Override
            public void unreg(RegInfo regInfo) {
                // 直接传递传入的RegInfo对象
            }

            @Override
            public void health(HealthInfo healthInfo) {
                // 直接传递传入的HealthInfo对象
            }
        };
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
