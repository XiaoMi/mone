package run.mone.m78.api.bo.im;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-25 16:39
 */
@Data
public class ExecuteBotReqDTO implements Serializable {

    private String username;
    private Long botId;
    private String input;
    private String topicId;

}
