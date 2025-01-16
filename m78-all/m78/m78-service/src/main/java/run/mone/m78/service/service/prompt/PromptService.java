package run.mone.m78.service.service.prompt;

import cn.hutool.core.codec.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.m78.api.bo.prompt.PromptExecuteParam;
import run.mone.m78.api.constant.PromptActionTypeConstant;
import run.mone.m78.service.bo.chatgpt.Ask;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.database.UUIDUtil;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.base.SseService;
import run.mone.m78.service.service.user.UserService;

import javax.annotation.Resource;
import java.util.Map;


/**
 * @author wmin
 * @date 2024/2/23
 */
@Service
@Slf4j
public class PromptService {

    @Resource
    private ChatgptService chatgptService;

    @Resource
    private SseService sseService;

    @Resource
    private UserService userService;

    /**
     * 执行提示操作并通过SSE流返回结果
     *
     * @param param 包含执行提示操作所需参数的对象
     * @return SseEmitter 用于发送和接收SSE消息的对象
     */
	public SseEmitter promptExecuteStream(PromptExecuteParam param) {
        String uuid = UUIDUtil.generateType1UUID().toString();
        param.setUuid(uuid);
        Map<String, String> chatgptParams = param.getParams();
        String model = userService.getUserConfig(param.getUser(), true).getCodeModel(Config.model);
        String promptName = PromptActionTypeConstant.getEnumByAction(param.getAction()).getPromptName();

        Ask ask = Ask.builder().id(param.getUuid()).model(model).promptName(promptName).paramMap(chatgptParams).build();

        log.info("promptExecuteStream id:{}", uuid);

        StringBuilder sb = new StringBuilder();

        return sseService.submit(uuid, () -> {
            chatgptService.ask(ask, (msg) -> {
                if (msg.equals("^quit")) {
                    sseService.complete(uuid);
                    log.info("ask quit. rst:{}", sb);
                } else {
                    sb.append(Base64.decodeStr(msg));
                    sseService.sendMessage(uuid, msg);
                }
            });
        });
    }



}
