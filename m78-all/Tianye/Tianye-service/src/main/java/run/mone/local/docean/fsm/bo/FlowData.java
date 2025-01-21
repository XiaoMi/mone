package run.mone.local.docean.fsm.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 16:24
 */
@Data
@Builder
public class FlowData implements Serializable {

    private String flowId;

    private String flowRecordId;

    private int executeType;

    private int id;

    private String name;

    private String type;

    @Builder.Default
    private ConcurrentHashMap<String, InputData> inputMap = new ConcurrentHashMap<>();

    @Builder.Default
    private ConcurrentHashMap<String, OutputData> outputMap = new ConcurrentHashMap<>();

    @Builder.Default
    private ConcurrentHashMap<String, InputData> batchMap = new ConcurrentHashMap<>();

    private Map<String, String> flowMeta;

    @Builder.Default
    private boolean debug = false;

    private boolean singleNodeTest;

}
