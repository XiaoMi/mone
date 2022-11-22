package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.bo.MetaDataParam;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.service.ApiGroupClusterService;
import com.xiaomi.youpin.gwdash.service.DomainService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import lombok.extern.slf4j.Slf4j;
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
public class DomainController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private DomainService domainService;



    private static final Logger LOGGER = LoggerFactory.getLogger(DomainController.class);


    /**
     * 新增domain
     * @param param
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/domain/new",method = RequestMethod.POST,consumes = {"application/json"})
    public Result<Void> newDomain(@RequestBody MetaDataParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.newApiGroupInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return domainService.newDomain(param);
    }

    /**
     * 更新domain
     * @param param
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/domain/update",method = RequestMethod.POST,consumes = {"application/json"})
    public Result<Void> updateDomain(@RequestBody MetaDataParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.newApiGroupInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        Result<Void> result = domainService.updateDomain(param);
        return result;
    }

    /**
     * 删除domain
     * @param id
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/domain/delete",method = {RequestMethod.GET})
    public Result<Void> deleteDomain(Integer id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.newApiGroupInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return domainService.deleteDomain(id);
    }

    /**
     * 分页查询domain
     * @param request
     * @param response
     * @param page
     * @param pageSize
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/domain/list", method = RequestMethod.GET)
    public Result<Map<String, Object>> getDomainList(HttpServletRequest request,
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
        Map<String, Object> result = domainService.getDomainList(name,page,pageSize);
        return Result.success(result);
    }

    @RequestMapping(value = "/api/domain/listall", method = RequestMethod.GET)
    public Result<Map<String, Object>> getDomainListAll(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     @RequestParam(value = "name",required = false) String name) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[FilterController.getApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        Map<String, Object> result = domainService.getDomainListAll(name);
        return Result.success(result);
    }
}
