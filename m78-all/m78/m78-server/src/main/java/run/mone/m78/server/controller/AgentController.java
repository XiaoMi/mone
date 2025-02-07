package run.mone.m78.server.controller;

import com.google.common.base.Preconditions;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import run.mone.local.docean.protobuf.AiMessage;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.m78.service.agent.bo.Agent;
import run.mone.m78.service.agent.rpc.AgentManager;
import run.mone.m78.service.agent.rpc.AgentRpcService;
import run.mone.m78.service.bo.CommunicateParam;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static run.mone.m78.api.constant.CommonConstant.AGENT_RPC_TIMEOUT;
import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.*;

/**
 * @author goodjava@qq.com
 * @date 2024/2/23 14:50
 */

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/agent")
public class AgentController {

    @Resource
    private AgentManager agentManager;

    @Resource
    private LoginService loginService;

    @Resource
    private AgentRpcService agentRpcService;


    //获取agent列表
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<List<Agent>> getAgentList(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get agent list");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(agentManager.getAgentList());
    }


    //和某个agent交流(class)
    @SneakyThrows
    @RequestMapping(value = "/communicate", method = RequestMethod.POST)
    public Result<String> communicateWithAgent(HttpServletRequest request, @RequestParam("agentId") String agentId, @RequestBody CommunicateParam communicateParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(communicateParam.getMessage()), "message can not be null");
        Preconditions.checkArgument(StringUtils.isNotBlank(communicateParam.getTopicId()), "topicId can not be null");
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to communicate with agent");
            return Result.fail(STATUS_FORBIDDEN, "User not authenticated");
        }
        // TODO: 复用chat功能
        List<Agent> agents = agentManager.getAgentByKey(agentId);
        if (agents == null || agents.isEmpty()) {
            log.error("Agent with id {} Agent not found", agentId);
            return Result.fail(STATUS_NOT_FOUND, "Agent not found");
        }
        //TODO 负载均衡
        Agent agent = agents.get(ThreadLocalRandom.current().nextInt(agents.size()));
        // Assuming there is a method to send message to the agent
        RemotingCommand req = RemotingCommand.createRequestCommand(TianyeCmd.clientMessageReq);
        req.addExtField("protobuf", "true");
        AiMessage aiMessage = AiMessage.newBuilder().setMessage(communicateParam.getMessage()).setFrom(account.getUsername()).setTo(agentId).setTopicId(communicateParam.getTopicId()).build();
        req.setBody(aiMessage.toByteArray());
        RemotingCommand res = agentRpcService.getRpcServer().sendMessage(agent.getAddress(), req, AGENT_RPC_TIMEOUT);
        AiResult result = AiResult.parseFrom(res.getBody());
        return Result.success(result.getMessage());
    }

}
