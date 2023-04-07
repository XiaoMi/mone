package com.xiaomi.mone.log.manager.common.context;

import com.xiaomi.mone.log.manager.convert.UserConvert;
import com.xiaomi.mone.log.manager.user.MoneUser;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/2 15:58
 */
public class MoneUserContext {

    private static ThreadLocal<MoneUser> currentUserHolder = new ThreadLocal<>();

    public static void setCurrentUser(AuthUserVo user, Boolean isAdmin) {
        MoneUser moneUser = UserConvert.INSTANCE.userAdapter(user);
        moneUser.setIsAdmin(isAdmin);
        currentUserHolder.set(moneUser);
    }

    public static MoneUser getCurrentUser() {
        return currentUserHolder.get();
    }

    public static void clear() {
        currentUserHolder.remove();
    }

}