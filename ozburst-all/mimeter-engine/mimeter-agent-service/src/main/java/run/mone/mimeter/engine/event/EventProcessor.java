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

package run.mone.mimeter.engine.event;

import com.google.common.eventbus.Subscribe;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.mimeter.engine.agent.bo.MibenchCmd;
import run.mone.mimeter.engine.agent.bo.data.AgentReq;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContextDTO;
import run.mone.mimeter.engine.agent.bo.task.TaskResult;
import run.mone.mimeter.engine.agent.bo.task.TaskStatusBo;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @author dongzhenxing
 * @date 2022/5/19
 */
@Component
public class EventProcessor {

    private static final Logger log = LoggerFactory.getLogger(EventProcessor.class);

    @Resource(name = "rpcClient")
    private RpcClient rpcClient;

    @Subscribe
    public void taskResultCallback(TaskResult tr) {
        log.info("tr id:{} success:{} failue:{}", tr.getId(), tr.getSuccess().get(), tr.getFailure().get());
        AgentReq ar = new AgentReq();
        ar.setTaskResult(tr);
        ar.setCmd(AgentReq.TASK_RESULT_CMD);
        try {
            rpcClient.sendMessage(RemotingCommand.createGsonRequestCommand(MibenchCmd.MANAGER, ar));
        } catch (Exception e) {
            log.error("taskResultCallback failed,reportId:{},cause by:{}",tr.getReportId(),e.getMessage());
        }
    }

    @Subscribe
    public void taskTotalContextCallback(SceneTotalCountContextDTO totalCountContextDTO) {
        AgentReq ar = new AgentReq();
        ar.setTotalCountContextDTO(totalCountContextDTO);
        ar.setCmd(AgentReq.TOTAL_DATA_COUNT_CMD);
        try {
            rpcClient.sendMessage(RemotingCommand.createGsonRequestCommand(MibenchCmd.MANAGER, ar));
        } catch (Exception e) {
            log.error("taskTotalContextCallback failed,reportId:{},cause by:{}",totalCountContextDTO.getReportId(),e.getMessage());
        }
    }

    @Subscribe
    public void taskStatusCallback(TaskStatusBo taskStatusBo) {
        AgentReq ar = new AgentReq();
        ar.setStatusBo(taskStatusBo);
        ar.setCmd(AgentReq.UP_TASK_STATUS);
        try {
            rpcClient.sendMessage(RemotingCommand.createGsonRequestCommand(MibenchCmd.MANAGER, ar));
        } catch (Exception e) {
            log.error("taskStatusCallback failed,tasktId:{},cause by:{}",taskStatusBo.getTaskId(),e.getMessage());
        }
    }
}
