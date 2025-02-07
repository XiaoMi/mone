package run.mone.mcp.solution.assessor;


import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatCompletionUserMessageParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class SolutionAssessorFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "solution_assessor";

    private String desc = "提供问题和相应解决方案的评估和建议;每当计划好要做的事情时，可以在执行前咨询solution-assessor寻求建议，如果发现有问题请修正。solution-assessor只用于执行前的评估，不用与执行后的总结";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "problem": {"type": "string", "description": "待解决的问题"},
                    "solution": {"type": "string", "description": "针对该问题设计的解决方案"}
                },
                "required": ["problem", "solution"]
            }
            """;

    private OpenAIClient client;
    private String model;

    public SolutionAssessorFunction() {
        String baseUrl = System.getenv("OPENAI_BASE_URL");
        String apiKey = System.getenv("OPENAI_API_KEY");
        String model = System.getenv("OPENAI_MODEL");

        if (model == null || model.isEmpty()) {
            model = "gpt-4o-mini";
        }
        OpenAIOkHttpClient.Builder builder = OpenAIOkHttpClient.builder().apiKey(apiKey);
        if (!StringUtils.isEmpty(baseUrl)){
            builder.baseUrl(baseUrl);
        }
        OpenAIClient client= builder.build();
        this.client = client;
        this.model = model;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String problem = (String) arguments.get("problem");
        String solution = (String) arguments.get("solution");
        log.info("problem: {} solution: {}", problem, solution);

        try {
            ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                    .model(this.model)
                    .addMessage(
                            ChatCompletionUserMessageParam.builder()
                                    .role(ChatCompletionUserMessageParam.Role.of("system"))
                                    .content("请评估以下问题和解决方案，并指出是否存在问题，或者是否需要提问者提供更多信息来澄清问题。回答请尽量简洁清晰，直奔主题")
                                    .build())
                    .addMessage(ChatCompletionUserMessageParam.builder()
                            .role(ChatCompletionUserMessageParam.Role.USER)
                            .content(String.format("问题: %s\\n解决方案: %s", problem, solution))
                            .build()

                    )
                    .temperature(0.0)
                    .build();
            ChatCompletion chatCompletion = client.chat().completions().create(request);
            String response = chatCompletion.choices().get(0).message().content().get();
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(response)), false);
        } catch (Exception e) {
            String errorMsg = String.format("调用 OpenAI API 失败: %s，你可以认为没有问题", e.getMessage());
            log.error(errorMsg, e);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(errorMsg)), true);
        }
    }
}
