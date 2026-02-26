package run.mone.mcp.hera.analysis.api;

/**
 * Hera日志详情查询服务Dubbo接口
 */
public interface IHeraLogDetailService {

    /**
     * 查询Hera日志详情
     *
     * @param spaceId   空间ID
     * @param storeId   存储ID
     * @param input     搜索输入内容（可能包含双引号等特殊字符）
     * @param tailName  日志尾部名称
     * @param startTime 开始时间（毫秒时间戳字符串）
     * @param endTime   结束时间（毫秒时间戳字符串）
     * @param page      分页页码，从1开始
     * @param pageSize  每页大小
     * @return 格式化的日志查询结果
     */
    String queryLogDetail(int spaceId, int storeId, String input, String tailName, String startTime, String endTime, int page, int pageSize);
}
