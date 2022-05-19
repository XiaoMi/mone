package com.xiaomi.mione.mquic.demo.server.manager;

import com.xiaomi.mione.mquic.demo.server.task.ITask;
import com.xiaomi.mione.mquic.demo.server.task.PingTask;
import lombok.Setter;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 9/5/21
 */
@Setter
public class TaskManager {

    private PingTask task = new PingTask();

    private ChannelManager channelManager;

    public void execute() {
        task.setChannelManager(channelManager);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
            task.execute();
        },0,5, TimeUnit.SECONDS);
    }

}
