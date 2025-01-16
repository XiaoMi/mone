package run.mone.m78.server.config.auth;

import com.google.gson.Gson;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.service.user.LoginService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-09 10:48
 */
@Component
@Slf4j
public class AuthRoleInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;

    @Autowired
    private AuthContext authContext;

    private static Gson gson = new Gson();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        String json = gson.toJson(Result.fail(GeneralCodes.NotAuthorized, "no authorized"));
        SessionAccount account = loginService.getAccountFromSession(request);
        log.info("=========AuthRoleInterceptor=========, 用户信息: {}", account);
        if (account == null) {
            log.warn("NO VALID USER INFO, WILL RESPOND WITH 401!");
            response.getWriter().write(json);
            return false;
        }
        log.info("=========执行权限验证=========");

        if (handler instanceof HandlerMethod handlerMethod) {
            Pair<Integer, String> checkAuth = authContext.checkAuth(account, request, handlerMethod);
            if (checkAuth.getKey() == 0) {
                return true;
            }
            json = gson.toJson(Result.fail(GeneralCodes.Forbidden, checkAuth.getValue()));
        } else if (handler instanceof ResourceHttpRequestHandler) {
            log.warn("warn not HandlerMethod:{}", handler.getClass());
            return true;
        } else {
            log.warn("error not HandlerMethod:{}", handler.getClass());
        }
        response.getWriter().write(json);
        return false;
    }


}
