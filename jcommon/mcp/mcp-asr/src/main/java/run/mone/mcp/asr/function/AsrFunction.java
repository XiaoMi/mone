package run.mone.mcp.asr.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.asr.service.AliAsrService;
import run.mone.mcp.asr.service.TencentAsrService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author 龚文
 */
@Data
@Slf4j
public class AsrFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private String name = "stream_asr";
    private String desc = "语音识别功能";
    private TencentAsrService tencentAsrService;
    private AliAsrService aliAsrService;

    private String toolScheme = """
            {
                  "type": "object",
                  "properties": {
                      "type": {
                          "type": "string",
                          "enum": ["tencent","ali"],
                          "description": "asr供应商：腾讯,阿里"
                      },
                      "fileName": {
                          "type": "String",
                          "description": "需要进行语音识别音频文件"
                      }
                  },
                  "required": ["type","fileName"]
              }
            """;

    public AsrFunction(TencentAsrService tencentAsrService,AliAsrService aliAsrService) {

        this.tencentAsrService = tencentAsrService;
        this.aliAsrService = aliAsrService;
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {

        String type = (String) arguments.get("type");
        String fileName = (String) arguments.get("fileName");

        switch (type) {
            case "tencent" -> {
                return tencentAsrService.doAsr(fileName)
                        .map(message -> new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(message)),
                                false
                        ))
                        .onErrorResume(error -> {
                            log.error("error in do tencent asr", error);
                            return Flux.just(new McpSchema.CallToolResult(
                                    List.of(new McpSchema.TextContent("Error: " + error.getMessage())),
                                    true
                            ));
                        });
            }
            case "ali" -> {
                return aliAsrService.doAsr(fileName)
                        .map(message -> new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(message)),
                                false
                        ))
                        .onErrorResume(error -> {
                            log.error("error in do ali asr", error);
                            return Flux.just(new McpSchema.CallToolResult(
                                    List.of(new McpSchema.TextContent("Error: " + error.getMessage())),
                                    true
                            ));
                        });
            }
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        }
    }



}
