/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.mischedule.processor;

import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.schedule.TaskManager;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskData;
import com.xiaomi.data.push.schedule.task.TaskParam;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author goodjava@qq.com
 */
public class CTaskProcessor implements NettyRequestProcessor {

    private TaskManager taskManager;

    public CTaskProcessor(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) throws Exception {

        TaskData data = remotingCommand.getReq(TaskData.class);

        TaskParam param =  data.getParam();
        TaskContext context = data.getTaskContext();
        taskManager.doTask(param, context);
        return RemotingCommand.createResponseCommand(4000);
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
