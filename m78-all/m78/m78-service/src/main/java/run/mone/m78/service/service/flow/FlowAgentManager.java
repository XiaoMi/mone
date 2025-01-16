package run.mone.m78.service.service.flow;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xiaomi.data.push.redis.Redis;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.m78.common.Constant;
import run.mone.m78.service.agent.bo.Agent;
import run.mone.m78.service.agent.rpc.AgentManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wmin
 * @date 2024/7/26
 */
@Service
@Slf4j
public class FlowAgentManager {

    @Resource
    private AgentManager agentManager;

    @Autowired
    private Redis redis;

    private Cache<String, Agent> flowRecordIdAgentCache;

    private static final int TIANYE_IP_REDIS_TTL = 1000 * 60 * 60 * 24;

    /**
     * 根据用户名和流程记录ID获取天业代理对象
     *
     * @param userName     用户名
     * @param flowRecordId 流程记录ID
     * @return 对应的天业代理对象，如果未找到则返回null
     */
    public Agent getTianYeAgent(String userName, String flowRecordId) {
        List<Agent> agents = agentManager.getAgentByKey(userName);
        if (agents == null || agents.isEmpty()) {
            log.info("Retrieve the remote agent.");
            agents = agentManager.getAgentByKey("public_agent");
            if (agents == null || agents.size() == 0) {
                log.warn("Agent with id {} not found", userName);
                return null;
            }
        }

        Agent agent = getAgentByFlowRecordId(flowRecordId, agents);
        if (agent == null) {
            log.warn("Agent with flowRecordId {} not found", flowRecordId);
            return null;
        }
        log.info("final flowRecordId:{},agent:{}", flowRecordId, agent.getAddress());
        return agent;
    }

    //TODO 负载均衡
    private Agent getRandomAgent(List<Agent> agents) {
        return agents.get(ThreadLocalRandom.current().nextInt(agents.size()));
    }

    /**
     * 程序的入口点
     *
     * @param args 命令行参数
     *             该方法生成一个随机整数并打印到控制台，随机数的范围是从0到0（不包括1），因此结果始终为0。
     */
    public static void main(String[] args) {
        System.out.println(ThreadLocalRandom.current().nextInt(1));
    }

    /**
     * 根据IP地址查找代理
     *
     * @param ip 代理的IP地址
     * @return 找到的代理对象，如果未找到则返回null
     */
    public Agent findAgentByIp(String ip) {
        try {
            List<Agent> agents = agentManager.getAgentList();
            return agents.stream().filter(i -> ip.equals(i.getAddress().split(":")[0])).collect(Collectors.toList()).get(0);
        } catch (Exception e) {
            log.warn("Agent with ip {} not found", ip);
            return null;
        }
    }

    private Agent getAgentByFlowRecordId(String flowRecordId, List<Agent> agents) {
        String tyAgentIp = redis.get(Constant.TIANYE_AGENT_IP_REDIS_PREFIX + flowRecordId);
        Agent finalAgent = getRandomAgent(agents);
        log.info("flowRecordId:{},tyAgentIp:{}", flowRecordId, tyAgentIp);
        if (StringUtils.isNotBlank(tyAgentIp)) {
            for (Agent agent : agents) {
                if (tyAgentIp.equals(agent.getAddress().split(":")[0])) {
                    finalAgent = agent;
                    log.info("flowRecordId:{},from cache tyAgentIp:{}", flowRecordId, tyAgentIp);
                    break;
                }
            }
        }
        redis.set(Constant.TIANYE_AGENT_IP_REDIS_PREFIX + flowRecordId, finalAgent.getAddress().split(":")[0]);
        return finalAgent;
    }


    /**
     * 如果缓存中不存在指定的代理IP，则将其存入缓存
     *
     * @param flowRecordId 流程记录ID
     * @param tyIp         代理IP地址
     */

    //bot执行时，并未记录tyAgentIp
    public void cacheAgentIpIfNotExists(String flowRecordId, String tyIp) {
        if (!redis.exists(Constant.TIANYE_AGENT_IP_REDIS_PREFIX + flowRecordId)) {
            log.info("cacheAgentIpIfNotExists flowRecordId:{},tyIp:{}", flowRecordId, tyIp);
            redis.set(Constant.TIANYE_AGENT_IP_REDIS_PREFIX + flowRecordId, tyIp);
        }
    }

    /**
     * 如果指定的flowRecordId在Redis中不存在，则缓存来自ty的IP地址
     *
     * @param flowRecordId 流程记录ID
     * @param fromTyIp     来自ty的IP地址
     */
    //subFlow由ty发起执行，执行结束后需要通知ty
    public void cacheSourceTyIpIfNotExists(String flowRecordId, String fromTyIp) {
        if (!redis.exists(Constant.SOURCE_TIANYE_AGENT_IP_REDIS_PREFIX + flowRecordId)) {
            log.info("cacheSourceTyIpIfNotExists flowRecordId:{},tyIp:{}", flowRecordId, fromTyIp);
            redis.set(Constant.SOURCE_TIANYE_AGENT_IP_REDIS_PREFIX + flowRecordId, fromTyIp);
        }
    }

    /**
     * 根据流记录ID获取源天业代理IP
     *
     * @param flowRecordId 流记录的唯一标识
     * @return 源天业代理IP，如果不存在则返回null
     */
    public String getSourceTyIp(String flowRecordId) {
        String key = Constant.SOURCE_TIANYE_AGENT_IP_REDIS_PREFIX + flowRecordId;
        String fromTyIp = redis.get(key);
        log.info("getSourceTyIp flowRecordId:{}, fromTyIp:{}", flowRecordId, fromTyIp);
        return fromTyIp;
    }


    /**
     * 初始化方法，在对象创建后调用。
     * <p>
     * 该方法使用CacheBuilder创建一个缓存，缓存的条目在写入一小时后过期，最大容量为5000条。
     */
    @PostConstruct
    public void init() {
        this.flowRecordIdAgentCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(5000)
                .build();
    }


    /**
     * 移除对应的agent缓存
     *
     * @param flowRecordId 流程记录ID，用于标识要移除的agent缓存
     */
    //移除对应的agent cache
    public void removeAgentCache(String flowRecordId) {
        log.info("removeAgentCache flowRecordId:{}", flowRecordId);
        redis.del(Constant.TIANYE_AGENT_IP_REDIS_PREFIX + flowRecordId);
    }


}
