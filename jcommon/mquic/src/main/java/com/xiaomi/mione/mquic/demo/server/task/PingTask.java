package com.xiaomi.mione.mquic.demo.server.task;

import com.xiaomi.mione.mquic.demo.server.manager.ChannelManager;
import lombok.Setter;

/**
 * @author goodjava@qq.com
 * @date 9/5/21
 */
@Setter
public class PingTask implements ITask {

    private ChannelManager channelManager;

    @Override
    public void execute() {
//        channelManager.sendMessage("ping");
    }
}
