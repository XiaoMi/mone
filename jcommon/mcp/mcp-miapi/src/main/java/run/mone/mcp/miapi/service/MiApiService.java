package run.mone.mcp.miapi.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.mcp.miapi.api.IMiApiService;
import run.mone.mcp.miapi.utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * MiApi Dubbo 服务实现类
 */
@Slf4j
@Service
@DubboService(timeout = 30000, group = "${dubbo.group}", version = "1.0")
public class MiApiService implements IMiApiService {

    private static final String BASE_URL = System.getenv("gateway_host");

    @Autowired
    private HttpUtils httpUtils;

    private String checkBaseUrl() {
        if (BASE_URL == null || BASE_URL.isEmpty()) {
            return "错误：配置错误: gateway_host 环境变量未设置";
        }
        return null;
    }

    /**
     * 获取有效的用户名
     * 如果参数中的userName为空，则从Dubbo的attachment中获取
     * 优先获取 X-Authenticated-User，如果没有则获取 x-authenticated-user
     */
    private String getEffectiveUserName(String userName) {
        if (userName != null && !userName.isEmpty()) {
            return userName;
        }
        // 从Dubbo attachment中获取
        String attachmentUser = RpcContext.getContext().getAttachment("X-Authenticated-User");
        if (attachmentUser != null && !attachmentUser.isEmpty()) {
            return attachmentUser;
        }
        // 尝试小写的header名
        attachmentUser = RpcContext.getContext().getAttachment("x-authenticated-user");
        return attachmentUser;
    }

    @Override
    public String getMyProjectList(String userName) {
        String effectiveUserName = getEffectiveUserName(userName);
        log.info("getMyProjectList, userName: {}", effectiveUserName);
        String error = checkBaseUrl();
        if (error != null) {
            return error;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("userName", effectiveUserName);
            return httpUtils.request("/mtop/miapi/getMyProjectList", params, java.util.List.class);
        } catch (Throwable e) {
            log.error("getMyProjectList error", e);
            return "错误：" + e.getMessage();
        }
    }

    @Override
    public String getProjectByName(String projectName) {
        log.info("getProjectByName, projectName: {}", projectName);
        String error = checkBaseUrl();
        if (error != null) {
            return error;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("projectName", projectName);
            return httpUtils.request("/mtop/miapi/getProjectByName", params, java.util.Map.class);
        } catch (Throwable e) {
            log.error("getProjectByName error", e);
            return "错误：" + e.getMessage();
        }
    }

    @Override
    public String getIndexDetail(String indexName, String indexId, String userName) {
        String effectiveUserName = getEffectiveUserName(userName);
        log.info("getIndexDetail, indexName: {}, indexId: {}, userName: {}", indexName, indexId, effectiveUserName);
        String error = checkBaseUrl();
        if (error != null) {
            return error;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("indexName", indexName);
            params.put("indexId", indexId);
            params.put("userName", effectiveUserName);
            return httpUtils.request("/mtop/miapi/getIndexDetail", params, java.util.List.class);
        } catch (Throwable e) {
            log.error("getIndexDetail error", e);
            return "错误：" + e.getMessage();
        }
    }

    @Override
    public String getIndexList(String projectName, String userName) {
        String effectiveUserName = getEffectiveUserName(userName);
        log.info("getIndexList, projectName: {}, userName: {}", projectName, effectiveUserName);
        String error = checkBaseUrl();
        if (error != null) {
            return error;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("projectName", projectName);
            params.put("userName", effectiveUserName);
            return httpUtils.request("/mtop/miapi/getIndexList", params, java.util.List.class);
        } catch (Throwable e) {
            log.error("getIndexList error", e);
            return "错误：" + e.getMessage();
        }
    }

    @Override
    public String getApiList(String keyword, String protocol, String userName) {
        String effectiveUserName = getEffectiveUserName(userName);
        log.info("getApiList, keyword: {}, protocol: {}, userName: {}", keyword, protocol, effectiveUserName);
        String error = checkBaseUrl();
        if (error != null) {
            return error;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("keyword", keyword);
            params.put("protocol", protocol);
            params.put("userName", effectiveUserName);
            return httpUtils.request("/mtop/miapi/getApiList", params, java.util.Map.class);
        } catch (Throwable e) {
            log.error("getApiList error", e);
            return "错误：" + e.getMessage();
        }
    }

    @Override
    public String searchProjectMembers(String projectId, String userName) {
        String effectiveUserName = getEffectiveUserName(userName);
        log.info("searchProjectMembers, projectId: {}, userName: {}", projectId, effectiveUserName);
        String error = checkBaseUrl();
        if (error != null) {
            return error;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("projectId", projectId);
            params.put("userName", effectiveUserName);
            return httpUtils.request("/mtop/miapi/searchProjectMembers", params, String.class);
        } catch (Throwable e) {
            log.error("searchProjectMembers error", e);
            return "错误：" + e.getMessage();
        }
    }

    @Override
    public String checkMiApiConfig(String userName) {
        String effectiveUserName = getEffectiveUserName(userName);
        log.info("checkMiApiConfig, userName: {}", effectiveUserName);
        String error = checkBaseUrl();
        if (error != null) {
            return error;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", "check_prompt");
            params.put("userName", effectiveUserName);
            return httpUtils.request("/mtop/miapi/getConfig", params, String.class);
        } catch (Throwable e) {
            log.error("checkMiApiConfig error", e);
            return "错误：" + e.getMessage();
        }
    }

    @Override
    public String getAddMiApiConfigPrompt(String userName) {
        String effectiveUserName = getEffectiveUserName(userName);
        log.info("getAddMiApiConfigPrompt, userName: {}", effectiveUserName);
        String error = checkBaseUrl();
        if (error != null) {
            return error;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", "add_config_prompt");
            params.put("userName", effectiveUserName);
            return httpUtils.request("/mtop/miapi/getConfig", params, String.class);
        } catch (Throwable e) {
            log.error("getAddMiApiConfigPrompt error", e);
            return "错误：" + e.getMessage();
        }
    }

    @Override
    public String applyProjectAuth(String projectId, String userName) {
        String effectiveUserName = getEffectiveUserName(userName);
        log.info("applyProjectAuth, projectId: {}, userName: {}", projectId, effectiveUserName);
        String error = checkBaseUrl();
        if (error != null) {
            return error;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("projectId", projectId);
            params.put("userName", effectiveUserName);
            return httpUtils.request("/mtop/miapi/applyProjectAuth", params, Object.class);
        } catch (Throwable e) {
            log.error("applyProjectAuth error", e);
            return "错误：" + e.getMessage();
        }
    }

    @Override
    public String dubboTest(String interfaceName, String methodName, String paramType, String parameter,
                            String env, String group, String version, String attachment,
                            String timeout, String retries, String addr, String dubboTag, String userName) {
        String effectiveUserName = getEffectiveUserName(userName);
        log.info("dubboTest, interfaceName: {}, methodName: {}, env: {}", interfaceName, methodName, env);
        String error = checkBaseUrl();
        if (error != null) {
            return error;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("interfaceName", interfaceName);
            params.put("methodName", methodName);
            params.put("paramType", paramType);
            params.put("parameter", parameter);
            params.put("env", env);
            params.put("group", group);
            params.put("version", version);
            if (attachment != null) {
                params.put("attachment", attachment);
            }
            if (timeout != null) {
                params.put("timeout", timeout);
            }
            if (retries != null) {
                params.put("retries", retries);
            }
            if (addr != null) {
                params.put("addr", addr);
            }
            if (dubboTag != null) {
                params.put("dubboTag", dubboTag);
            }
            params.put("userName", effectiveUserName);
            return httpUtils.request("/mtop/miapi/dubboTestAgent", params, java.util.Map.class);
        } catch (Throwable e) {
            log.error("dubboTest error", e);
            return "错误：" + e.getMessage();
        }
    }
}
