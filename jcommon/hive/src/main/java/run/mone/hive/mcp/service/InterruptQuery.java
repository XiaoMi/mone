package run.mone.hive.mcp.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * 打断查询配置类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterruptQuery {

    /**
     * 是否启用自动打断检测
     */
    private boolean autoInterruptQuery;
    
    /**
     * 模型版本
     */
    private String version;
    
    /**
     * 模型类型
     */
    private String modelType;
    
    /**
     * 发布服务名称
     */
    private String releaseServiceName;

}
