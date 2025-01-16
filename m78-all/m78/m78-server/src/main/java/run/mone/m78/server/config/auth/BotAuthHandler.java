package run.mone.m78.server.config.auth;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.m78.api.enums.UserRoleEnum;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.M78Bot;
import run.mone.m78.service.service.bot.BotService;
import run.mone.m78.service.service.workspace.WorkspaceService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-09 15:08
 */
@Service
public class BotAuthHandler implements AuthStrategy {

    @Autowired
    private BotService botService;

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 检查用户的权限
     *
     * @param account 用户的会话账户信息
     * @param roleControl 角色控制信息，包含权限和角色等级
     * @param request HTTP请求对象，用于获取请求头信息
     * @return 包含状态码和消息的Pair对象，状态码为0表示权限检查通过，400表示权限检查失败
     */
	@Override
    public Pair<Integer, String> checkAuth(SessionAccount account, RoleControl roleControl, HttpServletRequest request) {
        //如果是系统admin，直接有权限
        if (account.isAdmin()) {
            return Pair.of(0, "");
        }

        String botId = request.getHeader("BotId");
        if (StringUtils.isBlank(botId)) {
            return Pair.of(400, "check auth botId is null");
        }

        M78Bot bot = botService.getById(Long.parseLong(botId));
        if (roleControl.permissions() && bot.getPermissions() == 1) {
            return Pair.of(0, "");
        }
        // 如果是私有的，判断用户是否有workspace相关的权限
        Integer userRole = workspaceService.getWorkspaceRole(account, bot.getWorkspaceId());
        if (bot.getCreator().equals(account.getUsername())) {
            userRole = UserRoleEnum.OWNER.getCode();
        }

        if (userRole < roleControl.role().getCode()) {
            return Pair.of(400, "userRole is not enough");
        }

        return Pair.of(0, "");

    }


}
