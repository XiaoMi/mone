package run.mone.m78.server.config.auth.app;


import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import run.mone.m78.common.Constant;
import run.mone.m78.service.app.SessionAccountHolder;
import run.mone.m78.service.bo.user.BizUserInfo;
import run.mone.m78.service.bo.user.CheckLoginReq;
import run.mone.m78.service.exceptions.InvalidArgumentException;
import run.mone.m78.service.service.user.UserLoginService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Aspect
@Configuration
@Slf4j
public class AopAspect {

    @Autowired
    private UserLoginService userLoginService;

    @Before(value = "@annotation(run.mone.m78.server.config.auth.app.AppPermission)")
    public void before(JoinPoint joinPoint){
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String cookie = Optional.ofNullable(request.getHeader("Cookie")).orElse(request.getHeader("cookie"));
            if (StringUtils.isBlank(cookie)) {
                log.error("cookie is null !!!");
                throw new InvalidArgumentException("cookie is null");
            }
            Map<String, String> cookieMap = Arrays.stream(cookie.split("; ")).map(c -> c.split("=")).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1], (v1, v2) -> v2));
            String m78AppId = cookieMap.get(Constant.M78_APP_ID);
            String m78Token = cookieMap.get(Constant.M78_TOKEN);
            if (StringUtils.isBlank(m78AppId) || StringUtils.isBlank(m78Token)){
                log.error("cookie is invalid !!!");
                throw new InvalidArgumentException("cookie is invalid");
            }
            Result<BizUserInfo> authRst = userLoginService.authToken(CheckLoginReq.builder().appId(Integer.parseInt(m78AppId)).token(m78Token).build());
            if (authRst.getCode() != 0 || authRst.getData() == null){
                log.error("authToken failed !!!");
                throw new InvalidArgumentException("authToken failed");
            }
            BizUserInfo bizUserInfo = BizUserInfo.builder()
                    .appId(Integer.parseInt(m78AppId))
                    .userName(authRst.getData().getUserName())
                    .build();
            SessionAccountHolder.setAccount(bizUserInfo);

        } catch (InvalidArgumentException e) {
            log.error("annotation(run.mone.m78.server.config.auth.app.AppPermission) InvalidArgument ", e);
            throw e;
        }  catch (Throwable throwable) {
            log.error("annotation(run.mone.m78.server.config.auth.app.AppPermission)", throwable);
            throw new InvalidArgumentException("权限校验失败");
        }
    }

    @After(value = "@annotation(run.mone.m78.server.config.auth.app.AppPermission)")
    public void after(JoinPoint joinPoint) {
        SessionAccountHolder.clearContext();
    }

}
