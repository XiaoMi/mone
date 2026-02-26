package run.mone.mcp.hera.analysis.api;

/**
 * Trace查询服务Dubbo接口
 * 用于查询全量trace数据
 */
public interface ITraceQueryService {

    /**
     * 根据环境和traceId查询全量trace数据
     *
     * @param traceId 追踪ID
     * @param env     环境（staging/online）
     * @return 格式化的trace查询结果
     */
    String queryTraceData(String traceId, String env);
}
