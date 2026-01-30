package run.mone.mcp.hera.analysis.api;

/**
 * Hera分析服务Dubbo接口
 */
public interface IHeraAnalysisService {

    /**
     * 根据traceId分析根本原因
     *
     * @param traceId 追踪ID
     * @param env     环境
     * @return 分析结果
     */
    String analyzeTraceRoot(String traceId, String env);
}
