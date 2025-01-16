package run.mone.m78.server.config.auth;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import run.mone.m78.service.bo.user.SessionAccount;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-09 14:50
 */
@Component
public class AuthContext {

    @Resource
    private DefaultAuthHandler defaultAuthHandler;

    @Resource
    private BotAuthHandler botAuthHandler;

    public Pair<Integer, String> checkAuth(SessionAccount account, HttpServletRequest request, HandlerMethod handlerMethod) {
        RoleControl roleControl = handlerMethod.getMethodAnnotation(RoleControl.class);
        if (roleControl == null) {
            return Pair.of(0, "");
        }
        String strategy = roleControl.strategy();
        AuthStrategy authStrategy = null;
        switch (strategy) {
            case "bot" -> authStrategy = botAuthHandler;
            default -> authStrategy = defaultAuthHandler;
        }
        return authStrategy.checkAuth(account, roleControl, request);

    }

}
