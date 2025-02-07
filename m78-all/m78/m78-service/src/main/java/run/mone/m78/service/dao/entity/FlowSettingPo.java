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
import run.mone.m78.api.bo.flow.Edge;
import run.mone.m78.api.bo.flow.NodeInfo;

import java.util.List;

import static run.mone.m78.api.constant.TableConstant.FLOW_SETTING_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(FLOW_SETTING_TABLE)
public class FlowSettingPo {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("state")
    private Integer state;

    @Column("flow_base_id")
    private Integer flowBaseId;

    @Column("ctime")
    private Long ctime;

    @Column("utime")
    private Long utime;

    @Column(value = "edges", typeHandler = Fastjson2TypeHandler.class)
    private List<Edge> edges;

    @Column(value = "nodes", typeHandler = Fastjson2TypeHandler.class)
    private List<NodeInfo> nodes;

}
