package run.mone.m78.server.config.auth;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.m78.api.enums.UserRoleEnum;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.M78Workspace;
import run.mone.m78.service.service.workspace.WorkspaceService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-09 14:45
 */
@Service
public class DefaultAuthHandler implements AuthStrategy {

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 检查用户在指定工作区的权限
     *
     * @param account 用户的会话账户信息
     * @param roleControl 角色控制信息
     * @param request HTTP请求对象，用于获取工作区ID
     * @return 包含状态码和消息的Pair对象，状态码为400表示权限不足或工作区ID无效，状态码为0表示权限检查通过
     */
	@Override
    public Pair<Integer, String> checkAuth(SessionAccount account, RoleControl roleControl, HttpServletRequest request) {
        // workspace下user的权限，workspaceId暂定从header中获取
        String workspaceId = request.getHeader("Workspaceid");
        if (StringUtils.isBlank(workspaceId)) {
            return Pair.of(400, "workspaceId is null");
        }

        M78Workspace byId = workspaceService.getById(workspaceId);
        if (ObjectUtils.isEmpty(byId)) {
            return Pair.of(400, "workspace is null");
        }

        if (account.isAdmin()) {
            return Pair.of(0, "");
        }

        Integer userRole = workspaceService.getWorkspaceRole(account, Long.parseLong(workspaceId));
        if (byId.getOwner().equals(account.getUsername())) {
            userRole = UserRoleEnum.OWNER.getCode();
        }

        // 查看这个用户有没有workspace下边的权限
        if (userRole < roleControl.role().getCode()) {
            return Pair.of(400, "userRole is not enough");
        }
        return Pair.of(0, "");
    }


}
