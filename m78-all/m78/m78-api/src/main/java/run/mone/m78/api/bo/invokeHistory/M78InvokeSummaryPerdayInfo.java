package run.mone.m78.api.bo.invokeHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class M78InvokeSummaryPerdayInfo {

    private Long id;

    private Integer type; // 类型, 1bot, 2flow, 3plugin

    private Long relateId;

    private Long invokeCounts;

    private Long invokeUsers;

    private Long invokeDay;

    private String relateName;

    private String avatarUrl;
}
