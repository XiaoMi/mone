package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;

import java.util.UUID;

/**
 * 语音转文字工具
 * @author dingtao
 */
@Slf4j
public class SpeechToTextTool implements ITool {

    @Override
    public String getName() {
        return "speech_to_text";
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
            A tool designed to convert speech into text.
            Use this tool when the user wants to transform audio content into readable text format.
            
            **When to use:** Choose this tool when the user needs to convert speech from audio files 
            or audio data into readable text content.
            
            **Output:** The tool will return the text content recognized from the speech.
            """;
    }

    @Override
    public String parameters() {
        return """
                """;
    }

    @Override
    public String usage() {
        return """
            (Attention: If you are using this tool, you MUST return the text information within the <speech_to_text> tag):
            
            Example: Converting speech to text
            <speech_to_text>
              <result>
                [Text content recognized from speech]
              </result>
            </speech_to_text>
            """;
    }

    /**
     * 希望调用方能够直接传递base64入参，而不是让大模型返回
     * 例如可以在interceptor里面传递
     *
     * @param inputJson
     * @return
     */
    @Override
    public JsonObject execute(JsonObject inputJson) {
        JsonObject result = new JsonObject();
        
        try {
            // 获取参数
            if (!inputJson.has("base64") || StringUtils.isBlank(inputJson.get("base64").getAsString())) {
                log.error("语音转文字请求缺少必需的base64参数");
                result.addProperty("error", "缺少必需参数'base64'");
                return result;
            }
            
            String base64 = inputJson.get("base64").getAsString();
            log.info("开始语音转文字处理，base64数据长度：{}", base64.length());
            
            String filePath = System.getProperty("user.home") + "/" + UUID.randomUUID() + ".mp3";
            
            // 检查base64是否包含data:开头的格式，如果是则需要裁剪
            if (base64.startsWith("data:")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }
            
            // 创建LLM实例并调用转换方法
            String text = new LLM(LLMConfig.builder().llmProvider(LLMProvider.STEPFUN_ASR).build()).transcribeAudio(filePath, base64);
            
            // 检查结果
            if (StringUtils.isBlank(text)) {
                log.error("语音转文字失败：返回的文本数据为空");
                result.addProperty("error", "语音识别失败：返回的文本数据为空");
                return result;
            }
            
            result.addProperty("result", text);
            log.info("语音转文字处理成功，识别文本：{}", text);
            return result;
            
        } catch (Exception e) {
            log.error("语音转文字处理发生异常", e);
            result.addProperty("error", "语音识别失败: " + e.getMessage());
            return result;
        }
    }
} 