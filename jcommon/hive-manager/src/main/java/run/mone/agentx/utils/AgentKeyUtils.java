package run.mone.agentx.utils;

import com.google.common.base.Joiner;
import run.mone.agentx.dto.AgentWithInstancesDTO;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentInstance;

/**
 * @author goodjava@qq.com
 * @date 2025/5/8 11:52
 */
public class AgentKeyUtils {

    public static String key(AgentWithInstancesDTO agent ,AgentInstance instance) {
        if (null == instance) {
           instance = agent.getInstances().get(0);
        }
        String clientId = getAgentKey(agent.getAgent());
        return Joiner.on(":").join(clientId, instance.getIp(), instance.getPort());
    }

    public static String getAgentKey(Agent agent) {
        return agent.getName() + ":"
                + (agent.getGroup() == null ? "" : agent.getGroup()) + ":"
                + (agent.getVersion() == null ? "" : agent.getVersion());
    }
}
