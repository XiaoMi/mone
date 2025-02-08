package run.mone.m78.service.agent.multiagent.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/9/12 11:06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinalSummary implements Serializable {

    private List<String> summary;

    private String summaryStr;

}
