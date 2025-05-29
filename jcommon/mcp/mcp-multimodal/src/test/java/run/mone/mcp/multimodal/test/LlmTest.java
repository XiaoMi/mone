package run.mone.mcp.multimodal.test;

import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.Message;
import run.mone.mcp.multimodal.config.Prompt;
import run.mone.mcp.multimodal.util.ActionResponseParser;
import run.mone.mcp.multimodal.util.ImageProcessingUtil;

/**
 * @author goodjava@qq.com
 * @date 2025/5/13 15:20
 */
public class LlmTest {


    @SneakyThrows
    @Test
    public void test1() {
        LLM llm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO_UI_TARS).temperature(Prompt.temperature).build());
        String img = llm.imageToBase64("/tmp/abc.png", "png");
        LLM.LLMCompoundMsg m = LLM.getLlmCompoundMsg("帮我点击下页面截图中的 <开始使用>",
                Message.builder()
                        .images(Lists.newArrayList(img))
                        .build());
        m.setImageType("png");
        Flux<String> flux = llm.compoundMsgCall(m
                , Prompt.systemPrompt);
        StringBuilder sb = new StringBuilder();
        flux.subscribe(msg -> {
            System.out.println(msg);
            sb.append(msg);
        });

        flux.blockLast();
        System.out.println(sb);
    }


    @Test
    public void test2() {
        String msg = """
                Thought: 我看到左侧导航栏有个"开始使用"的选项，这正是我需要点击的。它位于导航栏的最上方，点击它应该能带我进入使用指南的页面。让我移动鼠标过去点击一下。
                Action: click(start_box='<bbox>162 118 162 118</bbox>')
                """;

        String res = ActionResponseParser.parseActionOutput(msg);
        System.out.println(res);

    }

}
