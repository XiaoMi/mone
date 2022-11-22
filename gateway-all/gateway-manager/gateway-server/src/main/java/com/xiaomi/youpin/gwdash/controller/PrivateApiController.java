package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.annotation.OperationLog;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.bo.openApi.GwGroupEntity;
import com.xiaomi.youpin.gwdash.bo.openApi.GwUser;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfo;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.*;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.hermes.entity.Group;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.gwdash.common.Consts.*;

/**
 * @author Xirui Yang (yangxirui)
 * @version 1.0
 * @since 2022/2/28
 *
 * This class does not use midun. The APIs below are called by other departments(staff: shangyanmin and dongtao).
 * username check is in APIKeyAuthFilter.
 * You can disable all APIs below through the private.api.disable field in the Nacos config.
 * See APISecurityConfig for more details of authentication.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/open/v1/private/api")
public class PrivateApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrivateApiController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApiInfoService apiInfoService;

    @Resource
    private ApiGroupInfoService groupService;

    @Autowired
    private GroupServiceApiRpc groupServiceAPI;

//    @Autowired
//    private FilterService filterService;

    @Value("${hermes.project.name}")
    private String projectName;

    @Autowired
    private UserService userService;

    private static final long DEFAULT_TIME_OUT = 6000;

//    @RequestMapping(value = "/project/getApplicationNames", method = RequestMethod.GET)
//    public Result<List<String>> getApplicationNames(HttpServletRequest request,
//                                                    @RequestParam(value = "name", required = false)
//                                                            String name,
//                                                    @RequestParam(value = "pageSize", required = false)
//                                                            Integer pageSize,
//                                                    @RequestParam(value = "pageNo", required = false)
//                                                            Integer pageNo
//    ) {
//        return this.projectService.getApplicationNames(SearchAppNameParam.builder()
//                .name(name)
//                .pageNo(pageNo)
//                .pageSize(pageSize)
//                .build());
//    }

    @RequestMapping(value = "/project/getAllApplicationNames", method = RequestMethod.GET)
    public Result<Set<String>> getApplicationNames1(HttpServletRequest request) {
        return this.projectService.getApplicationNames();
    }

    @RequestMapping(value = "/apiinfo/new", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.ADD, exclusion = OperationLog.Column.RESULT)
    public Result<Void> newApiInfo(@RequestBody ApiInfoParam param,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        param.setApiSrc(API_SRC_TIANGONG);
        LOGGER.info("private api [ApiInfoController.newApiInfo] param: {}", param);

        try {
            this.validateApiInfoParam(param);
        } catch (Exception exception) {
            return Result.fail(CommonError.InvalidParamError.getCode(), exception.getMessage());
        }
        return this.apiInfoService.newApiInfo(param,
                Optional.of(request.getHeader(SKIP_MI_DUN_USER_NAME)).orElse(""));
    }

    private void validateApiInfoParam(ApiInfoParam param) {
        if (StringUtils.isBlank(param.getApplication())) {
            throw new IllegalArgumentException("private api [ApiInfoController.newApiInfo] misses required fields like application");
        }
        if (!this.groupService.existedApiGroup(param.getGroupId())) {
            throw new IllegalArgumentException("private api [ApiInfoController.newApiInfo] invalid group id " + param.getGroupId());
        }
        if (APP_SRC_DEFAULT == Optional.ofNullable(param.getAppSrc()).orElse(0) && !this.projectService.existedAppName(param.getApplication())) {
            throw new IllegalArgumentException("private api [ApiInfoController.newApiInfo] invalid application name " + param.getApplication());
        }
    }

    @RequestMapping(value = "/apiinfo/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Object> listApiInfo(@RequestBody @Valid ListApiInfoParam param,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        if (!param.validate()) {
            throw new IllegalArgumentException("private api list api info misses required parameters");
        }
        return Result.success(this.apiInfoService.listApiInfo(param));
    }

    @RequestMapping(value = "/apiinfo/search", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Object> searchApiInfo(@RequestBody @Valid ListApiInfoParam param,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws ExecutionException, InterruptedException, TimeoutException {
        if (!param.validate()) {
            throw new IllegalArgumentException("private api list api info misses required parameters");
        }
        Map<String, Object> dict = new HashMap<>();
        CompletableFuture<List<ApiInfo>> f1 = CompletableFuture.supplyAsync(() -> this.apiInfoService.listApiInfo(param));
        CompletableFuture<Long> f2 = CompletableFuture.supplyAsync(() -> this.apiInfoService.countApiInfo(param));
        CompletableFuture.allOf(f1, f2).get(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);

        dict.put("list", f1.get());
        dict.put("count", f2.get());
        return Result.success(dict);
    }

    @RequestMapping(value = "/apiinfo/detail", method = RequestMethod.GET)
    public Result<ApiInfoDetail> getInfoDetail(@RequestParam("id") long id,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        return this.apiInfoService.getApiInfoDetail(id, Optional.of(request.getHeader(SKIP_MI_DUN_USER_NAME)).orElse(""));
    }

    private Pair<Optional<Account>, List<RoleBo>> getAccountAndRoles(HttpServletRequest request) {
        String username = Optional.of(request.getHeader(SKIP_MI_DUN_USER_NAME)).orElse("");
        CompletableFuture<GwUser> f1 = CompletableFuture.supplyAsync(() -> this.groupServiceAPI.describeUserByName(username));
        CompletableFuture<ImmutablePair<Optional<Account>, List<RoleBo>>> f2 = CompletableFuture.supplyAsync(
                        () -> userService.queryUserByName(username))
                .thenApply(account -> new ImmutablePair<>(Optional.ofNullable(account), getRolesFromAccount(account)));

        try {
            CompletableFuture.allOf(f1, f2).get(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);
            ImmutablePair<Optional<Account>, List<RoleBo>> resPair = f2.get();
            Optional<Account> accountOptional = resPair.getKey();

            if (accountOptional.isPresent() && (accountOptional.get().getGidInfos() == null || accountOptional.get().getGidInfos().isEmpty())) {
                accountOptional.get().setGidInfos(this.convertGwGroupEntities(f1.get().getGidInfos()));
            }
            return resPair;
        } catch (Exception e) {
            log.error("private api failed to get account and roles for user " + username, e);
            return new ImmutablePair<>(Optional.empty(), new ArrayList<>());
        }
    }

    private List<Group> convertGwGroupEntities(List<GwGroupEntity> srcList) {
        if (srcList == null) {
            return new ArrayList<>();
        }
        return srcList.stream().map(this::convertGwGroupEntity).collect(Collectors.toList());
    }

    private Group convertGwGroupEntity(GwGroupEntity src) {
        Group group = new Group();
        BeanUtils.copyProperties(src, group);
        return group;
    }

    private List<RoleBo> getRolesFromAccount(Account account) {
        if (account == null) {
            return new ArrayList<>();
        }
        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
        queryRoleRequest.setProjectName(this.projectName);
        queryRoleRequest.setUserName(account.getUserName());
        return this.userService.getRoleByProjectName(queryRoleRequest);
    }

    private boolean checkAdmin(List<RoleBo> roles) {
        return roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("admin")).findAny().orElse(null) != null;
    }

    @RequestMapping(value = "/apigroup/listall", method = RequestMethod.GET)
    public Result getApiGroupListAll(HttpServletRequest request,
                                      HttpServletResponse response) {
        String username = Optional.of(request.getHeader(SKIP_MI_DUN_USER_NAME)).orElse("");
        Pair<Optional<Account>, List<RoleBo>> data = this.getAccountAndRoles(request);
        return this.groupService.listApiGroupByRolesAndInfos(data.getValue(),
                this.convertGroupInfo(data.getKey().isPresent() ? data.getKey().get().getGidInfos() : new ArrayList<>()), username);
    }

    private List<GroupInfoEntity> convertGroupInfo(List<Group> groups) {
        if (groups == null) {
            return new ArrayList<>();
        }
        return groups.stream().map(this::convertGroupInfo).collect(Collectors.toList());
    }

    private GroupInfoEntity convertGroupInfo(Group group) {
        GroupInfoEntity target = new GroupInfoEntity();
        BeanUtils.copyProperties(group, target);
        return target;
    }

    @RequestMapping(value = "/filter/effect/list", method = RequestMethod.GET)
    public Result<List<FilterInfoWithoutDataBo>> getAllEffectList(HttpServletRequest request, HttpServletResponse response) {
        //return Result.success(filterService.getAllEffectList());
        return Result.success(null);
    }

    @RequestMapping(value = "/apiinfo/update", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.UPDATE, exclusion = OperationLog.Column.RESULT)
    public Result<Void> updateApiInfo(@RequestBody ApiInfoUpdateParam param,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        LOGGER.info("private api [ApiInfoController.updateApiInfo] param: {}", param);
        ApiInfo dbApiInfo = this.apiInfoService.getOneById(param.getId());

        if(dbApiInfo == null){
            return Result.fail(CommonError.InvalidIDParamError);
        }
        Pair<Optional<Account>, List<RoleBo>> data = this.getAccountAndRoles(request);
        Set<Integer> allowGids = data.getKey().isPresent() ?
                data.getKey().get().getGidInfos().stream().map(Group::getId).collect(Collectors.toSet()) : new HashSet<>();

        if (this.checkAdmin(data.getValue()) && (!allowGids.contains(param.getGroupId()) || !allowGids.contains(dbApiInfo.getGroupId()))) {
            LOGGER.error("[AccountController.updateApiInfo] not authorized to update api for this team");
            return Result.fail(CommonError.NotAuthorizedGroupError);
        }
        return this.apiInfoService.updateApiInfo(param, Optional.of(request.getHeader(SKIP_MI_DUN_USER_NAME)).orElse(""));
    }
}
