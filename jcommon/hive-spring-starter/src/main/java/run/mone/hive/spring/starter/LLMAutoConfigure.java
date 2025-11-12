package run.mone.hive.spring.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.CustomConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;

import java.util.Locale;

import static run.mone.hive.llm.ClaudeProxy.*;

/**
 * LLM 自动配置类
 * 
 * 独立于 hive.starter.enabled 开关，始终会创建 LLM bean
 * 这样即使禁用了 starter，LLM bean 仍然可以正常使用
 * 
 * @author shanwb
 */
@Configuration
@Slf4j
public class LLMAutoConfigure {

    @Value("${mcp.llm:CLAUDE_COMPANY}")
    private String llmType;

    /**
     * 大模型 Bean
     * 不受 hive.starter.enabled 控制，始终会创建
     */
    @Bean
    @ConditionalOnMissingBean
    public LLM llm() {
        llmType = llmType.toLowerCase(Locale.ROOT);
        if (LLMProvider.CLAUDE_COMPANY.name().equalsIgnoreCase(llmType)) {
            LLMConfig config = LLMConfig.builder()
                    .llmProvider(LLMProvider.CLAUDE_COMPANY)
                    .url(getClaudeUrl())
                    .version(getClaudeVersion())
                    .maxTokens(getClaudeMaxToekns())
                    .build();
            return new LLM(config);
        }
        //使用deepseek 原生的v3
        if (LLMProvider.DEEPSEEK.name().toLowerCase(Locale.ROOT).equals(llmType)) {
            return new LLM(LLMConfig.builder().llmProvider(LLMProvider.DEEPSEEK).build());
        }
        //使用字节的deepseek v3
        if (LLMProvider.DOUBAO_DEEPSEEK_V3.name().toLowerCase(Locale.ROOT).equals(llmType)) {
            return new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO_DEEPSEEK_V3).build());
        }

        if (LLMProvider.GOOGLE_2.name().toLowerCase(Locale.ROOT).equals(llmType)) {
            LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.GOOGLE_2).build();
            config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "streamGenerateContent?alt=sse");
            return new LLM(config);
        }
        if (LLMProvider.OPENAICOMPATIBLE.name().toLowerCase(Locale.ROOT).equals(llmType)) {
            LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.OPENAICOMPATIBLE).build();
            config.setUrl(System.getenv("OPENAI_COMPATIBLE_URL"));
            config.setModel(System.getenv("OPENAI_COMPATIBLE_MODEL"));
            config.setToken(System.getenv("OPENAI_COMPATIBLE_TOKEN"));
            return new LLM(config);
        }
        if (LLMProvider.OPENAI_MULTIMODAL_COMPATIBLE.name().toLowerCase(Locale.ROOT).equals(llmType)) {
            LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.OPENAI_MULTIMODAL_COMPATIBLE).build();
            config.setUrl(System.getenv("OPENAI_COMPATIBLE_URL"));
            config.setModel(System.getenv("OPENAI_COMPATIBLE_MODEL"));
            config.setToken(System.getenv("OPENAI_COMPATIBLE_TOKEN"));
            return new LLM(config);
        }

        if (LLMProvider.MIFY_GATEWAY.name().toLowerCase(Locale.ROOT).equals(llmType)) {
            LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.MIFY_GATEWAY).build();
            config.setUrl(System.getenv("MIFY_GATEWAY_URL"));
            config.setToken(System.getenv("MIFY_API_KEY"));
            CustomConfig customConfig = new CustomConfig();
            customConfig.setModel(System.getenv("MIFY_MODEL"));
            customConfig.addCustomHeader(CustomConfig.X_MODEL_PROVIDER_ID, System.getenv("MIFY_MODEL_PROVIDER_ID"));
            config.setCustomConfig(customConfig);
            return new LLM(config);
        }

        return new LLM(LLMConfig.builder().llmProvider(LLMProvider.valueOf(llmType.toUpperCase(Locale.ROOT))).build());
    }
}

