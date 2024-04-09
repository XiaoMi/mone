package run.mone.local.docean.fsm.bo;

import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/3/4 15:29
 */
@Data
@Builder
public class EndFlowRes implements Serializable {
    private int code;

    private String message;

    private String answerContent;

    private Map<String, JsonElement> data;

    private List<SyncFlowStatus.EndFlowOutputDetail> endFlowOutputDetails;

}
