package run.mone.m78.ip.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/9 14:32
 */
@Data
@Builder
public class AiMessage implements Serializable {

    private AiMessageType type;

    private String id;

    private String text;

    private String projectName;

    //代表是否是编码(```code```)
    @Builder.Default
    private boolean code = true;

}
