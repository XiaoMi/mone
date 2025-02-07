package run.mone.mcp.playwright.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 11:20
 */
@Service
@Slf4j
public class ChromeTestService {


    public String cz(String prompt, LLM llm) {
        String d = "帮我搜索下苹果笔记本";
        String ask = AiTemplate.renderTemplate(prompt, ImmutableMap.of("data", d));
        AiMessage msg = AiMessage.builder().build();
        String answer = llm.ask(Lists.newArrayList(msg));
        log.info("answer:{}", answer);
        return answer;
    }

    public String openTag(JsonObject res) {
        String dataStr = """
                             <action type="createNewTab" url="https://www.jd.com/" auto="true">
                                        打开京东
                             </action>
                            """;
        res.addProperty("data", dataStr);
        sendMessageToAll(res.toString());
        return;
    }


}
