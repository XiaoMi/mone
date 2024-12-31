package run.mone.local.docean.fsm.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 16:24
 */
@Data
@Builder
public class FlowData implements Serializable {

    private String flowRecordId;

    private int id;

    private String name;

    private String type;

    @Builder.Default
    private LinkedHashMap<String, InputData> inputMap = new LinkedHashMap<>();

    @Builder.Default
    private LinkedHashMap<String, OutputData> outputMap = new LinkedHashMap<>();

    @Builder.Default
    private LinkedHashMap<String, InputData> batchMap = new LinkedHashMap<>();

    private Map<String, String> flowMeta;

    @Builder.Default
    private boolean debug = false;

}
