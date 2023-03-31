package com.xiaomi.mone.tpc.user;

import com.xiaomi.mone.tpc.common.vo.UserGroupVo;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/12/27 15:04
 */
public interface  UserGroupHelper {

    List<UserGroupVo> getMyUserGroupList(Long userId);

    List<Long> getMyUserGroupIds(Long userId);
}
