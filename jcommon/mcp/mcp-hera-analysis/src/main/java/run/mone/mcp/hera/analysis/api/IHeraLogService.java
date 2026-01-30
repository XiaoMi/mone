package run.mone.mcp.hera.analysis.api;

/**
 * Hera日志服务Dubbo接口
 */
public interface IHeraLogService {

    /**
     * 通过Miline创建日志
     *
     * @param projectId 项目ID
     * @param envId     环境ID
     * @param tailName  日志尾部名称
     * @param logPath   日志路径
     * @param userName  用户名
     * @param userId    用户ID
     * @return 响应内容
     */
    String createLogByMiline(Long projectId, Long envId, String tailName, String logPath, String userName, String userId);
}
