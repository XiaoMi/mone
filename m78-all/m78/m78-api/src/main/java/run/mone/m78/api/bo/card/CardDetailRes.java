package run.mone.m78.api.bo.card;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CardDetailRes {

    private Card card;

    private String rootUniqueKey;

    private Map<String, CardElement> elementMap;

    private Map<String, CardVariable> cardVariableMap;

    private Map<String, CardVariable> usedCardVariableMap;

}
