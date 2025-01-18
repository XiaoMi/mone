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
public class AddOrUpdateCardDetailReq {

    private String rootUniqueKey;

    private Map<String, CardElement> elementMap;

    private Long workspaceId;

    private Long cardId;
}
