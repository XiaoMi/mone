package run.mone.m78.api.bo.invokeHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InvokeHistoryListReq {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int pageSize = 30;

    private Integer type; // 类型, 1bot, 2flow, 3plugin

    private Long relateId;

    private Long invokeTimeBegin;

    private Long invokeTimeEnd;

    private String invokeUserName;

    private String orderBy;

    //是否逆序
    private boolean isAsc;

}
