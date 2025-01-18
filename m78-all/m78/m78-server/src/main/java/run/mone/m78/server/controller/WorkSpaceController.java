package run.mone.m78.server.controller;

import com.google.common.base.Preconditions;
import com.mybatisflex.core.paginate.Page;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.bo.workspace.WorkspaceInfoResp;
import run.mone.m78.api.bo.workspace.QueryWorkspace;
import run.mone.m78.api.enums.UserRoleEnum;
import run.mone.m78.server.config.auth.RoleControl;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.M78Workspace;
import run.mone.m78.service.dto.AddUserDto;
import run.mone.m78.service.dto.RoleDto;
import run.mone.m78.service.dto.UserWorkSpaceDto;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.user.LoginService;
import run.mone.m78.service.service.workspace.WorkspaceService;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_BAD_REQUEST;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

/**
 * @author caobaoyu
 * @description: 工作空间的controller，权限校验代码很糙，后边优化
 * @date 2024-03-01 16:28
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/workspace")
@HttpApiModule(value = "WorkSpaceController", apiController = WorkSpaceController.class)
public class WorkSpaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private LoginService loginService;

    @PostMapping("/create")
    @HttpApiDoc(value = "/api/v1/workspace/create", method = MiApiRequestMethod.POST, apiName = "创建工作空间")
    public Result<Long> createWorkspace(HttpServletRequest request,
                                           @HttpApiDocClassDefine(value = "workspaceName", description = "工作空间名称") @RequestParam("workspaceName") String workspaceName,
                                           @HttpApiDocClassDefine(value = "remark", description = "工作空间描述") @RequestParam("remark") String remark,
                                           @HttpApiDocClassDefine(value = "avatarUrl") @RequestParam("avatarUrl") String avatarUrl
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if("我的空间".equals(workspaceName)){
            return Result.fail(STATUS_BAD_REQUEST, "workspaceName cannot be 我的空间");
        }
        String username = account.getUsername();
        return Result.success(workspaceService.createWorkspace(account, workspaceName, remark, avatarUrl));
    }

    //修改空间，支持修改名称、描述、头像，只有（所有者和管理员）可以修改
    @PostMapping("/update")
    @RoleControl(role = UserRoleEnum.ADMIN)
    @HttpApiDoc(value = "/api/v1/workspace/update", method = MiApiRequestMethod.POST, apiName = "修改工作空间")
    public Result<Boolean> updateWorkspace(HttpServletRequest request, @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id") @RequestParam("workspaceId") Long workspaceId,
                                           @HttpApiDocClassDefine(value = "workspaceName", description = "工作空间名称") @RequestParam("workspaceName") String workspaceName,
                                           @HttpApiDocClassDefine(value = "remark", description = "工作空间描述") @RequestParam("remark") String remark,
                                           @HttpApiDocClassDefine(value = "avatarUrl") @RequestParam("avatarUrl") String avatarUrl) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if("我的空间".equals(workspaceName)){
            return Result.fail(STATUS_BAD_REQUEST, "workspaceName cannot be 我的空间");
        }
        return Result.success(workspaceService.updateWorkspace(account.getUsername(), workspaceId, workspaceName, remark, avatarUrl));

    }

    //成员管理，添加新成员，批量接口。只有（所有者和管理员）可以添加，新人默认角色都是成员
    @PostMapping("/addUser")
    @RoleControl(role = UserRoleEnum.ADMIN)
    @HttpApiDoc(value = "/api/v1/workspace/addUser", method = MiApiRequestMethod.POST, apiName = "修改工作空间成员")
    public Result<Boolean> updateWorkspaceUser(HttpServletRequest request,
                                               @HttpApiDocClassDefine(value = "usernameList", description = "用户列表") @RequestBody AddUserDto addUserDto) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        // 先粗糙的写一版
        for (String username : addUserDto.getUsername()) {
            Pair<Integer, String> updateRes = workspaceService.addWorkspaceUser(account, addUserDto.getWorkspaceId(), username, addUserDto.getRole());
            if (updateRes.getKey() != 0) {
                return Result.fail(GeneralCodes.ParamError, String.format("user :%s add error，error: %s", username, updateRes.getValue()));
            }
        }
        return Result.success(true);
    }


    //成员管理，成员列表，获取该空间下的所有成员，返回用户名、角色、加入时间，可以根据成员类型删选（所有者、管理员、成员），可以按成员名字搜索
    @PostMapping("/getUserList")
    @HttpApiDoc(value = "/api/v1/workspace/getUserList", method = MiApiRequestMethod.POST, apiName = "获取工作空间成员列表")
    public Result<List<UserWorkSpaceDto>> getUserListByWorkspace(HttpServletRequest request,
                                                                 @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id", required = true)
                                                                 @RequestParam("workspaceId") Long workspaceId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(workspaceService.getUserList(account, workspaceId));
    }

    // 转让空间，只有所有者可以转
    @PostMapping("/transfer")
    @RoleControl(role = UserRoleEnum.OWNER)
    @HttpApiDoc(value = "/api/v1/workspace/transfer", method = MiApiRequestMethod.POST, apiName = "转让工作空间")
    public Result<Boolean> transferWorkspace(HttpServletRequest request, @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id") @RequestParam("workspaceId") Long workspaceId,
                                             @HttpApiDocClassDefine(value = "username", description = "用户名") @RequestParam("username") String username) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(workspaceService.transferWorkspace(account, workspaceId, username));
    }

    //成员管理，删除成员，只有（所有者和管理员）可以删
    @PostMapping("/deleteUser")
    @RoleControl(role = UserRoleEnum.ADMIN)
    @HttpApiDoc(value = "/api/v1/workspace/deleteUser", method = MiApiRequestMethod.POST, apiName = "删除工作空间成员")
    public Result<Boolean> deleteWorkspaceUser(HttpServletRequest request, @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id") @RequestParam("workspaceId") Long workspaceId,
                                               @HttpApiDocClassDefine(value = "user", description = "用户") @RequestParam("username") String username) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        Pair<Integer, String> deleteUser = workspaceService.deleteUser(account, workspaceId, username);
        return deleteUser.getKey() == 0 ? Result.success(true) : Result.fail(GeneralCodes.ServerIsBuzy, deleteUser.getValue());
    }


    //成员管理，修改某个成员的角色，所有者可以设置管理员和成员
    @PostMapping("/updateUserRole")
    @RoleControl(role = UserRoleEnum.ADMIN)
    @HttpApiDoc(value = "/api/v1/workspace/updateUserRole", method = MiApiRequestMethod.POST, apiName = "修改工作空间成员角色")
    public Result<Boolean> updateUserWorkspaceRole(HttpServletRequest request, @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id") @RequestParam("workspaceId") Long workspaceId,
                                                   @HttpApiDocClassDefine(value = "username", description = "用户") @RequestParam("username") String username,
                                                   @HttpApiDocClassDefine(value = "role", description = "权限 0-成员 1-管理员") @RequestParam("role") Integer role) {
        Preconditions.checkArgument(UserRoleEnum.valueOfCode(role) != null, "role code error");
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        Pair<Integer, String> res = workspaceService.updateUserRole(account, workspaceId, username, UserRoleEnum.valueOfCode(role));
        return res.getKey() == 0 ? Result.success(true) : Result.fail(GeneralCodes.ServerIsBuzy, res.getValue());
    }

    @GetMapping("/list")
    @HttpApiDoc(value = "/api/v1/workspace/list", method = MiApiRequestMethod.GET, apiName = "获取用户名下所有工作空间")
    public Result<List<WorkspaceInfoResp>> listWorkspace(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(workspaceService.workspaceList(account, null));
    }

    @PostMapping("/delete")
    @RoleControl(role = UserRoleEnum.ADMIN)
    @HttpApiDoc(value = "/api/v1/workspace/delete", method = MiApiRequestMethod.POST, apiName = "删除工作空间")
    public Result<Boolean> deleteWorkspace(HttpServletRequest request, @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id") @RequestParam("workspaceId") Long workspaceId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(workspaceService.deleteWorkspace(account, workspaceId));
    }

    @GetMapping("/getUserWorkspaceRole")
    @HttpApiDoc(value = "/api/v1/workspace/getUserWorkspaceRole", method = MiApiRequestMethod.GET, apiName = "获取用户在某个工作空间的角色")
    public Result<RoleDto> getUserWorkspaceRole(HttpServletRequest request, @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id") @RequestParam("workspaceId") Long workspaceId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (account.isAdmin()) {
            RoleDto roleDto = new RoleDto();
            roleDto.setRoleCode(UserRoleEnum.OWNER.getCode());
            roleDto.setRole(UserRoleEnum.OWNER.name());
            return Result.success(roleDto);
        }
        M78Workspace workspace = workspaceService.getById(workspaceId);
        Integer workspaceRole = workspaceService.getWorkspaceRole(account, workspaceId);
        UserRoleEnum userRoleEnum = UserRoleEnum.valueOfCode(workspaceRole);

        if (workspace.getOwner().equals(account.getUsername())) {
            userRoleEnum = UserRoleEnum.OWNER;
        }
        RoleDto roleDto = new RoleDto();
        roleDto.setRoleCode(userRoleEnum.getCode());
        roleDto.setRole(userRoleEnum.name());
        return Result.success(roleDto);

    }

    // 获取“我的空间” 预留的口子
    @GetMapping("/myWorkspace")
    @HttpApiDoc(value = "/api/v1/workspace/myWorkspace", method = MiApiRequestMethod.GET, apiName = "获取我的工作空间")
    public Result<Long> getMyWorkspace(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(workspaceService.getOrCreateMyWorkspace(account));
    }
    @PostMapping("/superAdminWorkspace")
    @HttpApiDoc(value = "/api/v1/workspace/superAdminWorkspace", method = MiApiRequestMethod.GET, apiName = "超管空间")
    public Result<Page<WorkspaceInfoResp>> superAdminWorkspace(HttpServletRequest request, @RequestBody QueryWorkspace queryWorkspace ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(workspaceService.superAdminWorkspace(queryWorkspace));
    }


}
