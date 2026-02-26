package run.mone.mcp.gateway.api;

import java.util.List;

/**
 * Gateway Dubbo服务接口，聚合所有Gateway相关的MCP功能
 */
public interface IGatewayDubboService {

    /**
     * 查询API列表信息
     *
     * @param env          环境 (staging/online)
     * @param keyword      模糊搜索关键字
     * @param applications 应用名称列表
     * @return API列表信息
     */
    String listApiInfo(String env, String keyword, List<String> applications);

    /**
     * 根据URL获取API详细信息
     *
     * @param env 环境 (staging/online)
     * @param url API的URL
     * @return API详细信息
     */
    String detailByUrl(String env, String url);

    /**
     * 查询Filter信息
     *
     * @param env    环境 (staging/sgpStaging/online/sgpOnline/eurOnline)
     * @param tenant 租户
     * @param id     Filter ID
     * @param zhName Filter中文名称
     * @param enName Filter英文名称
     * @return Filter信息
     */
    String searchFilter(String env, String tenant, String id, String zhName, String enName);

    /**
     * 删除Filter
     *
     * @param env 环境 (staging/sgpStaging/online/sgpOnline/eurOnline)
     * @param id  Filter ID
     * @return 删除结果
     */
    String deleteFilter(String env, String id);

    /**
     * 查询使用指定Filter的API信息
     *
     * @param env    环境 (staging/sgpStaging/online/sgpOnline/eurOnline)
     * @param tenant 租户
     * @param id     Filter ID
     * @param enName Filter英文名称
     * @return 使用此Filter的API信息
     */
    String searchApiByFilter(String env, String tenant, String id, String enName);
}
