package run.mone.openai;

import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/5/9 09:00
 */
@Builder
@Data
public class ReqConfig {

    @Builder.Default
    private int maxTokens = 4096;

    @Builder.Default
    private double temperature = 0.2f;

    @Builder.Default
    private String model = ChatCompletion.Model.GPT_3_5_TURBO.getName();

}
