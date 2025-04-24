package run.mone.mcp.tts.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.tts.service.AliTtsService;
import run.mone.mcp.tts.service.TencentTtsService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author 龚文
 */
@Data
@Slf4j
public class TtsFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {
    private TencentTtsService tencentTtsService;
    private AliTtsService aliTtsService;

    private String name = "stream_tts";
    private String desc = "语音合成功能";

    private String toolScheme = """
            {
                 "type": "object",
                 "properties": {
                     "type": {
                         "type": "string",
                         "enum": ["tencent","ali"],
                         "description": "tts供应商：腾讯,阿里"
                     },
                     "isCreateAudioFile": {
                         "type": "string",
                         "enum": ["true","false"],
                         "description": "是否创建音频文件：ture是，false否"
                     },
                     "isPlay": {
                         "type": "string",
                         "enum": ["true","false"],
                         "description": "是否播放：ture是，false否"
                     },
                     "isOutputBase64": {
                         "type": "string",
                         "enum": ["true","false"],
                         "description": "是否输出base64编码音频数据：ture是，false否"
                     },
                     "textString": {
                         "type": "string",
                         "description": "需要合成的文本字符串内容"
                     }
                 },
                 "required": [
                     "type",
                     "textString"
                 ]
             }
            """;


    public TtsFunction(TencentTtsService tencentTtsService, AliTtsService aliTtsService) {
        this.tencentTtsService = tencentTtsService;
        this.aliTtsService = aliTtsService;
    }


    // 语音合成,流式返回二进制内容
    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {

        String type = (String) arguments.get("type");
        String isCreateAudioFile = arguments.get("isCreateAudioFile") != null ? (String) arguments.get(
                "isCreateAudioFile") : "false";
        String isPlay = arguments.get("isPlay") != null ? (String) arguments.get("isPlay") : "false";
        String isOutputBase64 = arguments.get("isOutputBase64") != null ? (String) arguments.get("isOutputBase64") :
                "false";
        String textString = (String) arguments.get("textString");

        log.info("textString: {}", textString);
        switch (type) {
            case "tencent" -> {
                tencentTtsService.setIsCreateAudioFile(isCreateAudioFile);
                tencentTtsService.setIsPlay(isPlay);
                tencentTtsService.setIsOutputBase64(isOutputBase64);
                return tencentTtsService.doTts(textString)
                        .map(message -> new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(message)),
                                false
                        ))
                        .onErrorResume(error -> {
                            log.error("error in do tencent tts", error);
                            return Flux.just(new McpSchema.CallToolResult(
                                    List.of(new McpSchema.TextContent("Error: " + error.getMessage())),
                                    true
                            ));
                        });
            }
            case "ali" -> {
                aliTtsService.setIsCreateAudioFile(isCreateAudioFile);
                aliTtsService.setIsPlay(isPlay);
                aliTtsService.setIsOutputBase64(isOutputBase64);
                return aliTtsService.doTts(textString)
                        .map(message -> new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(message)),
                                false
                        ))
                        .onErrorResume(error -> {
                            log.error("error in do ali tts", error);
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
