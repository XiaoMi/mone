package run.mone.m78.service.agent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/5/22 10:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BotStateResult implements Serializable {

    private int code;

    private String message;

    private String data;

    private Map<String,String> meta;

    private String messageType;

    private String traceId;

}
