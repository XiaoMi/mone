package run.mone.mcp.hera.analysis.api;

/**
 * Dubbo接口QPS查询服务Dubbo接口
 */
public interface IDubboInterfaceQpsService {

    /**
     * 获取Dubbo接口在指定时间段内的QPS信息
     *
     * @param appName      应用名称
     * @param serviceName  Dubbo服务名称
     * @param methodName   Dubbo方法名称
     * @param serverZone   服务器区域
     * @param startTimeSec 开始时间戳（秒）
     * @param endTimeSec   结束时间戳（秒）
     * @return 格式化的QPS信息字符串
     */
    String getDubboInterfaceQps(String appName, String serviceName, String methodName,
                                String serverZone, long startTimeSec, long endTimeSec);
}
