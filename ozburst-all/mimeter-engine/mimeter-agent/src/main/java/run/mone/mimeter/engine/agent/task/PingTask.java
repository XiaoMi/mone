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

package run.mone.mimeter.engine.agent.task;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.common.StringUtils;
import common.NetUtils;
import common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.mimeter.engine.agent.bo.MibenchCmd;
import run.mone.mimeter.engine.agent.bo.Version;
import run.mone.mimeter.engine.agent.bo.data.AgentInfoDTO;
import run.mone.mimeter.engine.agent.bo.data.AgentReq;
import run.mone.mimeter.engine.agent.bo.data.User;

import java.nio.charset.StandardCharsets;


/**
 * @author goodjava@qq.com
 * @date 2022/5/20
 */
@Component
public class PingTask extends Task {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final Gson gson = Util.getGson();

    private final String name = System.currentTimeMillis() + "";

    private static final String SERVER_NAME = "mimeter";

    public void init() {
        this.setRunnable(() -> {
            String msg = "ping:" + new Version();
            log.info(msg);
            AgentReq agentReq = new AgentReq();
            User user = new User();
            user.setName(name);
            agentReq.setUser(user);
            agentReq.setAgentInfoDTO(getApiInfoDto());
            RemotingCommand req = RemotingCommand.createRequestCommand(MibenchCmd.PING);
            req.setBody(gson.toJson(agentReq).getBytes(StandardCharsets.UTF_8));
            String addr = this.getClient().getServerAddrs();
            if (StringUtils.isNotEmpty(addr)) {
                this.getClient().sendMessage(addr, req, responseFuture -> log.info("pong"));
            }
        });
        //单位秒
        this.setDelay(5);
    }

    private AgentInfoDTO getApiInfoDto() {
        AgentInfoDTO agentInfoDTO = new AgentInfoDTO();
        agentInfoDTO.setIp(NetUtils.getLocalHost());

        agentInfoDTO.setPort(0);
        agentInfoDTO.setHostname(NetUtils.getHostName());
        //cpu mem信息从proms 同步
        agentInfoDTO.setMem(0);
        agentInfoDTO.setUseMem(0);
        agentInfoDTO.setCpu(0);
        agentInfoDTO.setUseCpu(0);
        agentInfoDTO.setServerName(SERVER_NAME);
        String selectZone = System.getenv("SELECT_ZONE");
        log.info("[PingTask.getApiInfoDto], selectZone: {}", selectZone);
        agentInfoDTO.setDesc(StringUtils.isEmpty(selectZone) ? "default" : selectZone);
        return agentInfoDTO;
    }

}
