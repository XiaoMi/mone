package com.xiaomi.mone.log.agent.channel.listener;

import com.xiaomi.mone.log.agent.channel.ChannelService;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/4 15:06
 */
public interface FileMonitorListener {
    /**
     * 新增
     */
    void addChannelService(ChannelService channelService);

    /**
     * 删除
     */
    void removeChannelService(ChannelService channelService);
}
