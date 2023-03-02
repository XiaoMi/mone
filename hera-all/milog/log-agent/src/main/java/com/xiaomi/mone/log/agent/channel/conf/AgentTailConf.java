package com.xiaomi.mone.log.agent.channel.conf;

import com.xiaomi.mone.log.agent.channel.ChannelDefine;
import com.xiaomi.mone.log.api.model.meta.AgentDefine;
import lombok.Data;

import java.util.List;

/**
 * @author milog
 */
@Data
public class AgentTailConf {
    private List<ChannelDefine> channelDefine;
    private AgentDefine agentDefine;
}