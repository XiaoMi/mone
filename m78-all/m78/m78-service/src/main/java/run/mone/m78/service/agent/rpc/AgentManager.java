package run.mone.m78.service.agent.rpc;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import run.mone.local.docean.protobuf.AiMessage;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.m78.service.agent.bo.Agent;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.SafeRun;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.CommonConstant.AGENT_RPC_TIMEOUT;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;

/**
 * @author goodjava@qq.com
 * @date 2024/2/23 14:38
 */
@Service
@Slf4j
public class AgentManager {

    private ConcurrentHashMap<String, List<Agent>> agentMap = new ConcurrentHashMap<>();

    @Resource
    private AgentRpcService agentRpcService;


    /**
     * 初始化方法，在对象构造后执行。
     * <p>
     * 该方法启动一个定时任务，每5秒执行一次，检查agentMap中的Agent对象，
     * 如果某个Agent对象的时间超过15秒未更新，则将其从agentMap中移除。
     */
    @PostConstruct
    public void init() {
        //agent里边有time,判断15秒都没有更新的从agentMap中删除
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            SafeRun.run(() -> {
                agentMap.entrySet().stream().forEach(entry -> {
                    Iterator<Agent> iterator = entry.getValue().iterator();
                    while (iterator.hasNext()) {
                        Agent agent = iterator.next();
                        if (System.currentTimeMillis() - agent.getTime() > 15000) {
                            log.info("Agent {} is not updated, remove it.", agent.getAddress());
                            iterator.remove();
                        }
                    }
                });
            });
        }, 5, 5, TimeUnit.SECONDS);
    }


    /**
     * 将Agent对象添加到agentMap中。如果agentMap中已存在具有相同名称的Agent列表，
     * 则更新具有相同地址的Agent的时间；如果没有相同地址的Agent，则将新的Agent添加到列表中。
     *
     * @param agent 要添加或更新的Agent对象
     */
    public void putAgent(Agent agent) {
        synchronized (agentMap) {
            List<Agent> agents = agentMap.get(agent.getName());
            agent.setTime(System.currentTimeMillis());
            if (CollectionUtils.isEmpty(agents)) {
                agents = new ArrayList<>();
                agents.add(agent);
                agentMap.put(agent.getName(), agents);
            } else {
                boolean hasUpdate = false;
                for (Agent oldAgent : agents) {
                    if (agent.getAddress().equals(oldAgent.getAddress())) {
                        oldAgent.setTime(agent.getTime());
                        hasUpdate = true;
                        break;
                    }
                }
                if (!hasUpdate) {
                    agents.add(agent);
                }
            }
        }
    }

    /**
     * 根据给定的键获取对应的Agent列表
     *
     * @param key 键，用于查找对应的Agent列表
     * @return 对应键的Agent列表
     */
    //按key获取agent(class)
    public List<Agent> getAgentByKey(String key) {
        return agentMap.get(key);
    }


    /**
     * 获取所有Agent的列表
     *
     * @return 包含所有Agent的列表
     */
    //获取agent列表(class)
    public List<Agent> getAgentList() {
        return agentMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }


    /**
     * 发送命令到指定的代理
     *
     * @param userName 用户名，用于获取代理
     * @param topicId  主题ID，用于消息的主题标识
     * @param cmd      命令字符串
     * @param message  消息内容
     * @param timeout  超时时间，单位为毫秒
     * @return 包含AiResult的Result对象，表示操作结果
     * @throws Exception 如果在发送消息过程中发生错误
     */
    @SneakyThrows
    public Result<AiResult> sendCommandToAgent(String userName, String topicId, String cmd, Object message, Integer timeout) {
        List<Agent> agents = getAgentByKey(userName);
        //以userName起过agent，那agents不为空但size==0
        if (CollectionUtils.isEmpty(agents)) {
            log.info("Retrieve the remote agent.");
            agents = getAgentByKey("public_agent");
            if (agents == null || agents.size() == 0) {
                log.error("Agent with id {} Agent not found", userName);
                return Result.fail(STATUS_NOT_FOUND, "Agent not found");
            }
        }
        //TODO 负载均衡
        Agent agent = agents.get(0);
        try {
            agent = selectAgentByAffinity(userName, agents);
        } catch (Exception e) {
            log.error("Agent not found load balance error, msg: {}, size: {}", e.getMessage(), agents.size());
        }
        RemotingCommand req = RemotingCommand.createRequestCommand(TianyeCmd.clientMessageReq);
        req.addExtField("protobuf", "true");
        AiMessage aiMessage = AiMessage.newBuilder().setCmd(cmd).setMessage(GsonUtils.gson.toJson(message)).setFrom("m78").setTo(userName).setTopicId(topicId).build();
        req.setBody(aiMessage.toByteArray());
        RemotingCommand res = agentRpcService.getRpcServer().sendMessage(agent.getAddress(), req, Optional.ofNullable(timeout).orElse(AGENT_RPC_TIMEOUT));
        AiResult result = AiResult.parseFrom(res.getBody());
        log.info("message:{}", result.getMessage());
        return Result.success(result);
    }


    /**
     * 根据用户名，从传入的代理列表中选择一个具有亲和性的代理
     *
     * @param userName 用户名
     * @param agents   代理列表
     * @return 选择的代理
     */
    // 根据用户名，从传入的List<Agent>里带有亲和性的选择一个agent
    public Agent selectAgentByAffinity(String userName, @Nonnull List<Agent> agents) {
        HashCode hasCode = Hashing.murmur3_32_fixed().hashString(userName, StandardCharsets.UTF_8);
        int agentIndex = Hashing.consistentHash(hasCode, agents.size());
        if (agentIndex > agents.size() - 1) {
            agentIndex = agentIndex % agents.size();
        }
        return agents.get(agentIndex);
    }

}
