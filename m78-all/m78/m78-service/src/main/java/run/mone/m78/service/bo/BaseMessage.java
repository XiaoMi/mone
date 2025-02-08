package run.mone.m78.service.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/5/9 10:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseMessage implements Serializable {

    private MetaInfo metaInfo;

    private int code;

    private String msg;

    private String cmd;

    private String id;

    private String traceId;

    //text vision
    @Builder.Default
    private String messageType = "text";

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Data
    public static class MetaInfo {
        private Integer appId;
        private String id;
        private String type;
        private boolean end;
    }
}
