package run.mone.m78.service.bo.chatgpt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/5/25 10:25
 */
@Slf4j
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ChatCompletion implements Serializable {


    @NonNull
    @Builder.Default
    private String model = Model.GPT_3_5_TURBO.getName();

    //支持json格式返回
    @Setter
    private Format response_format;
    /**
     * 问题描述
     */
//    @NonNull
    private List<Message> messages;

    private List<Function> functions;

    /**
     * 使用什么取样温度，0到2之间。较高的值(如0.8)将使输出更加随机，而较低的值(如0.2)将使输出更加集中和确定。
     * <p>
     * We generally recommend altering this or but not both.top_p
     */
    @Builder.Default
    private double temperature = 0.2;

    /**
     * 使用温度采样的替代方法称为核心采样，其中模型考虑具有top_p概率质量的令牌的结果。因此，0.1 意味着只考虑包含前 10% 概率质量的代币。
     * <p>
     * 我们通常建议更改此设置，但不要同时更改两者。temperature
     */
    @JsonProperty("top_p")
    @Builder.Default
    private Double topP = 1d;


    /**
     * 为每个提示生成的完成次数。
     */
    @Builder.Default
    private Integer n = 1;


    /**
     * 是否流式输出.
     * default:false
     */
    @Builder.Default
    private boolean stream = true;
    /**
     * 最大支持4096
     */
    @JsonProperty("max_tokens")
    @Builder.Default
    private Integer maxTokens = 4096;


    @JsonProperty("presence_penalty")
    @Builder.Default
    private double presencePenalty = 0;

    /**
     * -2.0 ~~ 2.0
     */
    @JsonProperty("frequency_penalty")
    @Builder.Default
    private double frequencyPenalty = 0;

    /**
     * 补全的prompt参数 等同于chat completions中的message
     */
    @JsonProperty("prompt")
    private List<String> prompt;

    /**
     * 插入文本完成后出现的后缀
     */
    @JsonProperty("suffix")
    private String suffix;

    public void setModel(Model model) {
        this.model = model.getName();
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public void setTemperature(double temperature) {
        if (temperature > 2 || temperature < 0) {
            log.error("temperature参数异常，temperature属于[0,2]");
            this.temperature = 2;
            return;
        }
        if (temperature < 0) {
            log.error("temperature参数异常，temperature属于[0,2]");
            this.temperature = 0;
            return;
        }
        this.temperature = temperature;
    }


    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public void setPresencePenalty(double presencePenalty) {
        if (presencePenalty < -2.0) {
            this.presencePenalty = -2.0;
            return;
        }
        if (presencePenalty > 2.0) {
            this.presencePenalty = 2.0;
            return;
        }
        this.presencePenalty = presencePenalty;
    }

    public void setFrequencyPenalty(double frequencyPenalty) {
        if (frequencyPenalty < -2.0) {
            this.frequencyPenalty = -2.0;
            return;
        }
        if (frequencyPenalty > 2.0) {
            this.frequencyPenalty = 2.0;
            return;
        }
        this.frequencyPenalty = frequencyPenalty;
    }


    @Getter
    @AllArgsConstructor
    public enum Model {
        GPT_3_5_TURBO("gpt-3.5-turbo"),
        ;
        private String name;
    }

}
