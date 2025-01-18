package run.mone.m78.service.dao.entity;

import com.google.gson.JsonElement;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.mybatisflex.core.handler.GsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.api.bo.flow.SyncFlowStatus;
import run.mone.m78.service.dao.InputMapTypeHandler;
import run.mone.m78.service.dao.OutputMapTypeHandler;

import java.util.Map;

import static run.mone.m78.api.constant.TableConstant.FLOW_TEST_RECORD_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(FLOW_TEST_RECORD_TABLE)
public class FlowTestRecordPo {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("execute_type")
    private int executeType;

    @Column("flow_base_id")
    private Integer flowBaseId;

    @Column("start_time")
    private long startTime;

    @Column("status")
    private int status;

    @Column("runner")
    private String runner;

    @Column("duration")
    private long duration;

    @Column(value = "input", typeHandler = GsonTypeHandler.class)
    private Map<String, JsonElement> input;
    @Column(value = "end_flow_output", typeHandler = Fastjson2TypeHandler.class)
    private SyncFlowStatus.EndFlowOutput endFlowOutput;
    @Column(value = "node_inputs_map", typeHandler = InputMapTypeHandler.class)
    private Map<String, SyncFlowStatus.SyncNodeInput> nodeInputsMap;
    @Column(value = "node_outputs_map", typeHandler = OutputMapTypeHandler.class)
    private Map<String, SyncFlowStatus.SyncNodeOutput> nodeOutputsMap;

}
