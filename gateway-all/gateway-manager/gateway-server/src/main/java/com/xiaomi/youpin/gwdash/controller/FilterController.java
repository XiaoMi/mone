package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
import com.xiaomi.youpin.gwdash.bo.FilterInfoBo;
import com.xiaomi.youpin.gwdash.bo.FilterInfoWithoutDataBo;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.service.FilterService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.nutz.dao.Cnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.xiaomi.youpin.gwdash.common.Consts.ROLE_ADMIN;

/**
 * @author dp
 */
@RestController
@Slf4j
public class FilterController {

//    @Autowired
//    private FilterService filterService;

    @Autowired
    private LoginService loginService;

    /**
     * 展示 filter 列表
     **/
    @RequestMapping(value = "/api/filter/list", method = RequestMethod.GET)
    public Result<Map<String, Object>> getFilterList(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     @RequestParam(required = false, value = "status", defaultValue = "0") int status,
                                                     @RequestParam("page") int page,
                                                     @RequestParam("pageSize") int pageSize) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[FilterController.getApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        //Map<String, Object> result = filterService.getFilterList(page, pageSize, status);
        Map<String, Object> result = new HashMap<>();
        result.put("pluginList", null);
        result.put("total", 0);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return Result.success(result);
    }

    /**
     * 展示 filter 列表
     **/
    @RequestMapping(value = "/api/filter/effect/list", method = RequestMethod.GET)
    public List<FilterInfoWithoutDataBo> getAllEffectList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[FilterController.getAllEffectList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        //return filterService.getAllEffectList();
        return new ArrayList<>();
    }

    /**
     * 创建新的filter
     * 新建
     */
//    @RequestMapping(value = "/api/filter/new", method = RequestMethod.POST)
//    public Result<Boolean> createNewFilter(
//            HttpServletRequest request,
//            @RequestParam("group") String group,
//            @RequestParam("name") String name,
//            @RequestParam("commitId") String commitId,
//            @RequestParam(value = "domain", defaultValue = "", required = false) String domain
//    ) {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return filterService.createNewFilter(domain, account, group, name, commitId, "tesla");
//    }
//
//    /**
//     * 更新 filter (update)
//     */
//    @RequestMapping(value = "/api/filter/update", method = RequestMethod.POST)
//    public Result<Boolean> updateFilter(
//            HttpServletRequest request,
//            @RequestParam("id") int id,
//            @RequestParam("commitId") String commitId,
//            @RequestParam(value = "group", required = false) String group,
//            @RequestParam(value = "name", required = false) String name,
//            @RequestParam(value = "domain", defaultValue = "", required = false) String domain
//    ) {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return filterService.updateFilter(id, account, commitId, "tesla", domain, group, name);
//    }
//
//    @RequestMapping(value = "/api/filter/commits", method = RequestMethod.GET)
//    public Result<List<GitlabCommit>> getGitlabCommits(
//            HttpServletRequest request,
//            @RequestParam("group") String group,
//            @RequestParam("name") String name,
//            @RequestParam(value = "domain", defaultValue = "", required = false) String domain
//    ) throws GitAPIException, IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return filterService.getGitlabCommits(domain, group, name, account);
//    }
//
//    /**
//     * 删除filter
//     */
//    @RequestMapping(value = "/api/filter/del", method = RequestMethod.POST)
//    public Result<Boolean> deleteFilter(
//            HttpServletRequest request,
//            @RequestParam("id") int id
//    ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return filterService.deleteFilter(id, account);
//    }
//
//    /**
//     * 构建filter
//     * 构建
//     */
//    @RequestMapping(value = "/api/filter/build", method = RequestMethod.POST)
//    public Result<Boolean> buildFilter(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @RequestParam("id") int id
//    ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (account.getRole() != ROLE_ADMIN) {
//            log.warn("[FilterController.effect] current user is not admin");
//            response.sendError(401, "您没有该权限");
//            return null;
//        }
//        FilterInfoBo f = filterService.getFilterInfoBo(id);
//        return filterService.buildFilter(f.getGitAddress(), id, account);
//    }
//
//    /**
//     * 下载filter (download)
//     */
//    @RequestMapping(value = "/api/filter/download", method = RequestMethod.GET)
//    public ResponseEntity<byte[]> downloadFilter(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[FilterController.downloadFilter] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        return filterService.downloadFilter(id, account, response);
//    }
//
//    /**
//     * 审核通过
//     */
//    @RequestMapping(value = "/api/filter/effect", method = RequestMethod.POST)
//    public Result<Boolean> effect(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[FilterController.effect] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        if (account.getRole() != ROLE_ADMIN) {
//            log.warn("[FilterController.effect] current user is not admin");
//            response.sendError(401, "您没有该权限");
//            return null;
//        }
//
//        long accountId = account.getId();
//        return filterService.effectFilter(id, accountId, account.getUsername());
//    }
//
//    /**
//     * 审核不通过
//     */
//    @RequestMapping(value = "/api/filter/reject", method = RequestMethod.POST)
//    public Result<Boolean> reject(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[FilterController.reject] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        if (account.getRole() != ROLE_ADMIN) {
//            log.warn("[FilterController.reject] current user is not admin");
//            response.sendError(401, "您没有该权限");
//            return null;
//        }
//        long accountId = account.getId();
//        return filterService.reject(id, accountId, account.getUsername());
//    }
//
//    /**
//     * 过滤器上线
//     */
//    @RequestMapping(value = "/api/filter/online", method = RequestMethod.POST)
//    public Result<Boolean> online(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[FilterController.online] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        if (account.getRole() != ROLE_ADMIN) {
//            log.warn("[FilterController.online] current user is not admin");
//            response.sendError(401, "您没有该权限");
//            return null;
//        }
//        long accountId = account.getId();
//        return filterService.online(id, accountId);
//    }
//
//    /**
//     * 过滤器下线
//     */
//    @RequestMapping(value = "/api/filter/offline", method = RequestMethod.POST)
//    public Result<Boolean> offline(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession();
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[FilterController.offline] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        if (account.getRole() != ROLE_ADMIN) {
//            log.warn("[FilterController.delete] current user is not admin");
//            response.sendError(401, "offline");
//            return null;
//        }
//        long accountId = account.getId();
//        return filterService.offline(id, accountId);
//    }
//
//    /**
//     * redis同步filter
//     */
//    @RequestMapping(value = "/api/filter/redis/flesh", method = RequestMethod.POST)
//    public Result<Boolean> filterRedisFlesh(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession();
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[FilterController.filterRedisFlesh] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        if (account.getRole() != ROLE_ADMIN) {
//            log.warn("[FilterController.filterRedisFlesh] current user is not admin");
//            response.sendError(401, "您没有该权限");
//            return null;
//        }
//
//        return filterService.filterRedisFlesh();
//    }
//
//
//    /**
//     * 删除记录操作
//     */
//    @RequestMapping(value = "/api/filter/real/delete", method = RequestMethod.POST)
//    public Result<Boolean> realDelete(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession();
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[FilterController.realDelete] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        if (account.getRole() != ROLE_ADMIN) {
//            log.warn("[FilterController.realDelete] current user is not admin");
//            response.sendError(401, "您没有该权限");
//            return null;
//        }
//
//        long accountId = account.getId();
//        return filterService.realDelete(id, accountId);
//    }
//
//    /**
//     * filter 评分
//     */
//    @RequestMapping(value = "/api/filter/rate", method = RequestMethod.POST)
//    public Result<Boolean> rate(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id, @RequestParam("rate") double rate) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession();
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[FilterController.rate] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        int accountId = account.getId().intValue();
//        return filterService.rate(id, 1, accountId, rate);
//    }

}
