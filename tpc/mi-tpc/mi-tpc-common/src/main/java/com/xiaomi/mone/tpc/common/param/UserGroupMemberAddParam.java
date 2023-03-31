package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class UserGroupMemberAddParam extends BaseParam {

    private Long groupId;
    private Long memberId;

    @Override
    public boolean argCheck() {
       if (memberId == null) {
           return false;
       }
       if (groupId == null) {
           return false;
       }
        return true;
    }
}
