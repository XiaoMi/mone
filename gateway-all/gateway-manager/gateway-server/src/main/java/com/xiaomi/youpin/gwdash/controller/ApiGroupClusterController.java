package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.annotation.OperationLog;
import com.xiaomi.youpin.gwdash.bo.IdListParam;
import com.xiaomi.youpin.gwdash.bo.MetaDataParam;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.MetaDataRelationTypeEnum;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.config.EnvConfig;
import com.xiaomi.youpin.gwdash.service.ApiGroupClusterService;
import com.xiaomi.youpin.gwdash.service.GroupClusterServiceAPI;
import com.xiaomi.youpin.gwdash.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
public class ApiGroupClusterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGroupClusterController.class);
    @Autowired
    private LoginService loginService;
    @Autowired
    private ApiGroupClusterService apiGroupClusterService;
    @Reference(check = false, interfaceClass = GroupClusterServiceAPI.class, group = "${gw.intranet.group}")
    private GroupClusterServiceAPI clusterServiceAPI;
    @Autowired
    private EnvConfig envConfig;
    /**
     * 创建分组聚合apiGroupCluster
     *
     * @param param
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/apigroupcluster/new", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.ADD, exclusion = OperationLog.Column.RESULT)
    public Result<Void> newApiGroupCluster(@RequestBody MetaDataParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.newApiGroupInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiGroupClusterService.newApiGroupCluster(param);
    }

    /**
     * 更新api分组聚合 apiGroupCluster
     *
     * @param param
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/apigroupcluster/update", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.UPDATE, exclusion = OperationLog.Column.RESULT)
    public Result<Void> updateApiGroupCluster(@RequestBody MetaDataParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.newApiGroupInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiGroupClusterService.updateApiGroupCluster(param);
    }

    /**
     * 删除api分组聚合 apiGroupCluster
     *
     * @param id
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/apigroupcluster/delete", method = {RequestMethod.GET})
    @OperationLog(type = OperationLog.LogType.DEL, exclusion = OperationLog.Column.RESULT)
    public Result<Void> deleteApiGroupCluster(Integer id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.newApiGroupInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiGroupClusterService.deleteApiGroupCluster(id);
    }

    /**
     * 分页查询api分组聚合 apiGroupCluster
     *
     * @param request
     * @param response
     * @param page
     * @param pageSize
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/apigroupcluster/list", method = RequestMethod.GET)
    public Result<Map<String, Object>> getApiGroupClusterList(HttpServletRequest request,
                                                              HttpServletResponse response,
                                                              @RequestParam(value = "name",required = false) String name,
                                                              @RequestParam("page") int page,
                                                              @RequestParam("pageSize") int pageSize) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[FilterController.getApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        Map<String, Object> result = apiGroupClusterService.getApiGroupClusterList(name,page, pageSize);
        return Result.success(result);
    }

    /**
     * 根据分组聚合id，分页查询所拥有的域名domain
     *
     * @param request
     * @param response
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/apigroupcluster/listdomain", method = RequestMethod.GET)
    public Result<Map<String, Object>> getDomainListByApiGroupClusterId(HttpServletRequest request,
                                                                        HttpServletResponse response,
                                                                        @RequestParam("id") int id) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[FilterController.getApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        Map<String, Object> result = apiGroupClusterService.getDomainListByApiGroupClusterId(id);
        return Result.success(result);
    }

    /**
     * 根据分组聚合id，分页查询所拥有的域名group
     *
     * @param request
     * @param response
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/apigroupcluster/listgroup", method = RequestMethod.GET)
    public Result<Map<String, Object>> getApiGroupListByApiGroupClusterId(HttpServletRequest request,
                                                                          HttpServletResponse response,
                                                                          @RequestParam("id") int id) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[FilterController.getApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        Map<String, Object> result = apiGroupClusterService.getApiGroupByApiGroupClusterId(id);

        return Result.success(result);
    }

    /**
     * 更新域名domain到api分组聚合apiGroupCluster
     *
     * @param request
     * @param response
     * @param param
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/apigroupcluster/updatedomain", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.UPDATE, exclusion = OperationLog.Column.RESULT)
    public Result<Void> addApiGroupClusterDomain(HttpServletRequest request, HttpServletResponse response, @RequestBody IdListParam param) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[FilterController.getApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiGroupClusterService.updateMetaDataRelation(param, MetaDataRelationTypeEnum.ApiGroupCluster2Domain.getType());
    }

    /**
     * 更新api分组apiGroup到api分组聚合apiGroupCluster
     *
     * @param request
     * @param response
     * @param param
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/apigroupcluster/updategroup", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.UPDATE, exclusion = OperationLog.Column.RESULT)
    public Result<Void> addApiGroupClusterGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody IdListParam param) throws IOException {
        // IdListParam id:分组聚合id list: group id 数组
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[FilterController.getApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiGroupClusterService.updateMetaDataRelation(param, MetaDataRelationTypeEnum.ApiGroupCluster2ApiGroup.getType());
    }
}
