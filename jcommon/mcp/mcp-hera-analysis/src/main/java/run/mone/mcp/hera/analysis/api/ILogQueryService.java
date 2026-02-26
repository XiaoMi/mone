package run.mone.mcp.hera.analysis.api;

/**
 * 日志查询服务Dubbo接口
 */
public interface ILogQueryService {

    /**
     * 查询日志
     *
     * @param level     日志级别（ERROR, WARN, INFO等），可选参数
     * @param projectId 项目ID
     * @param envId     环境ID
     * @param startTime 开始时间（毫秒时间戳）
     * @param endTime   结束时间（毫秒时间戳）
     * @param traceId   链路追踪ID（32位0-9a-f组成的字符串），可选参数
     * @param logIp     机器IP或容器IP，可选参数
     * @param page      分页页码，从1开始
     * @param pageSize  每页大小
     * @return 格式化的日志查询结果
     */
    String queryLogs(String level, int projectId, int envId, long startTime, long endTime, String traceId, String logIp, int page, int pageSize);
}
