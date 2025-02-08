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
import run.mone.m78.api.bo.flow.NodeInputInfo;
import run.mone.m78.api.bo.flow.NodeOutputInfo;

import java.util.List;

import static run.mone.m78.api.constant.TableConstant.FLOW_BASE_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(FLOW_BASE_TABLE)
public class FlowBasePo {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("work_space_id")
    private Long workSpaceId;

    @Column("name")
    private String name;

    @Column("avatar_url")
    private String avatarUrl;

    @Column("ctime")
    private Long ctime;

    @Column("utime")
    private Long utime;

    @Column("state")
    private Integer state;

    @Column("publish_status")
    private Integer publishStatus;

    @Column("run_status")
    private Integer runStatus;

    @Column("publish_time")
    private Long publishTime;

    @Column("user_name")
    private String userName;

    @Column("desc")
    private String desc;

    @Column(value = "inputs", typeHandler = Fastjson2TypeHandler.class)
    private List<NodeInputInfo> inputs;

    @Column(value = "outputs", typeHandler = Fastjson2TypeHandler.class)
    private List<NodeOutputInfo> outputs;

    @Column("official")
    private Integer official;
    @Column("flow_avg_star")
    private Double flowAvgStar;

}
