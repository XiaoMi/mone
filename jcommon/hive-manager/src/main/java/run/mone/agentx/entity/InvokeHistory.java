package run.mone.agentx.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_invoke_history")
public class InvokeHistory extends BaseEntity {

    private Integer type; // 类型, 1-agent, 2-mcp, 3-其他

    @Column("relate_id")
    private Long relateId;

    private String inputs;

    private String outputs;

    @Column("invoke_time")
    private Long invokeTime;

    @Column("invoke_way")
    private Integer invokeWay; // 调用方式, 1页面, 2接口, 3系统内部, 4调试，5mcp等等

    @Column("invoke_user_name")
    private String invokeUserName;

    // 新增字段，用于支持调用次数上报功能
    @Column("app_name")
    private String appName; // 应用名称
    
    @Column("business_name")
    private String businessName; // 业务名称
    
    @Column("class_name")
    private String className; // 类名
    
    @Column("method_name")
    private String methodName; // 方法名
    
    private String description; // 方法描述
    
    private Boolean success; // 是否成功
    
    @Column("error_message")
    private String errorMessage; // 错误信息
    
    @Column("execution_time")
    private Long executionTime; // 执行耗时(毫秒)
    
    private String host; // 主机名/IP
}
