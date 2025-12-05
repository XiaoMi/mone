package run.mone.agentx.dto;

import lombok.Data;

/**
 * 调用上报分页查询请求
 * 
 * @author goodjava@qq.com
 * @date 2025/12/04
 */
@Data
public class ReportQueryRequest {
    
    /**
     * 应用名称
     */
    private String appName;
    
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
     * 类型, 1-agent, 2-mcp, 3-其他
     */
    private Integer type;
    
    /**
     * 调用方式, 1页面, 2接口, 3系统内部, 4调试等等
     */
    private Integer invokeWay;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 主机名/IP
     */
    private String host;
    
    /**
     * 开始时间（时间戳）
     */
    private Long startTime;
    
    /**
     * 结束时间（时间戳）
     */
    private Long endTime;
    
    /**
     * 页码，从1开始
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 20;
}

