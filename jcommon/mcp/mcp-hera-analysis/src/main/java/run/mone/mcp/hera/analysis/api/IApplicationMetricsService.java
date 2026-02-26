package run.mone.mcp.hera.analysis.api;

/**
 * 应用指标监控服务Dubbo接口
 */
public interface IApplicationMetricsService {

    /**
     * 获取应用指标信息
     * 并发查询QPS、CPU和Heap三种指标
     *
     * @param application 应用名称（项目ID和项目名称的组合）
     * @return 格式化的指标信息字符串
     */
    String getApplicationMetrics(String application);
}
