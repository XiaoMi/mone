package run.mone.m78.service.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/5/23
 */
@Data
@Builder
public class ReqChatSummaryListDto implements Serializable {
    private String username;
    private Integer botId;
    private Long startTime;
    private Integer priority;
}
