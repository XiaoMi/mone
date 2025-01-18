package run.mone.m78.service.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2024/5/7 13:59
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiProxyMessage {

    private String type;

    private String message;

    private String msgId;

}
