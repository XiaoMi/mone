package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.ApiGroupInfoListDTO;
import com.xiaomi.youpin.gwdash.bo.ApiGroupInfoListResultDTO;
import com.xiaomi.youpin.gwdash.bo.GroupInfoEntityDTO;
import com.xiaomi.youpin.gwdash.bo.openApi.GwUser;

import java.util.List;
import java.util.Map;

/**
 * @author jiangzheng3
 * @version 1.0
 * @description: 分组相关API
 * @date 2022/2/28 16:58
 */
public interface GroupClusterServiceAPI {

    Map<String, Object>  getApiGroupByApiGroupClusterId(int id);

}
