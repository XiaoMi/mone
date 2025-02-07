package run.mone.m78.service.agent.multiagent.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/9/12 14:04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BotParam implements Serializable {

    private String input;

    private String botId;

}
