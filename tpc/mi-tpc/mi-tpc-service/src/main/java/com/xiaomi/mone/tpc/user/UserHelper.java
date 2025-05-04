package com.xiaomi.mone.tpc.user;

import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.common.vo.UserVo;

public interface UserHelper {

    default UserVo register(String account, Integer userType) {
        return register(account, userType, null);
    }

    UserVo register(String account, Integer userType, String content);
}
