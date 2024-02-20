package run.mone.ultraman.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/4/28 10:55
 */
@Data
public class AiReq implements Serializable {

    private String cmd;

    private String ai;

    private String project;

    private String module;

    private boolean mute;

    private String param;

    private Map<String, String> meta;

}
