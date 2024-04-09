package run.mone.local.docean.controller;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.bo.AgentInfo;
import run.mone.local.docean.po.Message;
import run.mone.local.docean.service.LocalMessageService;
import run.mone.local.docean.service.ZService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/2/24 09:57
 */

@Slf4j
@Controller
public class AgentController {

    @Resource
    private ZService zService;

    @Resource
    private LocalMessageService localMessageService;


    //获取agent的信息,包括知识库id,prompt id和用户名(class)
    @RequestMapping(path = "/agent/getAgentInfo", method = "get")
    public Result<AgentInfo> getAgentInfo(@RequestParam("username") String username) {
        try {
            Long knowledgeId = zService.getKnowledgeIdByUserName(username, new HashMap<>());
            Long promptId = zService.getPromptIdByUserName(username);
            return Result.success(AgentInfo.builder().knowledgeId(knowledgeId).promptId(promptId).name(username).build());
        } catch (Exception e) {
            log.error("Failed to get agent info for user: {}", username, e);
            return Result.fromException(e);
        }
    }

    // 获取最近的bot message列表
    @RequestMapping(path = "/agent/getMsgs", method = "get")
    public Result<Map<String, List<Message>>> getLatestMsgList(@RequestParam("username") String username) {
        log.info("{} try to get local msg list...", username);
        try {
            Map<String, List<Message>> messagesMap = localMessageService.getMessagesMap();
            return Result.success(messagesMap);
        } catch (Exception e) {
            log.error("Failed to get local msg list, nested exception is:", e);
            return Result.fail(GeneralCodes.InternalError, "failed to get local msg list");
        }
    }

}
