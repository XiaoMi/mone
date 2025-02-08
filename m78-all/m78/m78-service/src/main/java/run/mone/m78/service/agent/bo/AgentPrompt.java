package run.mone.m78.service.agent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/9/11 09:37
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentPrompt implements Serializable {

    private String currentPrompt;

    private List<String> history;

    private int round;

    private int totalRound;

}
