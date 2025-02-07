package run.mone.m78.api.bo.card;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 卡片绑定信息
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CardBind {

    private Long id;

    private Long botId;

    private Long cardId;

    private Long relateId;

    private String type;

    private Map<String, String> bindDetail;

    private Long ctime;

    private Long utime;
}
