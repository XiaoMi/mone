package run.mone.mcp.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import run.mone.mcp.gateway.api.IGatewayDubboService;
import run.mone.mcp.gateway.service.bo.ListApiInfoParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gateway Dubbo服务实现类，聚合所有Gateway相关的MCP功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DubboService(timeout = 30000, group = "${dubbo.group}", version = "1.0")
public class GatewayDubboService implements IGatewayDubboService {

    private final GatewayService gatewayService;

    /**
     * 查询API列表信息
     */
    @Override
    public String listApiInfo(String env, String keyword, List<String> applications) {
        log.info("GatewayDubboService.listApiInfo: env={}, keyword={}, applications={}", env, keyword, applications);
        try {
            ListApiInfoParam param = new ListApiInfoParam();
            param.setName(keyword);
            if (applications != null && !applications.isEmpty()) {
                param.setApplications(applications);
            }
            return gatewayService.listApiInfo(env, param);
        } catch (Exception e) {
            log.error("listApiInfo执行失败", e);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 根据URL获取API详细信息
     */
    @Override
    public String detailByUrl(String env, String url) {
        log.info("GatewayDubboService.detailByUrl: env={}, url={}", env, url);
        try {
            return gatewayService.detailByUrl(env, url);
        } catch (Exception e) {
            log.error("detailByUrl执行失败", e);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 查询Filter信息
     */
    @Override
    public String searchFilter(String env, String tenant, String id, String zhName, String enName) {
        log.info("GatewayDubboService.searchFilter: env={}, tenant={}, id={}, zhName={}, enName={}",
                env, tenant, id, zhName, enName);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("tenant", tenant != null ? tenant : "");
            params.put("id", id != null ? id : "");
            params.put("zhName", zhName != null ? zhName : "");
            params.put("enName", enName != null ? enName : "");
            return gatewayService.searchFilter(env, params);
        } catch (Exception e) {
            log.error("searchFilter执行失败", e);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 删除Filter
     */
    @Override
    public String deleteFilter(String env, String id) {
        log.info("GatewayDubboService.deleteFilter: env={}, id={}", env, id);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id != null ? id : "");
            return gatewayService.deleteFilter(env, params);
        } catch (Exception e) {
            log.error("deleteFilter执行失败", e);
            return "error: " + e.getMessage();
        }
    }

    /**
     * 查询使用指定Filter的API信息
     */
    @Override
    public String searchApiByFilter(String env, String tenant, String id, String enName) {
        log.info("GatewayDubboService.searchApiByFilter: env={}, tenant={}, id={}, enName={}",
                env, tenant, id, enName);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("tenant", tenant != null ? tenant : "");
            params.put("id", id != null ? id : "");
            params.put("enName", enName != null ? enName : "");
            return gatewayService.searchApiByFilter(env, params);
        } catch (Exception e) {
            log.error("searchApiByFilter执行失败", e);
            return "error: " + e.getMessage();
        }
    }
}
