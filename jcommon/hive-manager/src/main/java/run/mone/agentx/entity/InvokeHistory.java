package run.mone.agentx.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_invoke_history")
public class InvokeHistory extends BaseEntity {

    private Integer type; // 类型, 1agent

    @Column("relate_id")
    private Long relateId;

    private String inputs;

    private String outputs;

    @Column("invoke_time")
    private Long invokeTime;

    @Column("invoke_way")
    private Integer invokeWay; // 调用方式, 1页面, 2接口, 3系统内部, 4调试等等

    @Column("invoke_user_name")
    private String invokeUserName;
}
