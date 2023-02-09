package com.xiaomi.mone.tpc.login.util;

import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.MoneTpcContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:33
 */
public class UserUtil {

    private static final ThreadLocal<AuthUserVo> local = new ThreadLocal();

    public static void setUser(AuthUserVo user) {
        if (user == null) {
            return;
        }
        local.set(user);
    }

    public static final AuthUserVo getUser() {
        return local.get();
    }

    public static final MoneTpcContext getContext() {
        AuthUserVo userVo = getUser();
        if (userVo == null) {
            return null;
        }
        MoneTpcContext context = new MoneTpcContext();
        context.setAccount(userVo.getAccount());
        context.setUserType(userVo.getUserType());
        return context;
    }

    public static void clearUser() {
        local.remove();
    }

    /**
     * 生成全局唯一账号
     * @param acc
     * @param uType
     * @return
     */
    public static String getFullAccount(String acc, Integer uType) {
        if (UserTypeEnum.CAS_TYPE.getCode().equals(uType)) {
            return acc;
        }
        StringBuilder fullAccount = new StringBuilder();
        fullAccount.append(acc).append("#").append(uType);
        return fullAccount.toString();
    }

    /**
     * 解析全局唯一账号
     * @param fullAccount
     * @return
     */
    public static AuthUserVo parseFullAccount(String fullAccount) {
        if (StringUtils.isEmpty(fullAccount)) {
            return null;
        }
        AuthUserVo param = new AuthUserVo();
        int pos = fullAccount.lastIndexOf('#');
        if (pos <= 0) {
            param.setAccount(fullAccount);
            param.setUserType(UserTypeEnum.CAS_TYPE.getCode());
            return param;
        }
        try {
            Integer userType = Integer.parseInt(fullAccount.substring(pos + 1));
            UserTypeEnum userTypeEnum = UserTypeEnum.getEnum(userType);
            if (userTypeEnum != null) {
                param.setAccount(fullAccount.substring(0, pos));
                param.setUserType(userTypeEnum.getCode());
            } else {
                param.setAccount(fullAccount);
                param.setUserType(UserTypeEnum.CAS_TYPE.getCode());
            }
            return param;
        } catch (Throwable e) {
            param.setAccount(fullAccount);
            param.setUserType(UserTypeEnum.CAS_TYPE.getCode());
            return param;
        }
    }
}
