package run.mone.mcp.yijing.function;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.yijing.service.YijingService;

import java.util.List;
import java.util.Map;

/**
 * 易经数字卦计算Function
 *
 * @author assistant
 */
@Component
@RequiredArgsConstructor
public class YijingNumberGuaFunction implements McpFunction {

    private static final Logger log = LoggerFactory.getLogger(YijingNumberGuaFunction.class);

    private YijingService yijingService;

    public YijingNumberGuaFunction(YijingService yijingService) {
        this.yijingService = yijingService;
    }

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "num1": {
                        "type": "integer",
                        "description": "第一个数字，用于计算下卦"
                    },
                    "num2": {
                        "type": "integer", 
                        "description": "第二个数字，用于计算上卦"
                    },
                    "num3": {
                        "type": "integer",
                        "description": "第三个数字，用于计算变爻"
                    },
                    "question": {
                        "type": "string",
                        "description": "用户要咨询的问题"
                    }
                },
                "required": ["num1", "num2", "num3", "question"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        return Flux.defer(() -> {
            try {
                // 验证必需参数
                if (!arguments.containsKey("num1") || !arguments.containsKey("num2") || 
                    !arguments.containsKey("num3") || !arguments.containsKey("question")) {
                    log.error("易经数字卦计算缺少必需参数");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("缺少必需参数：num1, num2, num3, question")), 
                        true));
                }

                int num1 = ((Number) arguments.get("num1")).intValue();
                int num2 = ((Number) arguments.get("num2")).intValue();
                int num3 = ((Number) arguments.get("num3")).intValue();
                String question = (String) arguments.get("question");

                log.info("开始易经数字卦计算，数字：{}, {}, {}，问题：{}", num1, num2, num3, question);

                // 获取卦象计算结果
                Map<String, String> guaResult = yijingService.getGuaCalculation(num1, num2, num3);
                String lowerGua = guaResult.get("lowerGua");
                String upperGua = guaResult.get("upperGua");
                String changeYao = guaResult.get("changeYao");
                String compoundGua = guaResult.get("compoundGua");

                // 调用YijingService进行LLM解析，直接返回Flux
                return yijingService.calculateYijingGua(num1, num2, num3, question)
                    .map(analysis -> {
                        StringBuilder result = new StringBuilder();
                        result.append("=== 易经数字卦计算结果 ===\n\n");
                        result.append("下卦：").append(lowerGua).append("\n");
                        result.append("上卦：").append(upperGua).append("\n");
                        result.append("变爻：").append(changeYao).append("\n");
                        result.append("复卦：").append(compoundGua).append("\n");
                        result.append("问题：").append(question).append("\n\n");
                        result.append("=== 易经解析 ===\n");
                        result.append(analysis);
                        
                        return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(result.toString())), 
                            false);
                    })
                    .onErrorReturn(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("易经解析过程中发生错误")), 
                        true));

            } catch (Exception e) {
                log.error("易经数字卦计算处理发生异常", e);
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("计算失败: " + e.getMessage())), 
                    true));
            }
        });
    }


    @Override
    public String getName() {
        return "yijing_number_gua";
    }

    @Override
    public String getDesc() {
        return "易经数字卦计算工具，根据用户提供的三个随机数字和问题，进行数字卦计算和分析。支持计算下卦、上卦、变爻，组合成复卦，并结合问题给出易经解析。";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
