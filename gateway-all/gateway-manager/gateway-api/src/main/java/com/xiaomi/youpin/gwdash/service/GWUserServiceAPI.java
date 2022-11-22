package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.GWAccount;

/**
 * @author jiangzheng
 * @version 1.0
 * @description:
 */
public interface GWUserServiceAPI {

    GWAccount queryGWUserByName(String name, String tenant);

    GWAccount registerAccount(String userName, String userPhone, String tenant);
}
