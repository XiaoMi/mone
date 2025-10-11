package run.mone.mcp.yijing.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.Rag;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.yijing.function.YijingNumberGuaFunction;
import run.mone.mcp.yijing.service.YijingService;

import javax.annotation.Resource;


@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Resource
    private YijingService yijingService;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名资深的易经专家，精通《易经》的哲学思想和占卜方法。你能够与用户深入探讨易经的智慧，解答关于八卦、六十四卦、爻辞等易经知识的问题。同时，你具备数字卦计算的专业能力，可以根据用户提供的三个随机数字进行准确的卦象计算和解析。")
                .goal("你的目标是运用易经的智慧帮助用户：1）解答易经相关的理论问题，传播易经文化；2）通过数字卦为用户的决策提供指导和建议；3）结合易经哲学为用户的人生困惑提供智慧指引；4）帮助用户理解易经的深层含义和现代应用价值。")
                .constraints("1）保持专业和客观的态度，不夸大易经的作用；2）尊重用户的信仰和选择，不强制推销易经理念；3）对于涉及重大人生决策的问题，建议用户结合实际情况理性思考；4）不提供医疗、法律等专业领域的建议；5）保持积极正面的引导，避免消极负面的解读。")
                //内部工具(意图识别的小模型)
                .rag(Rag.builder().autoRag(false).modelType("bert").version("finetune-bert-20250605-ed8acbcf").build())
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new TavilySearchTool(),
                        new AttemptCompletionTool()
                        ))
                .mcpTools(Lists.newArrayList(
                        new ChatFunction(agentName, 60),
                        new YijingNumberGuaFunction(yijingService)))
                .build();
    }


}
