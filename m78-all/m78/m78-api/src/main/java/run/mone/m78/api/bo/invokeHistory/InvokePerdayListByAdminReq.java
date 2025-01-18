package run.mone.m78.api.bo.invokeHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InvokePerdayListByAdminReq {

    private Long invokeTimeBegin;

    private Long invokeTimeEnd;
}
