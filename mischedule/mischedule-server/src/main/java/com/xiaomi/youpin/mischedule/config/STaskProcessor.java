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

package com.xiaomi.youpin.mischedule.config;

import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.schedule.TaskManager;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.youpin.mischedule.bo.RpcTaskReq;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author goodjava@qq.com
 * 处理任务
 * 支持任务的暂停和启动  修改参数
 */
public class STaskProcessor implements NettyRequestProcessor {

    private TaskManager taskManager;

    public STaskProcessor(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) {
        RpcTaskReq param = remotingCommand.getReq(RpcTaskReq.class);

        switch (param.getCmd()) {
            //暂停任务
            case "pause": {
                int id = param.getTaskId();
                this.taskManager.pause(id);
                break;
            }
            //开启任务
            case "start": {
                int id = param.getTaskId();
                this.taskManager.start(id);
                break;
            }
            //修改参数
            case "modifyParams": {
                int id = param.getTaskId();
                String params = param.getAttachments().get("params");
                this.taskManager.modifyParam(id, params);
                break;
            }
            //修改任务
            case "modify": {
                int id = param.getTaskId();
                String result = param.getAttachments().get("result");
                String context = param.getAttachments().get("context");
                String status = param.getAttachments().get("status");
                this.taskManager.modifyTaskCache(String.valueOf(id), result, context, status);
                break;
            }
        }
        return RemotingCommand.createResponseCommand(2000);
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
