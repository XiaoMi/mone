package run.mone.m78.service.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/3/6 20:33
 */
@Data
@Builder
public class BotReq {

    private BotVo botVo;

    private String message;

    private Long botId;

    private String dbInfo;

    private String msgType;

    // 外部传入的历史记录
    private String externalHistory;
}

