package run.mone.mcp.hera.analysis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Hera分析响应模型
 *
 * @author dingtao
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeraAnalysisResponse {
    /**
     * 响应码
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private ResponseData data;
    
    /**
     * 追踪ID
     */
    private String traceId;

    /**
     * 响应数据内部类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseData {
        /**
         * 分析结果
         */
        private Result result;
        
        /**
         * 流程记录ID
         */
        private String flowRecordId;
        
        /**
         * 流程状态
         */
        private int flowStatus;
    }

    /**
     * 分析结果内部类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        /**
         * 缓存结果
         */
        private String cacheResult;
        
        /**
         * LLM结果
         */
        private String llmResult;
    }
} 