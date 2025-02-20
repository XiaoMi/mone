package run.mone.local.docean.fsm.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/6/27 09:53
 */
@Data
public class ManualConfirmReq implements Serializable {

    private String nodeId;

    private String cmd;

    private String message;

    private Map<String,String> meta;

}
