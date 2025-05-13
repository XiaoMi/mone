package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.roles.ReactorRole;

import java.io.IOException;
import java.util.Base64;

/**
 * 文字转语音工具
 *
 * @author dingtao
 */
@Slf4j
public class TextToSpeechTool implements ITool {

    @Override
    public String getName() {
        return "text_to_speech";
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
                A tool designed to convert text into speech.
                Use this tool when the user wants to transform text content into audible speech format.
                
                **When to use:** Choose this tool when the user needs to convert written text into 
                speech that can be played or saved as an audio file.
                
                **Output:** The tool will return Base64-encoded audio data that can be directly used 
                for playback or saved as an audio file.
                """;
    }

    @Override
    public String parameters() {
        return """
                - text: (required) The text content to be converted to speech.
                """;
    }

    @Override
    public String usage() {
        return """
                (Attention: If you are using this tool, you MUST return the speech information within the <text_to_speech> tag):
                
                Example: Converting text to speech
                <text_to_speech>
                  <text>Hello, this is a test text that will be converted to speech.</text>
                  <result>
                    [Base64-encoded audio data]
                  </result>
                </text_to_speech>
                """;
    }

    /**
     * 返回值不希望参与下次大模型调用，可以在使用后擦除
     * 例如可以在interceptor里做处理
     *
     * @param inputJson
     * @return
     */
    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        // 检查必需参数
        if (!inputJson.has("text") || StringUtils.isBlank(inputJson.get("text").getAsString())) {
            log.error("文字转语音请求缺少必需的文本参数");
            result.addProperty("error", "缺少必需参数'text'");
            return result;
        }

        // 获取参数
        String text = inputJson.get("text").getAsString();

        log.info("开始文字转语音处理，文本长度：{}", text.length());

        try {
            // 创建LLM实例
            LLM llm = new LLM(LLMConfig.builder()
                    .llmProvider(LLMProvider.STEPFUN_TTS)
                    .build());

            // 调用LLM进行语音合成
            byte[] audioData = llm.generateSpeech(text);

            // 检查结果
            if (audioData == null || audioData.length == 0) {
                log.error("文字转语音失败：返回的音频数据为空");
                result.addProperty("error", "语音合成失败：返回的音频数据为空");
                return result;
            }

            // 将音频数据编码为Base64字符串
            String base64Audio = Base64.getEncoder().encodeToString(audioData);
            result.addProperty("result", base64Audio);
            result.addProperty("toolMsgType", ToolMsgType.VOICE);

            log.info("文字转语音处理成功，生成的音频数据大小：{} 字节", audioData.length);
            return result;
        } catch (IOException e) {
            log.error("文字转语音处理发生IO异常", e);
            result.addProperty("error", "语音合成失败: " + e.getMessage());
            return result;
        } catch (Exception e) {
            log.error("文字转语音处理发生未预期异常", e);
            result.addProperty("error", "语音合成失败: " + e.getMessage());
            return result;
        }
    }
} 