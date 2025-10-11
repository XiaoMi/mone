package run.mone.mcp.hera.analysis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 应用指标监控响应模型
 *
 * @author dingtao
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationMetricsResponse {
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
    private MetricsData data;

    /**
     * 指标数据内部类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetricsData {
        /**
         * 平均服务端QPS
         */
        private double avgServerQps;
        
        /**
         * 最大服务端QPS
         */
        private double maxServerQps;
        
        /**
         * 平均值（用于CPU和Heap）
         */
        private double avg;
        
        /**
         * 最大值（用于CPU和Heap）
         */
        private double max;
        
        /**
         * 堆内存配额/最大限制（MB，仅用于Heap）
         */
        private double heapQuota;
    }
}

