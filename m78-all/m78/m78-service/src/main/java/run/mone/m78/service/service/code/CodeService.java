package run.mone.m78.service.service.code;

import cn.hutool.core.codec.Base64;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.m78.api.bo.code.GenerateMethodParam;
import run.mone.m78.service.bo.chatgpt.Ask;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.database.UUIDUtil;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.base.SseService;
import run.mone.m78.service.service.user.UserService;

import javax.annotation.Resource;
import java.util.Map;

import static run.mone.m78.api.constant.PromptConstant.*;

/**
 * @author dp
 * @date 2024/1/25
 */
@Service
@Slf4j
public class CodeService {

    @Resource
    private ChatgptService chatgptService;

    @Resource
    private SseService sseService;

    @Resource
    private UserService userService;

    /**
     * 生成方法流的SSE连接
     *
     * @param generateMethodParam 包含生成方法所需参数的对象
     * @return SseEmitter 用于发送和接收服务器推送事件的对象
     */
	public SseEmitter generateMethodStream(GenerateMethodParam generateMethodParam) {
        String uuid = UUIDUtil.generateType1UUID().toString();
        generateMethodParam.setUuid(uuid);
        Map<String, String> chatgptParams = adapterGenerateMethodStream(generateMethodParam);

        String model = userService.getUserConfig(generateMethodParam.getUser(), true).getCodeModel(Config.model);

        Ask ask = Ask.builder().id(generateMethodParam.getUuid()).model(model).promptName(PROMPT_CODE_GENERATE_JAVA_METHOD).paramMap(chatgptParams).build();

        log.info("textTranslateStream id:{}", uuid);

        StringBuilder sb = new StringBuilder();

        return sseService.submit(uuid, () -> {
            chatgptService.ask(ask, (msg) -> {
                if (msg.equals("^quit")) {
                    sseService.complete(uuid);
                    log.info("ask quit");
                } else {
                    sb.append(Base64.decodeStr(msg));
                    sseService.sendMessage(uuid, msg);
                }
            });
        });
    }

    private Map<String, String> adapterGenerateMethodStream(GenerateMethodParam generateMethodParam) {
        Map<String, String> chatgptParams = ImmutableMap.of(
                "comment", generateMethodParam.getComment());

        return chatgptParams;
    }




}
