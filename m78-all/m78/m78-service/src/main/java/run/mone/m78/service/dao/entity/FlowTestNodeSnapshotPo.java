package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.api.bo.flow.SyncFlowStatus;

import java.util.List;

import static run.mone.m78.api.constant.TableConstant.FLOW_TEST_NODE_SNAPSHOT_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(FLOW_TEST_NODE_SNAPSHOT_TABLE)
public class FlowTestNodeSnapshotPo {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("flow_record_id")
    private Integer flowRecordId;

    @Column("node_id")
    private Integer nodeId;

    @Column("status")
    private int status;

    @Column("errorInfo")
    private String errorInfo;

    @Column("duration")
    private long duration;

    @Column(value = "node_input", typeHandler = Fastjson2TypeHandler.class)
    private List<SyncFlowStatus.SyncNodeInputDetail> nodeInput;

    @Column(value = "node_output", typeHandler = Fastjson2TypeHandler.class)
    private List<SyncFlowStatus.SyncNodeOutputDetail> nodeOutput;

}
