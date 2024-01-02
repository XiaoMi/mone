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

package run.mone.mimeter.engine.agent.processor;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import common.Util;
import io.netty.channel.ChannelHandlerContext;
import run.mone.mimeter.engine.agent.bo.MibenchCmd;
import run.mone.mimeter.engine.agent.bo.data.AgentReq;
import run.mone.mimeter.engine.agent.bo.task.Context;
import run.mone.mimeter.engine.agent.bo.task.HostsFileResult;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskResult;
import run.mone.mimeter.engine.service.BenchEngineService;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author goodjava@qq.com
 * @date 2022/5/11
 */
@Component
public class AgentProcessor implements NettyRequestProcessor {

    @Resource
    private BenchEngineService engineService;

    private static final Gson gson = Util.getGson();

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) throws Exception {
        AgentReq req = remotingCommand.getReq(AgentReq.class);
        Task task = req.getTask();
        //manager server 地址
        if (task != null){
            task.setAddr(req.getAddr());
        }
        if (req.getCmd().equals(AgentReq.SUBMIT_TASK_CMD)) {
            if (task.isDebug()){
                TaskResult tr = engineService.submitTask(new Context(), task);
                RemotingCommand resp = RemotingCommand.createResponseCommand(MibenchCmd.TASK);
                resp.setBody(gson.toJson(tr).getBytes(StandardCharsets.UTF_8));
                return resp;
            }
            engineService.submitTask(new Context(), task);
        }

        //停止任务
        if (req.getCmd().equals(AgentReq.CANCEL_TASK_CMD)) {
            engineService.cancelTask(task);
        }

        //变更任务rps
        if (req.getCmd().equals(AgentReq.CHANGE_TASK_QPS)) {
            engineService.changeTaskQps(req.getChangeQpsReq());
        }

        //修改本地host文件
        if (req.getCmd().equals(AgentReq.EDIT_HOST_CMD)) {
            engineService.editHostsFile(req.getAgentHostReqList());
            RemotingCommand resp = RemotingCommand.createResponseCommand(MibenchCmd.TASK);
            TaskResult tr = new TaskResult();
            tr.setOk(true);
            resp.setBody(gson.toJson(tr).getBytes(StandardCharsets.UTF_8));
            return resp;
        }

        //删除本地某项host配置
        if (req.getCmd().equals(AgentReq.DEL_HOST_CMD)){
            engineService.delHostsFile(req.getAgentHostReqList());
            RemotingCommand resp = RemotingCommand.createResponseCommand(MibenchCmd.TASK);
            TaskResult tr = new TaskResult();
            tr.setOk(true);
            resp.setBody(gson.toJson(tr).getBytes(StandardCharsets.UTF_8));
            return resp;
        }

        //加载本地某项host配置
        if (req.getCmd().equals(AgentReq.LOAD_HOST_CMD)){
            RemotingCommand resp = RemotingCommand.createResponseCommand(MibenchCmd.TASK);
            HostsFileResult hostsFileResult = engineService.loadHostsReq();
            resp.setBody(gson.toJson(hostsFileResult).getBytes(StandardCharsets.UTF_8));
            return resp;
        }


        return RemotingCommand.createResponseCommand(MibenchCmd.TASK);
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    @Override
    public int cmdId() {
        return MibenchCmd.TASK;
    }


}
