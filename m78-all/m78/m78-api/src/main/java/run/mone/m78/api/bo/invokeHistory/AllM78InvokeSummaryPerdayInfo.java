package run.mone.m78.api.bo.invokeHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AllM78InvokeSummaryPerdayInfo {

    private Integer type; // 类型, 1bot, 2flow, 3plugin

    private Long allInvokeCounts;

    private Long allInvokeUsers;
}
