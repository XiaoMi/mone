package com.xiaomi.mone.tpc.user;

import com.xiaomi.mone.tpc.common.vo.UserVo;

public interface UserHelper {

    UserVo register(String account, Integer userType);
}
