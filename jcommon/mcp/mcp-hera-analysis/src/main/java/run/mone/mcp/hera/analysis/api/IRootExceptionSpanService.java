package run.mone.mcp.hera.analysis.api;

/**
 * 根因Span查询服务Dubbo接口
 * 用于分析trace链路，提取异常根因节点的相关信息
 */
public interface IRootExceptionSpanService {

    /**
     * 根据环境和traceId查询异常根因span信息
     *
     * @param traceId 追踪ID
     * @param env     环境（staging/online）
     * @return 格式化的根因span信息
     */
    String queryRootExceptionSpan(String traceId, String env);
}
