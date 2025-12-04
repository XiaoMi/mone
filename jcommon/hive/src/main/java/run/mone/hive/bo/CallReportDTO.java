package run.mone.hive.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 调用次数上报数据传输对象
 * 
 * @author goodjava@qq.com
 * @date 2025/12/02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallReportDTO {
    
    /**
     * 应用名称
     */
    private String appName;

    private Integer type; // 类型, 1-agent, 2-mcp, 3-其他

    private Integer invokeWay; // 调用方式, 1页面, 2接口, 3系统内部, 4调试，5mcp等等
    
    /**
     * 业务名称
     */
    private String businessName;
    
    /**
     * 类名
     */
    private String className;
    
    /**
     * 方法名
     */
    private String methodName;
    
    /**
     * 方法描述
     */
    private String description;
    
    /**
     * 调用入参（JSON格式）
     */
    private String inputParams;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息（如果失败）
     */
    private String errorMessage;
    
    /**
     * 执行耗时（毫秒）
     */
    private Long executionTime;

    /**
     * 主机名/IP
     */
    private String host;

    /**
     * 创建时间
     */
    private Long createdAt;
}

