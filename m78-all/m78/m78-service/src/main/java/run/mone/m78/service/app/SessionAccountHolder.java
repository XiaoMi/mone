package run.mone.m78.service.app;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import run.mone.m78.service.bo.user.BizUserInfo;

/**
 * @author wmin
 * @date 2022/3/25
 */
public class SessionAccountHolder {
    private static final String ACCOUNT = "APP_SESSION_ACCOUNT";
    public static void setAccount(BizUserInfo account){
        RequestContextHolder.currentRequestAttributes().setAttribute(ACCOUNT, account, RequestAttributes.SCOPE_REQUEST);
    }

    public static BizUserInfo getAccount(){
        return (BizUserInfo) RequestContextHolder.currentRequestAttributes().getAttribute(ACCOUNT, RequestAttributes.SCOPE_REQUEST);
    }

    public static void clearContext(){
        RequestContextHolder.currentRequestAttributes().removeAttribute(ACCOUNT, RequestAttributes.SCOPE_REQUEST);
    }

}
