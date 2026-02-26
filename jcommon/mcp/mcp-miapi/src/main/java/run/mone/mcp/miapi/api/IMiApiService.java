package run.mone.mcp.miapi.api;

/**
 * MiApi Dubbo 服务接口
 */
public interface IMiApiService {

    /**
     * 查询我有权限的项目列表
     *
     * @param userName 用户名
     * @return 项目列表JSON字符串
     */
    String getMyProjectList(String userName);

    /**
     * 根据项目名称查询项目信息
     *
     * @param projectName 项目名称
     * @return 项目信息JSON字符串
     */
    String getProjectByName(String projectName);

    /**
     * 查询接口集合详情
     *
     * @param indexName 集合名称
     * @param indexId   集合ID
     * @param userName  用户名
     * @return 集合详情JSON字符串
     */
    String getIndexDetail(String indexName, String indexId, String userName);

    /**
     * 查询接口集合列表
     *
     * @param projectName 项目名称
     * @param userName    用户名
     * @return 集合列表JSON字符串
     */
    String getIndexList(String projectName, String userName);

    /**
     * 根据关键字查询接口信息
     *
     * @param keyword  关键字
     * @param protocol 接口类型（http为1，dubbo为3）
     * @param userName 用户名
     * @return 接口信息JSON字符串
     */
    String getApiList(String keyword, String protocol, String userName);

    /**
     * 查询项目成员
     *
     * @param projectId 项目ID
     * @param userName  用户名
     * @return 项目成员JSON字符串
     */
    String searchProjectMembers(String projectId, String userName);

    /**
     * 检查MiApi配置
     *
     * @param userName 用户名
     * @return 检查结果
     */
    String checkMiApiConfig(String userName);

    /**
     * 获取添加MiApi配置的提示
     *
     * @param userName 用户名
     * @return 配置提示
     */
    String getAddMiApiConfigPrompt(String userName);

    /**
     * 申请项目权限
     *
     * @param projectId 项目ID
     * @param userName  用户名
     * @return 申请结果
     */
    String applyProjectAuth(String projectId, String userName);

    /**
     * Dubbo接口测试/泛化调用
     *
     * @param interfaceName dubbo服务全限定名
     * @param methodName    dubbo方法名
     * @param paramType     参数类型
     * @param parameter     参数
     * @param env           环境
     * @param group         分组
     * @param version       版本
     * @param attachment    RpcContext
     * @param timeout       超时时间
     * @param retries       重试次数
     * @param addr          指定ip:port
     * @param dubboTag      dubbo tag
     * @param userName      用户名
     * @return 调用结果
     */
    String dubboTest(String interfaceName, String methodName, String paramType, String parameter,
                     String env, String group, String version, String attachment,
                     String timeout, String retries, String addr, String dubboTag, String userName);
}
