package run.mone.mcp.asr.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
                          "description": "asr供应商：腾讯,阿里，不指定时默认使用腾讯"
                      },
                      "voiceBase64": {
                          "type": "String",
                          "description": "base64编码音频数据"
                      },
                      "fileName": {
                          "type": "String",
                          "description": "需要进行语音识别音频文件"
                      },
                      "base64AudioFormat": {
                          "type": "String",
                          "enum": ["pcm","mp3","wav","speex","silk","opus","m4a"],
                          "description": "音频编码，使用base64编码音频数据时必传，不指定时默认为mp3"
                      }
                  }
              }
            """;

    public AsrFunction(TencentAsrService tencentAsrService,AliAsrService aliAsrService) {

        this.tencentAsrService = tencentAsrService;
        this.aliAsrService = aliAsrService;
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {

        String type = (String) arguments.getOrDefault("type", "tencent");
        String fileName = (String) arguments.get("fileName");
        String base64Audio = (String) arguments.get("voiceBase64");
        String base64AudioFormat = (String) arguments.getOrDefault("base64AudioFormat", "mp3");

        if(StringUtils.isBlank(fileName) && (StringUtils.isBlank(base64Audio) || StringUtils.isBlank(base64AudioFormat))){
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: fileName and base64Audio can not be null")),
                    true
            ));
        }

        switch (type) {
            case "tencent" -> {
                if(StringUtils.isNotBlank(base64AudioFormat)){
                    tencentAsrService.setBase64AudioFormat(base64AudioFormat);
                }
                return tencentAsrService.doAsr(fileName, base64Audio)
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
                if(StringUtils.isNotBlank(base64AudioFormat)){
                    aliAsrService.setBase64AudioFormat(base64AudioFormat);
                }
                return aliAsrService.doAsr(fileName, base64Audio)
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
