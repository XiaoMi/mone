package run.mone.mcp.hera.analysis.model;

import lombok.Data;

/**
 * Hera分析请求模型
 *
 * @author dingtao
 */
@Data
public class HeraAnalysisRequest {
    /**
     * 流程ID
     */
    private String flowId;
    
    /**
     * 输入参数
     */
    private Input input = new Input();
    
    /**
     * 操作命令
     */
    private String operateCmd;
    
    /**
     * 用户名
     */
    private String userName;

    /**
     * 输入参数内部类
     */
    @Data
    public static class Input {
        /**
         * 追踪ID
         */
        private String traceId;
        
        /**
         * 环境
         */
        private String env;
        
    }
} 