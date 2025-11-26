package run.mone.mcp.minimaxrealtime.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.mcp.minimaxrealtime.model.RealtimeMessage;

import java.util.Arrays;
import java.util.List;

/**
 * Realtime 配置帮助类
 * @author renqingfu
 * @Date 2025/5/22 16:30
 */
@Component
public class RealtimeConfigHelper {

    @Autowired
    private WebSocketConfig webSocketConfig;

    /**
     * 获取默认会话配置
     */
    public RealtimeMessage.SessionConfig getDefaultSessionConfig() {
        RealtimeMessage.SessionConfig config = new RealtimeMessage.SessionConfig();
        config.setModalities(Arrays.asList("text", "audio"));
        config.setInstructions(webSocketConfig.getDefaultInstructions());
        config.setVoice(webSocketConfig.getDefaultVoice());
        config.setInputAudioFormat(webSocketConfig.getDefaultInputAudioFormat());
        config.setOutputAudioFormat(webSocketConfig.getDefaultOutputAudioFormat());
        config.setTemperature(webSocketConfig.getDefaultTemperature());
        config.setMaxResponseOutputTokens(webSocketConfig.getDefaultMaxResponseOutputTokens());
        return config;
    }

    /**
     * 获取默认响应配置
     */
    public RealtimeMessage.ResponseConfig getDefaultResponseConfig() {
        RealtimeMessage.ResponseConfig config = new RealtimeMessage.ResponseConfig();
        config.setModalities(Arrays.asList("text", "audio"));
        config.setVoice(webSocketConfig.getDefaultVoice());
        config.setOutputAudioFormat(webSocketConfig.getDefaultOutputAudioFormat());
        config.setTemperature(webSocketConfig.getDefaultTemperature());
        config.setMaxOutputTokens(Integer.parseInt(webSocketConfig.getDefaultMaxResponseOutputTokens()));
        return config;
    }

    /**
     * 验证语音类型是否支持
     */
    public boolean isValidVoice(String voice) {
        List<String> supportedVoices = Arrays.asList(
                "female-yujie", "male-qingfeng", "female-sichuan", "male-beijing"
        );
        return supportedVoices.contains(voice);
    }

    /**
     * 验证音频格式是否支持
     */
    public boolean isValidAudioFormat(String format) {
        List<String> supportedFormats = Arrays.asList(
                "pcm16", "g711_ulaw", "g711_alaw"
        );
        return supportedFormats.contains(format);
    }

    /**
     * 验证模态是否支持
     */
    public boolean isValidModality(String modality) {
        List<String> supportedModalities = Arrays.asList("text", "audio");
        return supportedModalities.contains(modality);
    }

    /**
     * 获取支持的语音类型列表
     */
    public List<String> getSupportedVoices() {
        return Arrays.asList(
                "female-yujie", "male-qingfeng", "female-sichuan", "male-beijing"
        );
    }

    /**
     * 获取支持的音频格式列表
     */
    public List<String> getSupportedAudioFormats() {
        return Arrays.asList("pcm16", "g711_ulaw", "g711_alaw");
    }

    /**
     * 获取支持的模态列表
     */
    public List<String> getSupportedModalities() {
        return Arrays.asList("text", "audio");
    }
} 