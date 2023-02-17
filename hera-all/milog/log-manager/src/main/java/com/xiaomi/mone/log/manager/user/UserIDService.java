package com.xiaomi.mone.log.manager.user;

/**
 * @author wtt
 * @version 1.0
 * @description 获取用户唯一标识
 * @date 2021/9/7 10:48
 */
public interface UserIDService {

    String findUserId(String signature);
}
