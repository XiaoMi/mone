package run.mone.m78.api.bo.card;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GetCardBindReq {

    private Long botId;

    private Long relateId;

    private String type;
}
