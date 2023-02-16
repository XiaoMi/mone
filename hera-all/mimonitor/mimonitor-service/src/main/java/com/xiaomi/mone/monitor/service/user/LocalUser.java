
package com.xiaomi.mone.monitor.service.user;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/1/25 14:11
 */
@Slf4j
public class LocalUser {

    private static final ThreadLocal<UseDetailInfo> local = new ThreadLocal<>();

    public static void set(UseDetailInfo user) {
        local.set(user);
    }

    public static final UseDetailInfo get() {
        return local.get();
    }

    public static void clear() {
        local.remove();
    }

    public static final Map<Integer, UseDetailInfo.DeptDescr> getDepts() {
       Map<Integer, UseDetailInfo.DeptDescr> map = Maps.newHashMap();
        UseDetailInfo user = LocalUser.get();
        log.info("debug_user_info={}", user);
        if (user == null || CollectionUtils.isEmpty(user.getFullDeptDescrList())) {
            return map;
        }
        for (UseDetailInfo.DeptDescr dept : user.getFullDeptDescrList()) {
            map.put(Integer.valueOf(dept.getLevel()), dept);
        }
       return map;
    }

}
